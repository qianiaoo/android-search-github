/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.codeCheck

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        viewModel = ItemViewModel(requireContext())

        val layoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), layoutManager.orientation)
        val adapter = CustomAdapter(object : CustomAdapter.OnItemClickListener {
            override fun itemClick(item: Item) {
                gotoRepositoryFragment(item)
            }
        })


        fun handleSearchAction(editText: TextView, action: Int): Boolean {
            if (action == EditorInfo.IME_ACTION_SEARCH) {
                editText.text.toString().let {
                    viewModel.searchResults(it)
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

val diffUtil = object : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        return oldItem == newItem
    }

}

class CustomAdapter(
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<Item, CustomAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val repositoryNameView: TextView = view.findViewById(R.id.repositoryNameView)
    }

    interface OnItemClickListener {
        fun itemClick(item: Item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.repositoryNameView.text = item.name

        holder.itemView.setOnClickListener {
            itemClickListener.itemClick(item)
        }
    }
}
