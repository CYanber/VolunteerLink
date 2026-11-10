package com.example.volunteerlink.ui.coordinator

import android.R
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.volunteerlink.data.LocalRepository
import com.example.volunteerlink.data.SessionManager
import com.example.volunteerlink.databinding.ActivityCreateEditActivityBinding
import com.example.volunteerlink.model.ActivityItem
import java.util.Calendar
import java.util.Locale

class CreateEditActivityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateEditActivityBinding
    private var editingActivity: ActivityItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEditActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Create / Edit Activity"

        setupPickers()

        editingActivity = intent.getSerializableExtra("activity_item") as? ActivityItem

        if (editingActivity != null) {
            binding.tvHeaderTitle.text = "Edit Activity"
            binding.btnSaveActivity.text = "Update Activity"
            binding.etTitle.setText(editingActivity!!.title)
            binding.etDescription.setText(editingActivity!!.description)
            binding.etLocation.setText(editingActivity!!.location)
            binding.etDate.setText(editingActivity!!.date)
            binding.etStartTime.setText(editingActivity!!.startTime)
            binding.etEndTime.setText(editingActivity!!.endTime)
            binding.etDeadline.setText(editingActivity!!.registrationDeadline)
            binding.etMaxParticipants.setText(editingActivity!!.maxParticipants.toString())
        }

        binding.btnSaveActivity.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val date = binding.etDate.text.toString().trim()
            val startTime = binding.etStartTime.text.toString().trim()
            val endTime = binding.etEndTime.text.toString().trim()
            val deadline = binding.etDeadline.text.toString().trim()
            val maxPartStr = binding.etMaxParticipants.text.toString().trim()

            if (title.isEmpty() || location.isEmpty() || date.isEmpty() || maxPartStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val maxParticipants = maxPartStr.toIntOrNull() ?: 0
            if (maxParticipants < 1 || maxParticipants > 500) {
                Toast.makeText(this, "Max participants must be between 1 and 500", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (editingActivity == null) {
                val currentUser = SessionManager.currentUser
                if (currentUser == null) {
                    Toast.makeText(this, "Session expired", Toast.LENGTH_SHORT).show()
                    finish()
                    return@setOnClickListener
                }
                val error = LocalRepository.createActivity(
                    title, desc, location, date, startTime, endTime, deadline, maxParticipants, currentUser
                )
                if (error == null) {
                    Toast.makeText(this, "Activity created successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            } else {
                val error = LocalRepository.updateActivity(
                    editingActivity!!.activityId, title, desc, location, date, startTime, endTime, deadline, maxParticipants
                )
                if (error == null) {
                    Toast.makeText(this, "Activity updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupPickers() {
        // Date Picker for Date
        binding.etDate.isFocusable = false
        binding.etDate.isClickable = true
        binding.etDate.setOnClickListener {
            showDatePicker { dateStr ->
                binding.etDate.setText(dateStr)
            }
        }

        // Date Picker for Deadline
        binding.etDeadline.isFocusable = false
        binding.etDeadline.isClickable = true
        binding.etDeadline.setOnClickListener {
            showDatePicker { dateStr ->
                binding.etDeadline.setText(dateStr)
            }
        }

        // Time Picker for Start Time
        binding.etStartTime.isFocusable = false
        binding.etStartTime.isClickable = true
        binding.etStartTime.setOnClickListener {
            showTimePicker { timeStr ->
                binding.etStartTime.setText(timeStr)
            }
        }

        // Time Picker for End Time
        binding.etEndTime.isFocusable = false
        binding.etEndTime.isClickable = true
        binding.etEndTime.setOnClickListener {
            showTimePicker { timeStr ->
                binding.etEndTime.setText(timeStr)
            }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val formatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                onDateSelected(formatted)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val amPm = if (selectedHour < 12) "AM" else "PM"
                val hour12 = when {
                    selectedHour == 0 -> 12
                    selectedHour > 12 -> selectedHour - 12
                    else -> selectedHour
                }
                val formatted = String.format(Locale.getDefault(), "%02d:%02d %s", hour12, selectedMinute, amPm)
                onTimeSelected(formatted)
            },
            hour,
            minute,
            false
        ).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
