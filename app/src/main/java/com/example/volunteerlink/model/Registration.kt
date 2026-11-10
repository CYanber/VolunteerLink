package com.example.volunteerlink.model

import java.io.Serializable

data class Registration(
    val registrationId: String,
    val activityId: String,
    val volunteerId: String,
    val volunteerName: String,
    val activityTitle: String,
    val activityDate: String,
    val startTime: String,
    val endTime: String,
    val coordinatorId: String,
    var status: RegistrationStatus = RegistrationStatus.PENDING,
    var attendance: AttendanceStatus? = null,
    val requestedAt: Long = System.currentTimeMillis(),
    var decidedAt: Long? = null
) : Serializable
