package com.example.volunteerlink.data

import com.example.volunteerlink.model.User

object SessionManager {
    var currentUser: User? = null

    fun isLoggedIn(): Boolean = currentUser != null

    fun logout() {
        currentUser = null
    }
}
