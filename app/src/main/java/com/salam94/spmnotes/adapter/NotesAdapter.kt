package com.salam94.spmnotes.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.salam94.ptcgdex.util.inflate
import com.salam94.spmnotes.R
import com.salam94.spmnotes.model.Notes
import com.salam94.spmnotes.ui.pastyear.PastYearNavigator


class NotesAdapter(private val notes: List<Notes>,private val pastYearNavigator: PastYearNavigator) :
    RecyclerView.Adapter<NotesAdapter.SetHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetHolder {
        val inflatedView: View = parent.inflate(R.layout.item_notes, false)
        return SetHolder(inflatedView, pastYearNavigator)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: SetHolder, position: Int) {
        val item = notes[position]
        holder.bindSet(item)
    }


    class SetHolder(v: View, private val pastYearNavigator: PastYearNavigator) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var item: Notes? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if(item?.link?.contains("pdf", true) == true){
                item?.link?.let { pastYearNavigator.loadWebView(it) }
            }
        }


        fun bindSet(item: Notes) {
            this.item = item
            val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
            txtTitle.text = item.name

            YoYo.with(Techniques.BounceIn)
                .duration(700)
                .playOn(view)
        }
    }

}