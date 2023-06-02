package com.amtron.dronamma.model

data class Student(
    val id : String?=null,
    val name: String?=null,
    val mobile : String?=null,
    val gender: String?=null,
    val address: String?=null,
    val className: String?=null,
    val batch: String?=null,
    val birthday: String?=null,
    val branch: String?=null,
    var paid: Int?=null,
    var fees : Double?=null,
    var month : Int?=null,
)
