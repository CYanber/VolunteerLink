package com.example.volunteerlink.ui.volunteer

import android.R
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.volunteerlink.data.LocalRepository
import com.example.volunteerlink.data.SessionManager
import com.example.volunteerlink.databinding.ActivityMyAssignmentsBinding
import com.example.volunteerlink.ui.adapters.AssignmentAdapter

class MyAssignmentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyAssignmentsBinding
    private lateinit var adapter: AssignmentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyAssignmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Assignments"

        adapter = AssignmentAdapter(emptyList()) { reg ->
            val error = LocalRepository.cancelRegistration(reg.registrationId)
            if (error == null) {
                Toast.makeText(this, "Registration cancelled successfully", Toast.LENGTH_SHORT).show()
                loadAssignments()
            } else {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }

        binding.rvAssignments.layoutManager = LinearLayoutManager(this)
        binding.rvAssignments.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadAssignments()
    }

    private fun loadAssignments() {
        val user = SessionManager.currentUser
        val volId = user?.uid ?: "vol_1"
        val list = LocalRepository.getRegistrationsForVolunteer(volId)
        adapter.updateData(list)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
