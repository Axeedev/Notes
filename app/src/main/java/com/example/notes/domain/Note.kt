package com.example.notes.domain

data class Note(
    val id: Int,
    val title: String,
    val content: List<ContentItem>,
    val isPinned: Boolean,
    val updatedAt : Long
)
