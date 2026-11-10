package com.example.volunteerlink.ui.profile

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.volunteerlink.data.SessionManager
import com.example.volunteerlink.databinding.ActivityProfileBinding
import com.example.volunteerlink.ui.auth.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "User Profile"

        val user = SessionManager.currentUser
        if (user != null) {
            binding.tvName.text = user.fullName
            binding.tvRole.text = "ROLE: ${user.role.name}"
            binding.tvEmail.text = "Email: ${user.email}"
            binding.tvPhone.text = "Phone: ${user.phone.ifEmpty { "Not provided" }}"
        }

        binding.btnLogout.setOnClickListener {
            SessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
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
