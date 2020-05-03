# Test task for Chulakov

Operations:
Operations:
<p><b>GET</b> <u>/search/users</u></p>
<p><b>GET</b> <u>/users/{login}</u> (page, per_page, q)</p>

<div style="display:flex;">
<img alt="image 1" src="documents/screenshots/user_list.jpg" width="45%">
<img alt="image 2" src="documents/screenshots/user_details.jpg" width="45%">
</div>

Search result model:

```kotlin
class SearchResults<T> (
    val total_count : Int,
    val incomplete_results : Boolean,
    val items : List<T>
)
```

<p><a href='https://chulakov.ru/career/android-razrabotchik'>Android-разработчик</a></p>
<p><a href='https://docs.google.com/document/d/1mldQnn-hJFgoAsJxkc6qK4LoZrYgrgNORgowgu89uaE/'>Работа с Github API</a></p>
