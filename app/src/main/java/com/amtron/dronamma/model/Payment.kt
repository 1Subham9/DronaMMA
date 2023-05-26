package com.amtron.dronamma.model

data class Payment(
    val payment_id: String? = null,
    val student_id: String? = null,
    val name: String? = null,
    val amount: Double? = null,
    val date: String? = null,
    var payment: Int? = null,
    val branch: String? = null
)
