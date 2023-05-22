package com.amtron.dronamma.model

data class Attendance(
    val id : String? = null,
    val name: String? = null,
    val className: String? = null,
    val studentId: String? = null,
    val batch : String? = null,
    val branch: String? = null,
    val date: String? = null,
    var present : Int? = null
)
