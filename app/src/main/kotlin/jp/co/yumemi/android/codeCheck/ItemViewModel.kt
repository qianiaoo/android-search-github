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
) : ViewModel() {

    // エラー通知用
    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> get() = _errorLiveData

    // 検索結果データ用
    private val _searchResultsLiveData = MutableLiveData<List<Item>>()
    val searchResultsLiveData: LiveData<List<Item>> get() = _searchResultsLiveData


    // 検索結果
    fun searchResults(inputText: String, languageString: String) {

        // 検索のキーワードがからの場合は検索しない
        if (inputText.isBlank()) {
            _searchResultsLiveData.postValue(emptyList())
            return
        }

        viewModelScope.launch {
            // 一時的にIOスレッドに切り替えて、itemsを取得する
            val searchResult = ItemRepository.fetchSearchResults(inputText)
            when (searchResult) {
                is SearchResult.Success -> {
                    // view層に渡されたwritten_languageでlanguageフィールドを設定
                    val updatedItems = searchResult.items.map { item ->
                        item.copy(language = languageString.format(item.language))
                    }
                    _searchResultsLiveData.postValue(updatedItems)
                }
                is SearchResult.Failure -> {
                    // 外部にエラーメッセージを通知
                    _errorLiveData.postValue(searchResult.message)
                }
            }
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