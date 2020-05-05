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
import ru.chulakov.brzsmg.testtask.utils.Json
import java.util.concurrent.TimeUnit

val AppCompatActivity.app : Application
    get() = this.application as Application

val Fragment.app : Application
    get() = this.activity?.application as Application

/**
 * Application
 */
class Application : android.app.Application() {

    companion object {
        private lateinit var mInstance : Application
        fun getInstance(): Application {
            return mInstance
        }
    }

    private lateinit var mHsHttpClient: OkHttpClient
    private lateinit var mGitHubApi : GitHubApi

    override fun onCreate() {
        mInstance = this
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        mHsHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .connectTimeout(4, TimeUnit.SECONDS)
            .readTimeout(4, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
        val retrofit = Retrofit.Builder()
            .client(mHsHttpClient)
            .baseUrl("https://api.github.com")
            .addConverterFactory(GsonConverterFactory.create(Json.gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        mGitHubApi = retrofit.create(GitHubApi::class.java)
    }

    val hsHttpClient: OkHttpClient
        get() {
            return mHsHttpClient
        }

    val gitHubApi: GitHubApi
        get() {
            return mGitHubApi
        }

}