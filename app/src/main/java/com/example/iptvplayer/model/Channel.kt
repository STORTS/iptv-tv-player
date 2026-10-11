package com.example.iptvplayer.model

data class Channel(
    val id: String,
    val name: String,
    val streamUrl: String,
    val logoUrl: String?,
    val group: String
)
