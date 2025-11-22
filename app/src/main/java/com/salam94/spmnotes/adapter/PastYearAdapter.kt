package com.salam94.spmnotes.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.salam94.ptcgdex.util.inflate
import com.salam94.spmnotes.R
import com.salam94.spmnotes.model.PastYear
import com.salam94.spmnotes.ui.main.MainNavigator
import com.salam94.spmnotes.util.ImageHandler
import org.w3c.dom.Text


class PastYearAdapter(private val pastYear: List<PastYear>,private val mainNavigator: MainNavigator) :
    RecyclerView.Adapter<PastYearAdapter.SetHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetHolder {
        val inflatedView: View = parent.inflate(R.layout.item_past_year, false)
        return SetHolder(inflatedView, mainNavigator)
    }

    override fun getItemCount(): Int {
        return pastYear.size
    }

    override fun onBindViewHolder(holder: SetHolder, position: Int) {
        val item = pastYear[position]
        holder.bindSet(item)
    }


    class SetHolder(v: View, mainNavigator: MainNavigator) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var item: PastYear? = null
        private var mainNavigator = mainNavigator

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if(item?.link?.contains("pdf", true) == true){
                item?.link?.let { item?.name?.let { it1 -> mainNavigator.loadWebView(it, it1) } }
            } else {
                item?.link?.let { mainNavigator.refreshRecyclerView(it) }
            }
        }


        fun bindSet(item: PastYear) {
            this.item = item
            val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
            val imgSubject = view.findViewById<ImageView>(R.id.imgSubject)
            txtTitle.text = item.name.replace("_", " ").replace(".pdf", "")
            imgSubject.setImageDrawable(ContextCompat.getDrawable(view.context, ImageHandler().imageSubjectHandler(item.name)))

            YoYo.with(Techniques.BounceIn)
                .duration(700)
                .playOn(view)
        }
    }

}