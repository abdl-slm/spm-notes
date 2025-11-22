package com.salam94.spmnotes.ui.main

interface MainNavigator {

    fun refreshRecyclerView(url: String)

    fun loadWebView(url: String, name: String)
}