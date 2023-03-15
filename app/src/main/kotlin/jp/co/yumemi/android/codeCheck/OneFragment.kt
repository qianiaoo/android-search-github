/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.codeCheck

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import jp.co.yumemi.android.codeCheck.databinding.FragmentOneBinding

class OneFragment : Fragment(R.layout.fragment_one) {
    private lateinit var viewModel: ItemViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentOneBinding.bind(view)
        val itemRepository = ItemRepository()
        viewModel = ItemViewModel(itemRepository)

        val layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        val adapter = ItemAdapter(object : ItemAdapter.OnItemClickListener {
            override fun itemClick(item: Item) {
                gotoRepositoryFragment(item)
            }
        })


        fun handleSearchAction(editText: TextView, action: Int): Boolean {
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                editText.text.toString().let {
                    val languageString = getString(R.string.written_language)
                    viewModel.searchResults(it, languageString)
                }
                return true
            }
            return false
        }

        binding.searchInputText
            .setOnEditorActionListener { editText, action, _ ->
                handleSearchAction(editText, action)
            }

        binding.recyclerView.also {
            it.layoutManager = layoutManager
            it.addItemDecoration(dividerItemDecoration)
            it.adapter = adapter
        }

        viewModel.searchResultsLiveData.observe(viewLifecycleOwner, Observer { results ->
            adapter.submitList(results)
        })
    }


    fun gotoRepositoryFragment(item: Item) {
        val action = OneFragmentDirections
            .actionRepositoriesFragmentToRepositoryFragment(item = item)
        findNavController().navigate(action)
    }
}

