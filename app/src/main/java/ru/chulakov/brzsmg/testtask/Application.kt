package ru.chulakov.brzsmg.testtask

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.chulakov.brzsmg.testtask.server.GitHubApi
import java.util.concurrent.TimeUnit

val AppCompatActivity.app : Application
    get() = this.application as Application

val Fragment.app : Application
    get() = this.activity?.application as Application

/**
 * Application
 */
class Application : android.app.Application() {

    private lateinit var mGitHubApi : GitHubApi

    private var hsHttpClient: OkHttpClient? = null

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        val auth_login = "login"
        val auth_password = "unknown"
        hsHttpClient = OkHttpClient.Builder()
            /*.authenticator { route, response ->
                response
                    .request()
                    .newBuilder()
                    .header("Authorization", Credentials.basic(auth_login, auth_password))
                    .build()
            }*/
            .addNetworkInterceptor(StethoInterceptor())
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(4, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
        val retrofit = Retrofit.Builder()
            .client(hsHttpClient)
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create(Json.gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        mGitHubApi = retrofit.create(GitHubApi::class.java)
    }

    val gitHubApi: GitHubApi
        get() {
            return mGitHubApi
        }
}