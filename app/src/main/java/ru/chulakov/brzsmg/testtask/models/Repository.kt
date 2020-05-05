package ru.chulakov.brzsmg.testtask.models

import com.sebbia.brzsmg.testtask.types.DateTime

class Repository (
    val id : Integer,
    val name : String?,
    val owner : User?,
    val fork : Boolean?,
    val forks : Int?,
    val forks_count : Int?,
    val open_issues : Int?,
    val open_issues_count : Int?,
    val archive_url : String?,
    val created_at : DateTime?,
    val updated_at : DateTime?,
    val default_branch : String?,
    val deployments_url : String?,
    val description : String?,
    val downloads_url : String?,
    val events_url : String?,
    val forks_url : String?,
    val full_name : String?,
    val hooks_url : String?,
    val html_url : String?,
    val url : String?,
    val ssh_url : String?,
    val notifications_url : String?,
    val permissions : Permissions?,
    val private : Boolean?,
    val stargazers_count : Int?,
    val watchers : Int?,
    val watchers_count : Int?
)