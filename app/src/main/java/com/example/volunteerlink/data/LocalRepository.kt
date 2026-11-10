package com.example.volunteerlink.data

import com.example.volunteerlink.model.*

object LocalRepository {
    private val users = mutableMapOf<String, User>()
    private val userPasswords = mutableMapOf<String, String>() // email -> password
    private val activities = mutableMapOf<String, ActivityItem>()
    private val registrations = mutableMapOf<String, Registration>()

    init {
        // Seed initial users
        val coordId = "coord_1"
        users[coordId] = User(coordId, "Prof. Maria Santos", "coordinator@example.com", Role.COORDINATOR, "09123456789")
        userPasswords["coordinator@example.com"] = "password123"

        val volId = "vol_1"
        users[volId] = User(volId, "Juan Dela Cruz", "volunteer@example.com", Role.VOLUNTEER, "09987654321")
        userPasswords["volunteer@example.com"] = "password123"

        // Seed initial activities
        val actId1 = "act_1"
        activities[actId1] = ActivityItem(
            activityId = actId1,
            title = "Barangay Clean-up Drive",
            description = "Help clean up the community plaza and surrounding streets to promote health and sanitation.",
            location = "Barangay Hall Plaza",
            date = "2026-04-20",
            startTime = "07:00 AM",
            endTime = "10:00 AM",
            registrationDeadline = "2026-04-18",
            maxParticipants = 5,
            acceptedCount = 1,
            status = ActivityStatus.OPEN,
            coordinatorId = coordId,
            coordinatorName = "Prof. Maria Santos"
        )

        val actId2 = "act_2"
        activities[actId2] = ActivityItem(
            activityId = actId2,
            title = "Weekend Feeding Program",
            description = "Distribute nutritious meals and care packages to underprivileged children in the district.",
            location = "Community Center Gymnasium",
            date = "2026-04-25",
            startTime = "09:00 AM",
            endTime = "01:00 PM",
            registrationDeadline = "2026-04-23",
            maxParticipants = 10,
            acceptedCount = 0,
            status = ActivityStatus.OPEN,
            coordinatorId = coordId,
            coordinatorName = "Prof. Maria Santos"
        )

        // Seed initial registration
        val regId = "${actId1}_${volId}"
        registrations[regId] = Registration(
            registrationId = regId,
            activityId = actId1,
            volunteerId = volId,
            volunteerName = "Juan Dela Cruz",
            activityTitle = "Barangay Clean-up Drive",
            activityDate = "2026-04-20",
            startTime = "07:00 AM",
            endTime = "10:00 AM",
            coordinatorId = coordId,
            status = RegistrationStatus.ACCEPTED
        )
    }

    fun registerUser(fullName: String, email: String, pass: String, role: Role, phone: String): Pair<User?, String?> {
        if (fullName.isBlank() || email.isBlank() || pass.isBlank()) {
            return Pair(null, "All required fields must be filled.")
        }
        if (pass.length < 6) {
            return Pair(null, "Password must be at least 6 characters.")
        }
        if (userPasswords.containsKey(email.lowercase())) {
            return Pair(null, "Email is already registered.")
        }
        val uid = "user_${System.currentTimeMillis()}"
        val user = User(uid, fullName, email.lowercase(), role, phone)
        users[uid] = user
        userPasswords[email.lowercase()] = pass
        return Pair(user, null)
    }

    fun loginUser(email: String, pass: String): Pair<User?, String?> {
        val emailKey = email.lowercase().trim()
        val storedPass = userPasswords[emailKey]
        if (storedPass == null || storedPass != pass) {
            return Pair(null, "Invalid email or password.")
        }
        val user = users.values.find { it.email.equals(emailKey, ignoreCase = true) }
        return Pair(user, null)
    }

    fun getActivitiesForCoordinator(coordinatorId: String): List<ActivityItem> {
        return activities.values.filter { it.coordinatorId == coordinatorId }.sortedBy { it.date }
    }

    fun getOpenActivities(): List<ActivityItem> {
        return activities.values.filter { it.status == ActivityStatus.OPEN }.sortedBy { it.date }
    }

    fun getAllActivities(): List<ActivityItem> {
        return activities.values.sortedBy { it.date }
    }

    fun createActivity(
        title: String,
        description: String,
        location: String,
        date: String,
        startTime: String,
        endTime: String,
        deadline: String,
        maxParticipants: Int,
        coordinator: User
    ): String? {
        if (title.isBlank() || location.isBlank() || date.isBlank()) {
            return "Title, location, and date are required."
        }
        if (maxParticipants < 1 || maxParticipants > 500) {
            return "Max participants must be between 1 and 500."
        }
        val actId = "act_${System.currentTimeMillis()}"
        val activity = ActivityItem(
            activityId = actId,
            title = title,
            description = description,
            location = location,
            date = date,
            startTime = startTime,
            endTime = endTime,
            registrationDeadline = deadline,
            maxParticipants = maxParticipants,
            acceptedCount = 0,
            status = ActivityStatus.OPEN,
            coordinatorId = coordinator.uid,
            coordinatorName = coordinator.fullName
        )
        activities[actId] = activity
        return null
    }

