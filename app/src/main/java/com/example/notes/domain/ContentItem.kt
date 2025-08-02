package com.example.notes.domain



sealed interface ContentItem {

    data class ContentItemText(val text: String) : ContentItem

    data class ContentItemImage(val url: String) : ContentItem
}