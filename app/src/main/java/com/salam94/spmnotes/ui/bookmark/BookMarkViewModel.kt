package com.salam94.spmnotes.ui.bookmark

import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.salam94.spmnotes.adapter.PastYearAdapter
import com.salam94.spmnotes.model.PastYear
import com.salam94.spmnotes.util.SP_PAST_YEAR
import com.salam94.spmnotes.util.Stash
import java.util.ArrayList

class BookMarkViewModel : ViewModel() {

    private lateinit var bookMarkNavigator: BookMarkNavigator

    fun setNavigator(bookMarkNavigator: BookMarkNavigator){
        this.bookMarkNavigator = bookMarkNavigator
    }

    fun loadBookmarkedPastYear(bookMarkNavigator: BookMarkNavigator, pastYear: List<PastYear>){
        this.bookMarkNavigator.setRecyclerBook(pastYear)
    }
}