/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.codeCheck

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import jp.co.yumemi.android.codeCheck.databinding.FragmentDetailBinding

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val args: DetailFragmentArgs by navArgs()

    private var _binding: FragmentDetailBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("検索した日時", AppStateManager.lastSearchDate.toString())

        _binding = FragmentDetailBinding.bind(view)

        var item = args.item

        _binding!!.ownerIconView.load(item.ownerIconUrl);
        _binding!!.nameView.text = item.name;
        _binding!!.languageView.text = item.language;
        _binding!!.starsView.text = "${item.stargazersCount} stars";
        _binding!!.watchersView.text = "${item.watchersCount} watchers";
        _binding!!.forksView.text = "${item.forksCount} forks";
        _binding!!.openIssuesView.text = "${item.openIssuesCount} open issues";
    }
}
