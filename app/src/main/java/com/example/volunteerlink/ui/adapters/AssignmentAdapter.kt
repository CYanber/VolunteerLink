package com.example.volunteerlink.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.volunteerlink.databinding.ItemAssignmentBinding
import com.example.volunteerlink.model.Registration
import com.example.volunteerlink.model.RegistrationStatus

class AssignmentAdapter(
    private var assignments: List<Registration>,
    private val onCancel: (Registration) -> Unit
) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>() {

    fun updateData(newAssignments: List<Registration>) {
        assignments = newAssignments
        notifyDataSetChanged()
    }

    inner class AssignmentViewHolder(private val binding: ItemAssignmentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reg: Registration) {
            binding.tvActivityTitle.text = reg.activityTitle
            binding.tvDateTime.text = "${reg.activityDate} | ${reg.startTime} - ${reg.endTime}"
            binding.chipRegStatus.text = reg.status.name

            if (reg.status == RegistrationStatus.PENDING || reg.status == RegistrationStatus.ACCEPTED) {
                binding.btnCancelReg.visibility = View.VISIBLE
                binding.btnCancelReg.setOnClickListener { onCancel(reg) }
            } else {
                binding.btnCancelReg.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val binding = ItemAssignmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        holder.bind(assignments[position])
    }

    override fun getItemCount(): Int = assignments.size
}
