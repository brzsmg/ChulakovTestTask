package ru.chulakov.brzsmg.testtask.models

import com.sebbia.brzsmg.testtask.types.DateTime
import java.io.Serializable

/**
 * Пользователь GitHub.
 */
class User (
    val id : Int,
    val type : String?,
    val login : String,
    val name : String?,
    val avatar_url : String?,
    val url : String?,
    val html_url : String,
    val events_url : String?,
    val received_events_url : String?,
    val starred_url : String?,
    val followers_url : String?,
    val organizations_url : String?,
    val subscriptions_url : String?,
    val followers : Int?, //Double
    val following : Int?, //Double
    val blog : String?,
    val plan : Plan?,
    val email : String?,
    val created_at : DateTime?,
    val updated_at : DateTime?,
    val bio : String?,
    val following_url : String?,
    val two_factor_authentication : Boolean?,
    val repos_url : String?,
    val gists_url : String?,
    val private_gists : Int, //Double
    val location : Any?,
    val node_id : String?,
    val company : Any?,
    val public_gists : Int?, //Double
    val hireable : Any?,
    val gravatar_id : String?,
    val public_repos : Int?, //Double
    val total_private_repos : Int?, //Double
    val site_admin : Boolean?,
    val disk_usage : Int?, //Double
    val collaborators : Int?, //Double
    val owned_private_repos : Int? //Double
) : Serializable