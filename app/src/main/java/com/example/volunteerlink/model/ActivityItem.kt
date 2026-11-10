package com.example.volunteerlink.model

import java.io.Serializable

data class ActivityItem(
    val activityId: String,
    var title: String,
    var description: String,
    var location: String,
    var date: String,
    var startTime: String,
    var endTime: String,
    var registrationDeadline: String,
    var maxParticipants: Int,
    var acceptedCount: Int = 0,
    var status: ActivityStatus = ActivityStatus.OPEN,
    val coordinatorId: String,
    val coordinatorName: String,
    val createdAt: Long = System.currentTimeMillis()
) : Serializable
