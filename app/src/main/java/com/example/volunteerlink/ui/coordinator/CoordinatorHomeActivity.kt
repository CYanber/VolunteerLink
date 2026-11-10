package com.example.volunteerlink.ui.coordinator

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.volunteerlink.data.LocalRepository
import com.example.volunteerlink.data.SessionManager
import com.example.volunteerlink.databinding.ActivityCoordinatorHomeBinding
import com.example.volunteerlink.ui.adapters.ActivityAdapter
import com.example.volunteerlink.ui.profile.ProfileActivity

class CoordinatorHomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCoordinatorHomeBinding
    private lateinit var adapter: ActivityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoordinatorHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = SessionManager.currentUser
        val coordId = currentUser?.uid ?: "coord_1"

        adapter = ActivityAdapter(emptyList()) { activity ->
            val intent = Intent(this, ManageActivityActivity::class.java)
            intent.putExtra("activity_item", activity)
            startActivity(intent)
        }

        binding.rvCoordinatorActivities.layoutManager = LinearLayoutManager(this)
        binding.rvCoordinatorActivities.adapter = adapter

        binding.fabCreateActivity.setOnClickListener {
            startActivity(Intent(this, CreateEditActivityActivity::class.java))
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val currentUser = SessionManager.currentUser
        val coordId = currentUser?.uid ?: "coord_1"
        val list = LocalRepository.getActivitiesForCoordinator(coordId)
        adapter.updateData(list)
    }
}
