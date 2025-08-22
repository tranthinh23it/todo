package com.example.to_do_app.domain

data class  Notification(
    var id : String ,
    var recipient : String ="" ,
    var message : String ="",
    var type : String ="",
    var time : String ="",
    var seen : Boolean = false,
)
