package com.example.volunteerlink.ui.volunteer

import android.R
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.volunteerlink.data.LocalRepository
import com.example.volunteerlink.data.SessionManager
import com.example.volunteerlink.databinding.ActivityActivityDetailsBinding
import com.example.volunteerlink.model.ActivityItem

class ActivityDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityActivityDetailsBinding
    private lateinit var activityItem: ActivityItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Activity Details"

        activityItem = intent.getSerializableExtra("activity_item") as? ActivityItem ?: finish().let { return }

        binding.tvTitle.text = activityItem.title
        binding.tvCoordinator.text = "Organizer: ${activityItem.coordinatorName}"
        binding.chipStatus.text = activityItem.status.name
        binding.tvDescription.text = activityItem.description
        binding.tvSchedule.text = "📅 ${activityItem.date} | ${activityItem.startTime} - ${activityItem.endTime}"
        binding.tvLocation.text = "📍 ${activityItem.location}"
        binding.tvDeadline.text = "⏰ Registration Deadline: ${activityItem.registrationDeadline}"
        binding.tvSlots.text = "Slots Filled: ${activityItem.acceptedCount} / ${activityItem.maxParticipants}"

        binding.btnRequestJoin.setOnClickListener {
            val user = SessionManager.currentUser
            if (user == null) {
                Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show()
                finish()
                return@setOnClickListener
            }

            val error = LocalRepository.requestToJoin(activityItem, user)
            if (error == null) {
                Toast.makeText(this, "Join request submitted successfully!", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
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
