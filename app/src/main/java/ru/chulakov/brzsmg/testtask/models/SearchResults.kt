package ru.chulakov.brzsmg.testtask.models

/**
 * Результаты поиска на GitHub.
 */
class SearchResults<T> (
    val total_count : Int,
    val incomplete_results : Boolean,
    val items : List<T>
)