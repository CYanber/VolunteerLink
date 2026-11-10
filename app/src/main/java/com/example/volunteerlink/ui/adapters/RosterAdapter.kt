package com.example.volunteerlink.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.volunteerlink.databinding.ItemRosterBinding
import com.example.volunteerlink.model.AttendanceStatus
import com.example.volunteerlink.model.Registration

class RosterAdapter(
    private var roster: List<Registration>,
    private val onMarkAttendance: (Registration, AttendanceStatus) -> Unit
) : RecyclerView.Adapter<RosterAdapter.RosterViewHolder>() {

    fun updateData(newRoster: List<Registration>) {
        roster = newRoster
        notifyDataSetChanged()
    }

    inner class RosterViewHolder(private val binding: ItemRosterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reg: Registration) {
            binding.tvVolunteerName.text = reg.volunteerName
            binding.tvAttendanceStatus.text = reg.attendance?.name ?: "Not Marked"

            binding.btnPresent.setOnClickListener { onMarkAttendance(reg, AttendanceStatus.PRESENT) }
            binding.btnAbsent.setOnClickListener { onMarkAttendance(reg, AttendanceStatus.ABSENT) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RosterViewHolder {
        val binding = ItemRosterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RosterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RosterViewHolder, position: Int) {
        holder.bind(roster[position])
    }

    override fun getItemCount(): Int = roster.size
}
