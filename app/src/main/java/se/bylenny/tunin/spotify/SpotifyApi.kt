package se.bylenny.tunin.spotify

import com.squareup.moshi.Moshi
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query
import se.bylenny.tunin.log.loggingInterceptor
import se.bylenny.tunin.spotify.models.SpotifyList
import java.util.concurrent.TimeUnit


interface SpotifyApi {

    @GET("/v1/search")
    @Headers(value = [
        "Accept: application/json",
        "Content-Type: application/json"
    ])
    fun search(
        @Header("Authorization") authorization: String,
        @Query("q") query: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 50,
        @Query("type") type: String = "album,artist,track"
    ): Single<Response<SpotifyList>>

    companion object {
        fun create(moshi: Moshi, client: OkHttpClient): SpotifyApi {
            val clazz = SpotifyApi::class.java

            return Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client.newBuilder()
                    .addNetworkInterceptor(loggingInterceptor(clazz))
                    .build())
                .build()
                .create(clazz)
        }
    }
}
