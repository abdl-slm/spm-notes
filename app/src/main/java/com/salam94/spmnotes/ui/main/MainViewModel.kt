package com.salam94.spmnotes.ui.main

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salam94.spmnotes.adapter.PastYearAdapter
import com.salam94.spmnotes.model.PastYear
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class MainViewModel : ViewModel() {

    fun getPastYearList(
        context: Context,
        recyclerPaper: RecyclerView,
        url: String,
        mainNavigator: MainNavigator,
        progressBar: ProgressBar
    ) {
        // Launch a coroutine on the Main thread to start
        // Note: If this is inside an Activity/Fragment, use 'lifecycleScope.launch' instead of creating a new CoroutineScope
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Switch to IO thread for network and parsing operations
                val pastYearList = withContext(Dispatchers.IO) {
                    val pastYear: ArrayList<PastYear> = ArrayList()

                    // Connect and Get Document
                    val doc = Jsoup.connect(url).get()

                    val tableElements = doc.getElementsByClass("table table-striped table-bordered table-hover")

                    for (tableElement in tableElements) {
                        // Use standard loop or safety checks
                        val tbodies = tableElement.getElementsByTag("tbody")
                        if (tbodies.isEmpty()) continue

                        val tableBody = tbodies[0]
                        val tableRows = tableBody.getElementsByTag("tr")

                        for (tableRow in tableRows) {
                            val tableDataSet = tableRow.getElementsByTag("td")

                            for (tableData in tableDataSet) {
                                val links = tableData.getElementsByTag("a")
                                if (links.isNotEmpty()) {
                                    val paperTitleLink = links[0]
                                    val paperLink = paperTitleLink.absUrl("href")

                                    // Add to list
                                    pastYear.add(PastYear(tableData.text(), paperLink))
                                }
                            }
                        }
                    }
                    // Return the populated list to the Main thread
                    return@withContext pastYear
                }

                // We are back on the Main Thread here
                progressBar.visibility = View.GONE

                val pastYearAdapter = PastYearAdapter(pastYearList, mainNavigator)
                recyclerPaper.layoutManager = LinearLayoutManager(context)
                recyclerPaper.adapter = pastYearAdapter

            } catch (e: Exception) {
                // Handle errors on Main Thread
                e.printStackTrace()
                progressBar.visibility = View.GONE
            }
        }
    }
}