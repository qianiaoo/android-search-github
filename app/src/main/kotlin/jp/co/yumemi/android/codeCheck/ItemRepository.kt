package jp.co.yumemi.android.codeCheck

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*

// 検索結果を定義するsealed class
sealed class SearchResult {
    data class Success(val items: List<Item>) : SearchResult()
    data class Failure(val message: String) : SearchResult()
}

object ItemRepository {
    private val client = HttpClient(Android)

    const val GITHUB_API_URL = "https://api.github.com/search/repositories"

    // clientのリソースをリリースする
    fun close() = client.close()


    suspend fun fetchSearchResults(inputText: String): SearchResult {


        // IOスレッドに切り替えてAPIからのデータ取得を実行する
        return withContext(Dispatchers.IO) {
            val items = mutableListOf<Item>()
            try {
                val response: HttpResponse =
                    client.get(GITHUB_API_URL) {
                        header("Accept", "application/vnd.github.v3+json")
                        parameter("q", inputText)
                    }

                if (response.status != HttpStatusCode.OK) return@withContext SearchResult.Failure("リクエスト失敗しました。少し休憩しましょう。[${response.status}]")

                val jsonBody = JSONObject(response.receive<String>())
                val jsonItems = jsonBody.optJSONArray("items")!!
                /**
                 * アイテムの個数分ループする
                 */
                for (i in 0 until jsonItems.length()) {
                    val jsonItem = jsonItems.optJSONObject(i)!!
                    val name = jsonItem.optString("full_name")
                    val ownerIconUrl = jsonItem.optJSONObject("owner")!!.optString("avatar_url")
                    val language = jsonItem.optString("language")
                    val stargazersCount = jsonItem.optLong("stargazers_count")
                    val watchersCount = jsonItem.optLong("watchers_count")
                    val forksCount = jsonItem.optLong("forks_count")
                    val openIssuesCount = jsonItem.optLong("open_issues_count")

                    items.add(
                        Item(
                            name = name,
                            ownerIconUrl = ownerIconUrl,
                            language = language,
                            stargazersCount = stargazersCount,
                            watchersCount = watchersCount,
                            forksCount = forksCount,
                            openIssuesCount = openIssuesCount
                        )
                    )
                }
                AppStateManager.lastSearchDate = Date()

                SearchResult.Success(items)

            } catch (e: Exception) {
                SearchResult.Failure("エラーが発生しました。@qianiaooまで連絡してください。：${e.message}")
            }
        }
    }
}