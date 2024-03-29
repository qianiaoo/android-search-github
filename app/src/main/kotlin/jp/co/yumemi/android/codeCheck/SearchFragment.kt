/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.codeCheck

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import jp.co.yumemi.android.codeCheck.databinding.FragmentSearchBinding
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment(R.layout.fragment_search) {
    // activity-ktxを使ってviewModelを初期化する
    private val viewModel: ItemViewModel by viewModels {
        ViewModelProvider.NewInstanceFactory()
    }

    @Volatile
    private var searchJob: Job? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // viewと紐付ける
        val binding = FragmentSearchBinding.bind(view)

        val layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        // lambdaでclickListener関数を渡す
        val adapter = ItemAdapter {
            gotoRepositoryFragment(it) // 次のページに行く
        }

        // エラーメッセージを観察する。lifeCycleはonCreateViewからonDestroyViewまで指定
        viewModel.errorLiveData.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }

        // inputに検索動作用TextWatcherを設定する、毎回inputが変わる時にデータを取得する
        binding.searchInputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val inputText = it.toString()
                    val languageString = getString(R.string.written_language)

                    // ユーザーが速くて連続入力する場合は前のクエリをキャンセルする
                    searchJob?.cancel()
                    val delayTime = 200L
                    // ユーザーがすぐにまた新しいのを入力する可能性があるので、0.2秒後データを取得するように
                    searchJob = lifecycleScope.launch {
                        delay(delayTime) // 200ms後実行
                        viewModel.searchResults(inputText, languageString)
                    }
                }
            }
        })

        // recyclerViewを設定する
        binding.recyclerView.also {
            it.layoutManager = layoutManager
            it.addItemDecoration(dividerItemDecoration)
            it.adapter = adapter
        }

        // LiveDataのデータの変化を観察して、view(adapter)に更新する
        viewModel.searchResultsLiveData.observe(viewLifecycleOwner) { results ->
            adapter.submitList(results)
        }
    }


    // RepositoryFragmentへ飛ぶ関数
    private fun gotoRepositoryFragment(item: Item) {
        val action =
            SearchFragmentDirections.actionRepositoriesFragmentToRepositoryFragment(item = item) // navにitemというkeyにitemを設定(nav_graph.xml)
        findNavController().navigate(action)
    }
}

