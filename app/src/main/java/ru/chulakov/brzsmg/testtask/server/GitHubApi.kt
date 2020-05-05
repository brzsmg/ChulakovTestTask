package ru.chulakov.brzsmg.testtask.server

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.chulakov.brzsmg.testtask.models.Repository
import ru.chulakov.brzsmg.testtask.models.SearchResults
import ru.chulakov.brzsmg.testtask.models.User

/**
 * GitHub
 */
interface GitHubApi {
    @GET("/user")
    fun requestUser() : Single<Response<User>>

    @GET("/users")
    fun requestUsers(
        @Query("page") page : Int,
        @Query("per_page") per_page : Int
    ) : Single<Response<List<User>>>

    @GET("/users/{login}")
    fun requestUser(@Path("login") login : String) : Single<Response<User>>

    @GET("/search/users")
    fun requestSearchUsers(
        @Query("page") page : Int,
        @Query("per_page") per_page : Int,
        @Query("q") q : String
    ) : Single<Response<SearchResults<User>>>

    @GET("/user/repos")
    fun requestRepository() : Single<Response<List<Repository>>>
}