package se.bylenny.tunin.injection

import android.app.Application
import android.content.Context
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import se.bylenny.tunin.R
import se.bylenny.tunin.list.InflatorFactory
import se.bylenny.tunin.list.SpotifyListAdapter
import se.bylenny.tunin.list.album.AlbumViewHolder
import se.bylenny.tunin.list.artist.ArtistViewHolder
import se.bylenny.tunin.list.title.TitleViewHolder
import se.bylenny.tunin.list.track.TrackViewHolder
import se.bylenny.tunin.main.MainPresenter
import se.bylenny.tunin.main.MainPresenterImpl
import se.bylenny.tunin.persist.SharedPreferencesPersistantStorage
import se.bylenny.tunin.spotify.AccountApi
import se.bylenny.tunin.spotify.Spotify
import se.bylenny.tunin.spotify.SpotifyApi
import se.bylenny.tunin.spotify.models.SpotifySession
import java.util.concurrent.TimeUnit

@Module
class AppModule(private val app: Application) {

    @Provides
    fun context(): Context = app

    @Provides
    fun moshi(): Moshi = Moshi.Builder().build()

    @Provides
    fun spotify(
        context: Context,
        moshi: Moshi,
        accountApi: AccountApi,
        spotifyApi: SpotifyApi
    ): Spotify {
        val type = SpotifySession::class.java
        val adapter = moshi.adapter(type)
        val prefs = context.getSharedPreferences("spotify", Context.MODE_PRIVATE)
        return Spotify(SharedPreferencesPersistantStorage(adapter, type, prefs), accountApi, spotifyApi)
    }

    @Provides
    fun adapter(): SpotifyListAdapter = SpotifyListAdapter(
        "track" to InflatorFactory(R.layout.list_item_track) { TrackViewHolder(it) },
        "artist" to InflatorFactory(R.layout.list_item_artist) { ArtistViewHolder(it) },
        "album" to InflatorFactory(R.layout.list_item_album) { AlbumViewHolder(it) },
        "title" to InflatorFactory(R.layout.list_item_title) { TitleViewHolder(it) }
    )

    @Provides
    fun client(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    fun accountApi(moshi: Moshi, client: OkHttpClient): AccountApi =
        AccountApi.create(moshi, client)

    @Provides
    fun spotifyApi(moshi: Moshi, client: OkHttpClient): SpotifyApi =
        SpotifyApi.create(moshi, client)

    @Provides
    fun mainPresenter(spotify: Spotify): MainPresenter =
        MainPresenterImpl(spotify)
}