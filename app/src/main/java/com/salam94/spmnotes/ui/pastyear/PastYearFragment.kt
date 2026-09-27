package com.salam94.spmnotes.ui.pastyear

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.google.android.gms.ads.AdRequest
import com.salam94.spmnotes.databinding.PastYearFragmentBinding
import com.salam94.spmnotes.model.PastYear
import com.salam94.spmnotes.util.Stash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class PastYearFragment : Fragment(), PastYearNavigator, OnLoadCompleteListener {

    companion object {
        fun newInstance() = PastYearFragment()
        private const val TAG = "PastYearFragment"
        private const val STASH_BOOKMARKS_KEY = "past_year"
    }

    // Modern ViewModel initialization
    private val viewModel: PastYearViewModel by viewModels()

    // View Binding memory-leak safe pattern
    private var _binding: PastYearFragmentBinding? = null
    private val binding get() = _binding!!

    private var currentPdfUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PastYearFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdView()
        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupAdView() {
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun setupRecyclerView() {
        binding.recyclerNote.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = viewModel.loadNotes(this@PastYearFragment)
        }
    }

    private fun setupClickListeners() {
        binding.btnCloseNote.setOnClickListener {
            binding.webViewNote.isVisible = false
            binding.topBarWebViewNote.isVisible = false
        }

        binding.btnBookmark.setOnClickListener {
            currentPdfUrl?.let { url ->
                saveBookmark(url)
            }
        }
    }

    override fun loadWebView(url: String) {
        this.currentPdfUrl = url
        downloadAndDisplayPdf(url)
    }

    private fun downloadAndDisplayPdf(pdfUrl: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            showLoading(true)

            val inputStream = fetchPdfInputStream(pdfUrl)

            if (inputStream != null) {
                binding.webViewNote.isVisible = true
                binding.webViewNote.fromStream(inputStream)
                    .onLoad(this@PastYearFragment)
                    .load()
            } else {
                showLoading(false)
                Toast.makeText(context, "Failed to download PDF stream from $pdfUrl", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun fetchPdfInputStream(urlString: String): InputStream? =
        withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 10_000
                urlConnection.readTimeout = 15_000
                BufferedInputStream(urlConnection.inputStream)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching PDF", e)
                null
            }
        }

    private fun showLoading(isLoading: Boolean) {
        binding.topBarWebViewNote.isVisible = isLoading
    }

    override fun loadComplete(nbPages: Int) {
        showLoading(false)
    }

    private fun saveBookmark(url: String) {
        val bookmarks = Stash.getArrayList<PastYear>(STASH_BOOKMARKS_KEY, PastYear::class.java)
        bookmarks.add(PastYear(name = "Past Year Note", link = url))
        Stash.put(STASH_BOOKMARKS_KEY, bookmarks)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}