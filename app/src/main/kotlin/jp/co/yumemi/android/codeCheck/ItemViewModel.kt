/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.codeCheck

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jp.co.yumemi.android.codeCheck.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.*
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.util.*

/**
 * TwoFragment で使う
 */
class ItemViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {


    private val _searchResultsLiveData = MutableLiveData<List<Item>>()
    val searchResultsLiveData: LiveData<List<Item>> get() = _searchResultsLiveData


    // 検索結果
    fun searchResults(inputText: String, languageString: String) {
        viewModelScope.launch {
            val items = itemRepository.fetchSearchResults(inputText)
            // view層に渡されたwritten_languageでlanguageフィールドを設定
            val updatedItems = items.map { item ->
                item.copy(language = languageString.format(item.language))
            }
            _searchResultsLiveData.postValue(updatedItems)
        }
    }


}

@Parcelize
data class Item(
    val name: String,
    val ownerIconUrl: String,
    val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
) : Parcelable