    fun updateActivity(
        activityId: String,
        title: String,
        description: String,
        location: String,
        date: String,
        startTime: String,
        endTime: String,
        deadline: String,
        maxParticipants: Int
    ): String? {
        val existing = activities[activityId] ?: return "Activity not found."
        if (maxParticipants < existing.acceptedCount) {
            return "Cannot reduce max participants below current accepted count (${existing.acceptedCount})."
        }
        existing.title = title
        existing.description = description
        existing.location = location
        existing.date = date
        existing.startTime = startTime
        existing.endTime = endTime
        existing.registrationDeadline = deadline
        existing.maxParticipants = maxParticipants
        return null
    }

    fun cancelActivity(activityId: String) {
        val act = activities[activityId] ?: return
        act.status = ActivityStatus.CANCELLED
        // Cancel all pending and accepted registrations for this activity
        for (reg in registrations.values.filter { it.activityId == activityId }) {
            if (reg.status == RegistrationStatus.PENDING || reg.status == RegistrationStatus.ACCEPTED) {
                reg.status = RegistrationStatus.CANCELLED
            }
        }
    }

    fun reopenActivity(activityId: String) {
        val act = activities[activityId] ?: return
        act.status = if (act.acceptedCount >= act.maxParticipants) ActivityStatus.FULL else ActivityStatus.OPEN
    }

    fun getRegistrationsForActivity(activityId: String): List<Registration> {
        return registrations.values.filter { it.activityId == activityId }
    }

    fun getRegistrationsForVolunteer(volunteerId: String): List<Registration> {
        return registrations.values.filter { it.volunteerId == volunteerId }.sortedByDescending { it.activityDate }
    }

    fun requestToJoin(activity: ActivityItem, volunteer: User): String? {
        if (activity.status != ActivityStatus.OPEN) {
            return "Activity is not open for registration."
        }
        // Check duplicate registration
        val existingReg = registrations.values.find { it.activityId == activity.activityId && it.volunteerId == volunteer.uid }
        if (existingReg != null) {
            if (existingReg.status == RegistrationStatus.PENDING || existingReg.status == RegistrationStatus.ACCEPTED) {
                return "You already have an active request or assignment for this activity."
            }
        }

        // Check schedule conflicts with other PENDING or ACCEPTED registrations on the same date
        val volunteerRegs = registrations.values.filter {
            it.volunteerId == volunteer.uid && (it.status == RegistrationStatus.PENDING || it.status == RegistrationStatus.ACCEPTED)
        }
        for (reg in volunteerRegs) {
            if (reg.activityDate == activity.date) {
                // Same date conflict check
                return "Schedule conflict: You already have a commitment on ${activity.date} (${reg.activityTitle})."
            }
        }

        val regId = "${activity.activityId}_${volunteer.uid}"
        val newReg = Registration(
            registrationId = regId,
            activityId = activity.activityId,
            volunteerId = volunteer.uid,
            volunteerName = volunteer.fullName,
            activityTitle = activity.title,
            activityDate = activity.date,
            startTime = activity.startTime,
            endTime = activity.endTime,
            coordinatorId = activity.coordinatorId,
            status = RegistrationStatus.PENDING
        )
        registrations[regId] = newReg
        return null
    }

    fun acceptRequest(registrationId: String): String? {
        val reg = registrations[registrationId] ?: return "Registration not found."
        val act = activities[reg.activityId] ?: return "Activity not found."

        if (act.acceptedCount >= act.maxParticipants) {
            return "Activity is already full."
        }

        reg.status = RegistrationStatus.ACCEPTED
        reg.decidedAt = System.currentTimeMillis()
        act.acceptedCount++

        if (act.acceptedCount >= act.maxParticipants) {
            act.status = ActivityStatus.FULL
        }
        return null
    }

    fun declineRequest(registrationId: String): String? {
        val reg = registrations[registrationId] ?: return "Registration not found."
        reg.status = RegistrationStatus.DECLINED
        reg.decidedAt = System.currentTimeMillis()
        return null
    }

    fun cancelRegistration(registrationId: String): String? {
        val reg = registrations[registrationId] ?: return "Registration not found."
        val wasAccepted = reg.status == RegistrationStatus.ACCEPTED
        reg.status = RegistrationStatus.CANCELLED

        if (wasAccepted) {
            val act = activities[reg.activityId]
            if (act != null) {
                if (act.acceptedCount > 0) act.acceptedCount--
                if (act.status == ActivityStatus.FULL && act.acceptedCount < act.maxParticipants) {
                    act.status = ActivityStatus.OPEN
                }
            }
        }
        return null
    }

    fun updateAttendance(registrationId: String, attendance: AttendanceStatus) {
        val reg = registrations[registrationId] ?: return
        reg.attendance = attendance
    }
}
