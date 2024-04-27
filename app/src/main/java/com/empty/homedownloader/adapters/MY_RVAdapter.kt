package com.empty.homedownloader.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.empty.homedownloader.myclasses.ParentFeed
import com.empty.homedownloader.R

class MY_RVAdapter(private val parentFeed: ParentFeed): RecyclerView.Adapter<MY_RVAdapter.CustomVH>() {

    inner class CustomVH(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemTitle : TextView = itemView.findViewById(R.id.itemTitle)
        val itemUrl : TextView = itemView.findViewById(R.id.itemUrl)
        val itemRow : LinearLayout = itemView.findViewById(R.id.customLL)
    }

    override fun getItemCount(): Int {
        return parentFeed.allData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomVH {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemRow = layoutInflater.inflate(R.layout.custom_app_list, parent, false)
        return CustomVH(itemRow)
    }

    override fun onBindViewHolder(holder: CustomVH, position: Int) {
        val data = parentFeed.allData[position]
        val assetData = data.assets.firstOrNull()

        if (position == 0) {
            val first = data.name + " <-- Latest (Click This)"
            holder.itemTitle.text = first
        }else {
            holder.itemTitle.text = data.name
        }

        holder.itemUrl.text = assetData?.browser_download_url ?: "INVALID APP URL"

        val row = holder.itemRow.context
        holder.itemRow.setOnClickListener {
            Toast.makeText(row, assetData?.name, Toast.LENGTH_SHORT).show()
        }
    }
}