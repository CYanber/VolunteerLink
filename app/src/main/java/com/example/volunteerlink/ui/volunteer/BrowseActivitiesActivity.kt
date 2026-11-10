package com.example.volunteerlink.ui.volunteer

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.volunteerlink.data.LocalRepository
import com.example.volunteerlink.databinding.ActivityBrowseActivitiesBinding
import com.example.volunteerlink.model.ActivityItem
import com.example.volunteerlink.ui.adapters.ActivityAdapter
import com.example.volunteerlink.ui.profile.ProfileActivity

class BrowseActivitiesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBrowseActivitiesBinding
    private lateinit var adapter: ActivityAdapter
    private var allActivities = listOf<ActivityItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBrowseActivitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ActivityAdapter(emptyList()) { activity ->
            val intent = Intent(this, ActivityDetailsActivity::class.java)
            intent.putExtra("activity_item", activity)
            startActivity(intent)
        }

        binding.rvBrowseActivities.layoutManager = LinearLayoutManager(this)
        binding.rvBrowseActivities.adapter = adapter

        binding.btnMyAssignments.setOnClickListener {
            startActivity(Intent(this, MyAssignmentsActivity::class.java))
        }

        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterActivities(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        allActivities = LocalRepository.getOpenActivities()
        filterActivities(binding.etSearch.text.toString())
    }

    private fun filterActivities(query: String) {
        val q = query.lowercase().trim()
        val filtered = if (q.isEmpty()) {
            allActivities
        } else {
            allActivities.filter {
                it.title.lowercase().contains(q) || it.location.lowercase().contains(q)
            }
        }
        adapter.updateData(filtered)
    }
}
