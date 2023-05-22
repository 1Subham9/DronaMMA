package com.amtron.dronamma.model

data class Student(
    val id : String,
    val name: String,
    val mobile : String,
    val gender: String,
    val address: String,
    val className: String,
    val batch: String,
    val birthday: String,
    val branch: String,
    val active: Int,
    val fees : Double,
    val advance : Double
)
