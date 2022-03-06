package com.kerry.mycustomview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kerry.customview.scrollbar.FastScroller

class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), FastScroller.SectionIndexer {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_holder, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyViewHolder) {
            holder.bindView(position)
        }
    }


    override fun getItemCount(): Int = MainActivity.ITEM_COUNT

    override fun getSectionText(position: Int): CharSequence {
        return "${position.plus(1)}/${itemCount}"
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvContent: TextView = itemView.findViewById(R.id.tvContent)

        fun bindView(position: Int) {
            "item ${position.plus(1)}".let { tvContent.text = it }
        }

    }
}