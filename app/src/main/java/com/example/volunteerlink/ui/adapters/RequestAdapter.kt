package com.example.volunteerlink.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.volunteerlink.databinding.ItemRequestBinding
import com.example.volunteerlink.model.Registration

class RequestAdapter(
    private var requests: List<Registration>,
    private val onAccept: (Registration) -> Unit,
    private val onDecline: (Registration) -> Unit
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    fun updateData(newRequests: List<Registration>) {
        requests = newRequests
        notifyDataSetChanged()
    }

    inner class RequestViewHolder(private val binding: ItemRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reg: Registration) {
            binding.tvVolunteerName.text = reg.volunteerName
            binding.btnAccept.setOnClickListener { onAccept(reg) }
            binding.btnDecline.setOnClickListener { onDecline(reg) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val binding = ItemRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int = requests.size
}
