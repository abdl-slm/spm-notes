package com.salam94.spmnotes.ui.main

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.google.android.gms.ads.AdRequest
import com.salam94.spmnotes.R
import com.salam94.spmnotes.databinding.MainFragmentBinding
import com.salam94.spmnotes.model.PastYear
import com.salam94.spmnotes.util.SP_PAST_YEAR
import com.salam94.spmnotes.util.Stash
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MainFragment : Fragment(), MainNavigator, OnLoadCompleteListener, AnimatorListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private val backUrl: ArrayList<String> = ArrayList()
    private lateinit var progressDialog: ProgressDialog
    private lateinit var pdfUrl: String

    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]

        progressDialog = ProgressDialog(context)

        backUrl.add("https://trial.spmpaper.me/")

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        context?.let {
            viewModel.getPastYearList(
                it,
                binding.recyclerPaper,
                "https://trial.spmpaper.me/",
                this,
                binding.progressCircular
            )
        }

        binding.btnClose.setOnClickListener {
            binding.webViewPast.visibility = GONE
            binding.topBarWebView.visibility = GONE
        }

        binding.refreshLayout.setOnRefreshListener {
            if (backUrl.size > 1) {
                binding.progressCircular.visibility = VISIBLE
                viewModel.getPastYearList(
                    requireContext(),
                    binding.recyclerPaper,
                    backUrl[backUrl.size - 1],
                    this, binding.progressCircular
                )

                backUrl.removeAt(backUrl.size - 1)
                binding.refreshLayout.isRefreshing = false
            } else{
                context?.let {
                    viewModel.getPastYearList(
                        it,
                        binding.recyclerPaper,
                        "https://trial.spmpaper.me/",
                        this,
                        binding.progressCircular
                    )
                }
                binding.refreshLayout.isRefreshing = false
            }
        }


        binding.btnBack.setOnClickListener {

            if (backUrl.size > 1) {
                binding.progressCircular.visibility = VISIBLE
                viewModel.getPastYearList(
                    requireContext(),
                    binding.recyclerPaper,
                    backUrl[backUrl.size - 2],
                    this, binding.progressCircular
                )

                backUrl.removeAt(backUrl.size - 1)
            }

            if(backUrl.size == 1) {
                binding.btnBack.visibility = INVISIBLE
            }
        }

        //Handle bookmarking here
        binding.btnBookmark.setOnClickListener {
            context?.let {
                val pastYearBookmarked = Stash.getArrayList<PastYear>(SP_PAST_YEAR, PastYear::class.java)
                binding.btnBookmark.setImageDrawable(
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.star_filled_24px
                    )
                )
                if(!pastYearBookmarked.contains(PastYear(binding.webViewTitle.text.toString(), pdfUrl))) {
                    pastYearBookmarked.add(PastYear(binding.webViewTitle.text.toString(), pdfUrl))
                    Stash.put(SP_PAST_YEAR, pastYearBookmarked)
                    Toast.makeText(it, "Added to bookmark.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(it, "Already bookmarked..", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun refreshRecyclerView(url: String) {
        binding.progressCircular.visibility = VISIBLE
        backUrl.add(url)
        viewModel.getPastYearList(requireContext(), binding.recyclerPaper, url, this, binding.progressCircular)

        if(backUrl.size > 1){
            binding.btnBack.visibility = VISIBLE
        } else {
            binding.btnBack.visibility = INVISIBLE
        }
    }

    override fun loadWebView(url: String, name: String) {
        this.pdfUrl = url
        binding.webViewTitle.text = name
        LoadUrl().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class LoadUrl() : AsyncTask<String?, Void?, InputStream?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            binding.topBarWebView.visibility = VISIBLE
            progressDialog.setTitle("Please wait")
            progressDialog.setMessage("Fetching PDF from server...")
            progressDialog.setCancelable(false)
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg params: String?): InputStream? {
            var inputStream: InputStream? = null
            try {
                val url = URL(pdfUrl)
                val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                inputStream = BufferedInputStream(urlConnection.inputStream)

            } catch (e: IOException) {
                return null
            }
            return inputStream
        }

        override fun onPostExecute(inputStream: InputStream?) {
            binding.webViewPast.visibility = VISIBLE
            binding.webViewPast.fromStream(inputStream)
                .onLoad(this@MainFragment)
                .load()
        }

    }

    override fun loadComplete(nbPages: Int) {
        progressDialog.dismiss()
    }

    override fun onAnimationStart(p0: Animator) {

    }

    override fun onAnimationEnd(p0: Animator) {
        binding.webViewPast.visibility = GONE
    }

    override fun onAnimationCancel(p0: Animator) {
    }

    override fun onAnimationRepeat(p0: Animator) {
    }
}