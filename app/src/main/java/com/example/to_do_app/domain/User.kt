package com.example.to_do_app.domain

data class User(
    var name: String = "",
    var userName: String = "",
    var userId: String = "",
    var email: String = "",
    var password: String = "",
    var phoneNumber: String = "",
    var dob: String = "",
    var address: String = "",
    var imgUrl: String? = "",
    val fcmToken: String? = null,
    val gender : String = "",
    val team : String="",
)