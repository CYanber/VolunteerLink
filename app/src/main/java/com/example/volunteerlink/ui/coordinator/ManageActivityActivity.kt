package com.example.volunteerlink.ui.coordinator

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.volunteerlink.data.LocalRepository
import com.example.volunteerlink.databinding.ActivityManageActivityBinding
import com.example.volunteerlink.model.ActivityItem
import com.example.volunteerlink.model.ActivityStatus
import com.example.volunteerlink.model.RegistrationStatus
import com.example.volunteerlink.ui.adapters.RequestAdapter
import com.example.volunteerlink.ui.adapters.RosterAdapter
import com.google.android.material.tabs.TabLayout

class ManageActivityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageActivityBinding
    private lateinit var activityItem: ActivityItem
    private lateinit var requestAdapter: RequestAdapter
    private lateinit var rosterAdapter: RosterAdapter
    private var currentTab = 0 // 0: Requests, 1: Roster

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Manage Activity"

        activityItem = intent.getSerializableExtra("activity_item") as? ActivityItem ?: finish().let { return }

        binding.tvActivityTitle.text = activityItem.title
        updateSlotsInfo()

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Pending Requests"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Accepted Roster"))

        requestAdapter = RequestAdapter(emptyList(),
            onAccept = { reg ->
                val error = LocalRepository.acceptRequest(reg.registrationId)
                if (error == null) {
                    Toast.makeText(this, "Volunteer accepted", Toast.LENGTH_SHORT).show()
                    reloadCurrentTab()
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            },
            onDecline = { reg ->
                LocalRepository.declineRequest(reg.registrationId)
                Toast.makeText(this, "Volunteer declined", Toast.LENGTH_SHORT).show()
                reloadCurrentTab()
            }
        )

        rosterAdapter = RosterAdapter(emptyList()) { reg, attendance ->
            LocalRepository.updateAttendance(reg.registrationId, attendance)
            Toast.makeText(this, "Attendance marked as ${attendance.name}", Toast.LENGTH_SHORT).show()
            reloadCurrentTab()
        }

        binding.rvManageList.layoutManager = LinearLayoutManager(this)
        binding.rvManageList.adapter = requestAdapter

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                reloadCurrentTab()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, CreateEditActivityActivity::class.java)
            intent.putExtra("activity_item", activityItem)
            startActivity(intent)
        }

        binding.btnCancelActivity.setOnClickListener {
            if (activityItem.status == ActivityStatus.CANCELLED) {
                LocalRepository.reopenActivity(activityItem.activityId)
                Toast.makeText(this, "Activity reopened", Toast.LENGTH_SHORT).show()
            } else {
                LocalRepository.cancelActivity(activityItem.activityId)
                Toast.makeText(this, "Activity cancelled", Toast.LENGTH_SHORT).show()
            }
            refreshActivityState()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshActivityState()
    }

    private fun refreshActivityState() {
        val refreshed = LocalRepository.getAllActivities().find { it.activityId == activityItem.activityId }
        if (refreshed != null) {
            activityItem = refreshed
            binding.tvActivityTitle.text = activityItem.title
            updateSlotsInfo()
        }
        reloadCurrentTab()
    }

    private fun updateSlotsInfo() {
        binding.tvSlotsInfo.text = "Slots: ${activityItem.acceptedCount}/${activityItem.maxParticipants} | Status: ${activityItem.status.name}"
        if (activityItem.status == ActivityStatus.CANCELLED) {
            binding.btnCancelActivity.text = "Reopen Activity"
        } else {
            binding.btnCancelActivity.text = "Cancel Activity"
        }
    }

    private fun reloadCurrentTab() {
        val allRegs = LocalRepository.getRegistrationsForActivity(activityItem.activityId)
        if (currentTab == 0) {
            binding.tvSectionHeader.text = "Pending Requests"
            val pending = allRegs.filter { it.status == RegistrationStatus.PENDING }
            binding.rvManageList.adapter = requestAdapter
            requestAdapter.updateData(pending)
        } else {
            binding.tvSectionHeader.text = "Accepted Roster"
            val accepted = allRegs.filter { it.status == RegistrationStatus.ACCEPTED }
            binding.rvManageList.adapter = rosterAdapter
            rosterAdapter.updateData(accepted)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
