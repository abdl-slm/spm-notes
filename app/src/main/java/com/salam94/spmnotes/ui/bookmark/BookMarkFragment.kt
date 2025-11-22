package com.salam94.spmnotes.ui.bookmark

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.google.android.gms.ads.AdRequest
import com.salam94.spmnotes.R
import com.salam94.spmnotes.adapter.PastYearAdapter
import com.salam94.spmnotes.databinding.FragmentBookMarkBinding
import com.salam94.spmnotes.model.PastYear
import com.salam94.spmnotes.ui.main.MainNavigator
import com.salam94.spmnotes.util.SP_PAST_YEAR
import com.salam94.spmnotes.util.Stash
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class BookMarkFragment : Fragment(), MainNavigator, BookMarkNavigator, OnLoadCompleteListener{

    companion object {
        fun newInstance() = BookMarkFragment()
    }

    private lateinit var viewModel: BookMarkViewModel
    private lateinit var binding: FragmentBookMarkBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var pdfUrl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookMarkBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[BookMarkViewModel::class.java]
        viewModel.setNavigator(this)
        progressDialog = ProgressDialog(context)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        var pastYear: ArrayList<PastYear> = Stash.getArrayList(SP_PAST_YEAR, PastYear::class.java)
        var pastYearAdapter = PastYearAdapter(pastYear, this)
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerPaper.layoutManager = linearLayoutManager
        binding.recyclerPaper.adapter = pastYearAdapter

        binding.refreshLayout.setOnRefreshListener {
            pastYear = Stash.getArrayList(SP_PAST_YEAR, PastYear::class.java)
            pastYearAdapter = PastYearAdapter(pastYear, this)
            binding.recyclerPaper.layoutManager = linearLayoutManager
            binding.recyclerPaper.adapter = pastYearAdapter
            binding.refreshLayout.isRefreshing = false
        }

        binding.btnClose.setOnClickListener {
            binding.webViewPast.visibility = View.GONE
            binding.topBarWebView.visibility = View.GONE
        }


        //Handle bookmarking here
        binding.btnBookmark.setOnClickListener {
            context?.let {
                val pastYearBookmarked = Stash.getArrayList<PastYear>(SP_PAST_YEAR, PastYear::class.java)

                if(pastYearBookmarked.contains(PastYear(binding.webViewTitle.text.toString(), pdfUrl))) {
                    pastYearBookmarked.remove(
                        PastYear(
                            binding.webViewTitle.text.toString(),
                            pdfUrl
                        )
                    )
                    Stash.put(SP_PAST_YEAR, pastYearBookmarked)
                    Toast.makeText(it, "Removed from bookmark.", Toast.LENGTH_LONG).show()
                    binding.btnBookmark.setImageDrawable(
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.star_24px
                        )
                    )
                } else{
                    pastYearBookmarked.add(
                        PastYear(
                            binding.webViewTitle.text.toString(),
                            pdfUrl
                        )
                    )
                    Stash.put(SP_PAST_YEAR, pastYearBookmarked)
                    Toast.makeText(it, "Added to bookmark.", Toast.LENGTH_LONG).show()
                    binding.btnBookmark.setImageDrawable(
                        ContextCompat.getDrawable(
                            it,
                            R.drawable.star_filled_24px
                        )
                    )
                }
            }
        }
    }

    override fun refreshRecyclerView(url: String) {
        //
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
            binding.topBarWebView.visibility = View.VISIBLE
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
            binding.webViewPast.visibility = View.VISIBLE
            binding.webViewPast.fromStream(inputStream)
                .onLoad(this@BookMarkFragment)
                .load()
        }

    }

    override fun setRecyclerBook(pastYear: List<PastYear>) {
        val pastYearAdapter = PastYearAdapter(pastYear, this)
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerPaper.layoutManager = linearLayoutManager
        binding.recyclerPaper.adapter = pastYearAdapter
    }


    override fun loadComplete(nbPages: Int) {
        progressDialog.dismiss()
    }

}