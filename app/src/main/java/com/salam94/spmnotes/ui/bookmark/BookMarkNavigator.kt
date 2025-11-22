package com.salam94.spmnotes.ui.bookmark

import com.salam94.spmnotes.model.PastYear

interface BookMarkNavigator {
    fun setRecyclerBook(pastYear: List<PastYear>)
}