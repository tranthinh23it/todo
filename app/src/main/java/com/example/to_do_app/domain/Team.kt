package com.example.to_do_app.domain

data class Team(
    var id: String ="",
    var name: String ="",
    var description: String ="",
    var owner: String="",
    var members: List<String> = emptyList(),

)