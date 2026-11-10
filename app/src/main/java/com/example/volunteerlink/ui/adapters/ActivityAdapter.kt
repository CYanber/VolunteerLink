package com.example.volunteerlink.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.volunteerlink.databinding.ItemActivityBinding
import com.example.volunteerlink.model.ActivityItem

class ActivityAdapter(
    private var activities: List<ActivityItem>,
    private val onClick: (ActivityItem) -> Unit
) : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    fun updateData(newActivities: List<ActivityItem>) {
        activities = newActivities
        notifyDataSetChanged()
    }

    inner class ActivityViewHolder(private val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(activity: ActivityItem) {
            binding.tvTitle.text = activity.title
            binding.tvDescription.text = activity.description
            binding.tvDateTime.text = "${activity.date} | ${activity.startTime} - ${activity.endTime}"
            binding.tvLocation.text = "📍 ${activity.location}"
            binding.tvSlots.text = "Slots: ${activity.acceptedCount}/${activity.maxParticipants}"
            binding.chipStatus.text = activity.status.name

            binding.root.setOnClickListener { onClick(activity) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(activities[position])
    }

    override fun getItemCount(): Int = activities.size
}
