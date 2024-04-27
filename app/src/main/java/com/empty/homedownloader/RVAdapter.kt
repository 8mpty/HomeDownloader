package com.empty.homedownloader

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RVAdapter: RecyclerView.Adapter<CustomVH>() {
    override fun getItemCount(): Int {
        return 10
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomVH {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val itemRow = layoutInflater.inflate(R.layout.custom_app_list, parent, false)
        return CustomVH(itemRow)
    }

    override fun onBindViewHolder(holder: CustomVH, position: Int) {
        val itemTitle = holder.itemView.findViewById<TextView>(R.id.itemTitle)
        val itemUrl = holder.itemView.findViewById<TextView>(R.id.itemUrl)

        itemTitle.text = "NEW APP!!"
        itemUrl.text = "NEW URL"
    }
}

class CustomVH(v: View): RecyclerView.ViewHolder(v)