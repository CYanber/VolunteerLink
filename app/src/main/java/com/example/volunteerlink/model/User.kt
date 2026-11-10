package com.example.volunteerlink.model

import java.io.Serializable

data class User(
    val uid: String,
    val fullName: String,
    val email: String,
    val role: Role,
    val phone: String = "",
    val createdAt: Long = System.currentTimeMillis()
) : Serializable
