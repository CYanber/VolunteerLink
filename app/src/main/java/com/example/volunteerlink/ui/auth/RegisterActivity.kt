package com.example.volunteerlink.ui.auth

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.volunteerlink.data.LocalRepository
import com.example.volunteerlink.data.SessionManager
import com.example.volunteerlink.databinding.ActivityRegisterBinding
import com.example.volunteerlink.model.Role
import com.example.volunteerlink.ui.coordinator.CoordinatorHomeActivity
import com.example.volunteerlink.ui.volunteer.BrowseActivitiesActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Register Account"

        binding.btnRegister.setOnClickListener {
            val name = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val role = if (binding.rbCoordinator.isChecked) Role.COORDINATOR else Role.VOLUNTEER

            val (user, error) = LocalRepository.registerUser(name, email, pass, role, phone)
            if (user != null) {
                SessionManager.currentUser = user
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                routeUser(user.role)
            } else {
                Toast.makeText(this, error ?: "Registration failed", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun routeUser(role: Role) {
        val intent = when (role) {
            Role.COORDINATOR -> Intent(this, CoordinatorHomeActivity::class.java)
            Role.VOLUNTEER -> Intent(this, BrowseActivitiesActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
