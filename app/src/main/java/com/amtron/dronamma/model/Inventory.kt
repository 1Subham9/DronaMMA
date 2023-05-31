package com.amtron.dronamma.model


data class Inventory(
    val id: String?=null,
    var name: String?=null,
    var branch: String?=null,
    var price: Double? =null,
    var quantity: Int? =null
)
