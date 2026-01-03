package com.security.monitor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.security.monitor.models.TrafficData

class TrafficAdapter : RecyclerView.Adapter<TrafficAdapter.ViewHolder>() {
    
    private val items = mutableListOf<TrafficData>()
    
    fun updateData(newItems: List<TrafficData>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_traffic, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
    
    override fun getItemCount(): Int = items.size
    
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textSource: TextView = itemView.findViewById(R.id.text_source)
        private val textDest: TextView = itemView.findViewById(R.id.text_dest)
        private val textProtocol: TextView = itemView.findViewById(R.id.text_protocol)
        private val textBytes: TextView = itemView.findViewById(R.id.text_bytes)
        private val textStatus: TextView = itemView.findViewById(R.id.text_status)
        
        fun bind(traffic: TrafficData) {
            textSource.text = traffic.sourceIP
            textDest.text = "${traffic.destIP}:${traffic.destPort}"
            textProtocol.text = traffic.protocol
            textBytes.text = "${traffic.bytesSent + traffic.bytesReceived} bytes"
            
            if (traffic.status != null) {
                textStatus.text = traffic.status
            } else {
                textStatus.text = "Score: ${traffic.threatScore.toInt()}"
            }
            
            // Color based on threat score
            when {
                traffic.threatScore > 80 -> {
                    textStatus.setTextColor(android.graphics.Color.RED)
                }
                traffic.threatScore > 60 -> {
                    textStatus.setTextColor(android.graphics.Color.parseColor("#FFA500"))
                }
                else -> {
                    textStatus.setTextColor(android.graphics.Color.GREEN)
                }
            }
        }
    }
}
