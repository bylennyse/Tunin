package se.bylenny.tunin.spotify

import android.content.Intent
import android.net.Uri
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import se.bylenny.tunin.list.ListItem
import se.bylenny.tunin.list.album.AlbumListItem
import se.bylenny.tunin.list.artist.ArtistListItem
import se.bylenny.tunin.list.title.TitleListItem
import se.bylenny.tunin.list.track.TrackListItem
import se.bylenny.tunin.log.lazyLogger
import se.bylenny.tunin.persist.PersistantStorage
import se.bylenny.tunin.spotify.models.Item
import se.bylenny.tunin.spotify.models.SpotifyList
import se.bylenny.tunin.spotify.models.SpotifySession
import java.io.IOException
import java.util.*

class Spotify(
    private val storage: PersistantStorage<SpotifySession>
) {
    companion object {
        const val CLIENT_SECRET = "5c69133deb4940d8bb036e6c9319c6f6"
        const val CLIENT_ID = "38cb0aa5e6b34cbc8f358199f52a4697"
        const val REDIRECT_URI = "tunin://oauth_callback/"

        private val log by lazyLogger()

        fun isCallback(uri: Uri): Boolean = uri.scheme == "tunin" && uri.host == "oauth_callback"
    }

    private var loginState: String = UUID.randomUUID().toString()
    private val accountApi: AccountApi by lazy { AccountApi.create() }
    private val api: SpotifyApi by lazy { SpotifyApi.create() }
    private var session: SpotifySession? = storage.restore()
    private val authorization: String
        get() = "Bearer ${session?.accessToken}"
    val hasSession: Boolean
        get() = session != null

    init {
        refreshTokens()
    }

    private fun refreshTokens() {
        session?.refreshToken?.let { refreshToken ->
            accountApi.refreshTokens(refreshToken)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map { response ->
                    if (!response.isSuccessful) {
                        throw IOException("${response.raw().request().url()} => ${response.errorBody()}")
                    }
                    val session = response.body()!!
                    this@Spotify.session = session
                    storage.store(session)
                    session
                }
                .subscribe({
                    log.debug("$it")
                }, {
                    storage.clear()
                    session = null
                })
        }
    }

    /**
     * Step 1 in flow
     * [https://developer.spotify.com/documentation/general/guides/authorization-guide/#authorization-code-flow]
     */
    fun createAuthorizationIntent(): Intent = Intent(Intent.ACTION_VIEW).apply {
        loginState = UUID.randomUUID().toString()
        data = Uri.parse("https://accounts.spotify.com/authorize")
            .buildUpon()
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter("client_id", Spotify.CLIENT_ID)
            .appendQueryParameter("scope", "")
            .appendQueryParameter("state", loginState)
            .appendQueryParameter("redirect_uri", Spotify.REDIRECT_URI)
            .build()
    }

    fun createOpenIntent(item: ListItem): Intent =
        Intent(Intent.ACTION_VIEW, Uri.parse(item.uri))

    /**
     * Step 2 in flow
     * [https://developer.spotify.com/documentation/general/guides/authorization-guide/#2-have-your-application-request-refresh-and-access-tokens-spotify-returns-access-and-refresh-tokens]
     */
    fun handleCallback(uri: Uri): Single<SpotifySession> {
        log.debug("handleCallback $uri")

        val code: String? = uri.getQueryParameter("code")
        val state: String? = uri.getQueryParameter("state")
        val error: String? = uri.getQueryParameter("error")

        if (error != null) {
            return Single.error(IOException(error))
        }

        if (state != loginState) {
            return Single.error(IOException("$state != $loginState"))
        }

        if (code == null) {
            return Single.error(IOException("Missing code"))
        }

        return accountApi.requestTokens(code)
            .map { response ->
                if (!response.isSuccessful) {
                    throw IOException("${response.raw().request().url()} => ${response.errorBody()}")
                }
                val session = response.body()!!
                this@Spotify.session = session
                storage.store(session)
                session
            }
    }

    fun search(query: String): Single<List<ListItem>> {
        return api.search(authorization, query)
            .map {
                if (!it.isSuccessful) {
                    if (it.code() == 401) {
                        session = null
                        storage.clear()
                    }
                }
                val items = it.body() ?: throw IOException("${it.errorBody()?.string()}")
                convertToList(items)
            }
    }

    private fun convertToList(input: SpotifyList): List<ListItem> {
        val tracksTitle = listOf(TitleListItem("tracks"))
        val tracks: List<ListItem> = input.tracks?.items?.map { convertToTrackItem(it) } ?: emptyList()
        val artistsTitle = listOf(TitleListItem("artists"))
        val artists: List<ListItem> = input.artists?.items?.map { convertToArtistItem(it) } ?: emptyList()
        val albumsTitle = listOf(TitleListItem("albums"))
        val albums: List<ListItem> = input.albums?.items?.map { convertToAlbumItem(it) } ?: emptyList()
        return tracksTitle.plus(tracks).plus(artistsTitle).plus(artists).plus(albumsTitle).plus(albums)
    }

    private fun convertToTrackItem(item: Item): ListItem {
        return TrackListItem(
            item.uri,
            item.name,
            item.trackNumber,
            item.discNumber,
            item.album?.name,
            item.artists.mapNotNull { it.name },
            item.album?.images?.firstOrNull()?.url
        )
    }

    private fun convertToArtistItem(item: Item): ListItem {
        return ArtistListItem(
            item.uri,
            item.name,
            item.images.firstOrNull()?.url
        )
    }

    private fun convertToAlbumItem(item: Item): ListItem {
        return AlbumListItem(
            item.uri,
            item.name,
            item.images.firstOrNull()?.url
        )
    }
}