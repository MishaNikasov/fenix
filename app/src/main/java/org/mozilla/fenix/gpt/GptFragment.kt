/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.gpt

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.launch
import org.mozilla.fenix.R
import org.mozilla.fenix.databinding.FragmentGptBinding
import org.mozilla.fenix.ext.requireComponents
import org.mozilla.fenix.ext.showToolbar

class GptFragment: Fragment(R.layout.fragment_gpt) {

    lateinit var binding: FragmentGptBinding

    val args by navArgs<GptFragmentArgs>()

    override fun onResume() {
        super.onResume()
        showToolbar(getString(R.string.summary))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGptBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            val message = requireComponents.useCases.analyzePageUseCase(args.pageUrl)
            binding.loader.isVisible = false
            binding.text.text = message
        }
    }

}
