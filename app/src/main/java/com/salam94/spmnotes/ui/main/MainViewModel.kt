package com.salam94.spmnotes.ui.main

import android.content.Context
import android.util.Log
import android.view.View.GONE
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salam94.spmnotes.adapter.PastYearAdapter
import com.salam94.spmnotes.model.PastYear
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup
import java.lang.Exception

class MainViewModel : ViewModel() {

    fun getPastYearList(context: Context, recyclerPaper: RecyclerView, url: String, mainNavigator: MainNavigator, progressBar: ProgressBar) {
        doAsync {

            val pastYear: ArrayList<PastYear> = ArrayList()
            val doc =
                Jsoup.connect(url)
                    .get()
            val tableElements =
                doc.getElementsByClass("table table-striped table-bordered table-hover")

            for (tableElement in tableElements) {

                try {

                    val tableBody = tableElement.getElementsByTag("tbody")[0]
                    val tableRows = tableBody.getElementsByTag("tr")

                    for (tableRow in tableRows) {

                        val tableDataSet = tableRow.getElementsByTag("td")

                        for (tableData in tableDataSet) {
                            val paperTitleLink = tableData.getElementsByTag("a")[0]
                            val paperLink = paperTitleLink.absUrl("href")

                            //Too long is most probably ads
                            pastYear.add(PastYear(tableData.text(), paperLink))
                        }
                    }

                    uiThread {
                        progressBar.visibility = GONE
                        val pastYearAdapter = PastYearAdapter(pastYear, mainNavigator)
                        val linearLayoutManager = LinearLayoutManager(context)
                        recyclerPaper.layoutManager = linearLayoutManager
                        recyclerPaper.adapter = pastYearAdapter
                    }
                }catch (e:Exception){
                    uiThread {
                        progressBar.visibility = GONE
                    }
                }

            }
        }
    }
}