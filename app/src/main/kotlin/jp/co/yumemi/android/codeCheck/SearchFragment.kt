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

class SearchFragment : Fragment(R.layout.fragment_search) {
    private lateinit var viewModel: ItemViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // viewと紐付ける
        val binding = FragmentSearchBinding.bind(view)
        // ItemRepositoryでItemViewModalを初期化する
        viewModel = ItemViewModel()

        val layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        // lambdaでclickListener関数を渡す
        val adapter = ItemAdapter {
            gotoRepositoryFragment(it) // 次のページに行く
        }


        // inputに検索動作用TextWatcherを設定する、毎回inputが変わる時にデータを取得する
        binding.searchInputText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    val searchText = it.toString()
                    val languageString = getString(R.string.written_language)
                    viewModel.searchResults(searchText, languageString)
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
            SearchFragmentDirections.actionRepositoriesFragmentToRepositoryFragment(item = item)
        findNavController().navigate(action)
    }
}

