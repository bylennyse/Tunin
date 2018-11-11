package se.bylenny.tunin.spotify

import com.squareup.moshi.Moshi
import io.reactivex.Single
import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import se.bylenny.tunin.log.loggingInterceptor
import se.bylenny.tunin.spotify.models.SpotifySession
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit


interface AccountApi {

    /**
     * [https://developer.spotify.com/documentation/general/guides/authorization-guide/#2-have-your-application-request-refresh-and-access-tokens-spotify-returns-access-and-refresh-tokens]
     */
    @FormUrlEncoded
    @POST("/api/token")
    @Headers(value = [
        "Accept: application/json"
    ])
    fun requestTokens(
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String = Spotify.REDIRECT_URI,
        @Field("grant_type") grantType: String = "authorization_code",
        @Header("Authorization") authorization: String = Credentials.basic(
            Spotify.CLIENT_ID,
            Spotify.CLIENT_SECRET,
            Charset.forName("UTF-8")
        )
    ): Single<Response<SpotifySession>>

    /**
     * [https://developer.spotify.com/documentation/general/guides/authorization-guide/#4-requesting-a-refreshed-access-token-spotify-returns-a-new-access-token-to-your-app]
     */
    @FormUrlEncoded
    @POST("/api/token")
    @Headers(value = [
        "Accept: application/json"
    ])
    fun refreshTokens(
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token",
        @Header("Authorization") authorization: String = Credentials.basic(
            Spotify.CLIENT_ID,
            Spotify.CLIENT_SECRET,
            Charset.forName("UTF-8")
        )
    ): Single<Response<SpotifySession>>

    companion object {
        fun create(): AccountApi {
            val clazz = AccountApi::class.java

            val moshi = Moshi.Builder()
                .build()

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .addNetworkInterceptor(loggingInterceptor(clazz))
                .build()

            return Retrofit.Builder()
                .baseUrl("https://accounts.spotify.com/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build()
                .create(clazz)
        }
    }
}
