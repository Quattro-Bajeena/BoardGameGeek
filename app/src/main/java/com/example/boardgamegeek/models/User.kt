package com.example.boardgamegeek.models

import java.util.*

data class User (
    val username : String,
    val gameAmount : Int,
    val addonAmount: Int,
    val syncDate: Date
    )