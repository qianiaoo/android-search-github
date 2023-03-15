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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("検索した日時", AppStateManager.lastSearchDate?.toString() ?: "未検索")

        // viewと紐付け
        val binding = FragmentDetailBinding.bind(view)

        // navからitemを取得(nav_graph)
        val item = args.item

        // viewにデータを表示
        binding.ownerIconView.load(item.ownerIconUrl)
        binding.nameView.text = item.name
        binding.languageView.text = item.language
        binding.starsView.text = getString(R.string.stars_format, item.stargazersCount)
        binding.watchersView.text = getString(R.string.watchers_format, item.watchersCount)
        binding.forksView.text = getString(R.string.forks_format, item.forksCount)
        binding.openIssuesView.text = getString(R.string.open_issues_format, item.openIssuesCount)
    }
}
