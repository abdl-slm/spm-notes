package com.salam94.spmnotes.ui.pastyear

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.google.android.gms.ads.AdRequest
import com.salam94.spmnotes.databinding.PastYearFragmentBinding
import com.salam94.spmnotes.model.PastYear
import com.salam94.spmnotes.util.Stash
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class PastYearFragment : Fragment(), PastYearNavigator, OnLoadCompleteListener {

    companion object {
        fun newInstance() = PastYearFragment()
    }

    private lateinit var viewModel: PastYearViewModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var pdfUrl: String
    private lateinit var binding: PastYearFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PastYearFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this)[PastYearViewModel::class.java]
        progressDialog = ProgressDialog(context)

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        val linearLayoutManager = LinearLayoutManager(context)
        binding.recyclerNote.adapter = viewModel.loadNotes(this)
        binding.recyclerNote.layoutManager = linearLayoutManager

        binding.btnCloseNote.setOnClickListener {
            binding.webViewNote.visibility = View.GONE
            binding.topBarWebViewNote.visibility = View.GONE
        }

        binding.btnBookmark.setOnClickListener {
            // 1. Get existing list (Stash returns an empty ArrayList if none exists)
            // We pass PastYear::class.java so Gson knows what type of objects are inside
            val bookmarks = Stash.getArrayList<PastYear>("past_year", PastYear::class.java)

            // 2. Add the new item
            bookmarks.add(PastYear("test", "test"))

            // 3. Save the updated list back to Stash
            Stash.put("past_year", bookmarks)

            // Log to verify
            Log.d("salamk", Stash.getArrayList<PastYear>("past_year", PastYear::class.java).toString())
        }
    }


    override fun loadWebView(url: String) {
        this.pdfUrl = url
        LoadUrl().execute()
    }

     inner class LoadUrl() : AsyncTask<String?, Void?, InputStream?>() {
        override fun onPreExecute() {
            super.onPreExecute()

            binding.topBarWebViewNote.visibility = View.VISIBLE

            progressDialog.setTitle("Please wait")
            progressDialog.setMessage("Fetching Notes PDF from server, this might take a while.")
            progressDialog.setCancelable(true)
            progressDialog.setCanceledOnTouchOutside(true)
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
            binding.webViewNote.visibility = View.VISIBLE
            binding.webViewNote.fromStream(inputStream)
                .onLoad(this@PastYearFragment)
                .load()
        }

    }

    override fun loadComplete(nbPages: Int) {
        progressDialog.dismiss()
    }

}