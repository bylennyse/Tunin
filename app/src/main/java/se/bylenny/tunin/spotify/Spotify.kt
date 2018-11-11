package se.bylenny.tunin.spotify

import android.content.Intent
import android.net.Uri
import io.reactivex.Single
import se.bylenny.tunin.BuildConfig
import se.bylenny.tunin.log.lazyLogger
import se.bylenny.tunin.spotify.models.SpotifySession
import java.io.IOException
import java.util.*

class Spotify {
    companion object {
        const val CLIENT_NAME = "${BuildConfig.APPLICATION_ID}-${BuildConfig.VERSION_NAME}"
        const val CLIENT_SECRET = "5c69133deb4940d8bb036e6c9319c6f6"
        const val CLIENT_ID = "38cb0aa5e6b34cbc8f358199f52a4697"
        const val REDIRECT_URI = "tunin://oauth_callback/"

        private val log by lazyLogger()

        fun isCallback(uri: Uri): Boolean = uri.scheme == "tunin" && uri.host == "oauth_callback"
    }

    private var loginState: String = UUID.randomUUID().toString()
    private val accountApi: AccountApi by lazy { AccountApi.create() }
    val api: SpotifyApi by lazy { SpotifyApi.create() }
    private var session: SpotifySession? = null
    val authorization: String
        get() = "Bearer ${session?.accessToken}"

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
                session
            }
    }
}