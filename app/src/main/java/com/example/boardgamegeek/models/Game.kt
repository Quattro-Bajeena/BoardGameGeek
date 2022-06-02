package com.example.boardgamegeek.models

import java.util.*

data class Game (
    val id: String,
    val name: String,
    val thumbnail: String,
    val published: String,
    val ranking: String,
    val subtype: String
        )