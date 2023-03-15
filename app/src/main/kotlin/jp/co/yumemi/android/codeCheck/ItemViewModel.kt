/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.codeCheck

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.parcelize.Parcelize

/**
 * TwoFragment で使う
 */
class ItemViewModel(
    private val itemRepository: ItemRepository
) : ViewModel() {

    // 内部記録用長さ可変リスト
    private val _searchResultsLiveData = MutableLiveData<List<Item>>()
    // 外部用長さ固定List
    val searchResultsLiveData: LiveData<List<Item>> get() = _searchResultsLiveData


    // 検索結果
    fun searchResults(inputText: String, languageString: String) {
        viewModelScope.launch {
            // 一時的にIOスレッドに切り替えて、itemsを取得する
            val items = itemRepository.fetchSearchResults(inputText)
            // view層に渡されたwritten_languageでlanguageフィールドを設定
            val updatedItems = items.map { item ->
                item.copy(language = languageString.format(item.language))
            }
            _searchResultsLiveData.postValue(updatedItems)
        }
    }
}

// 一つのgithub レポジトリに相当するClass
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