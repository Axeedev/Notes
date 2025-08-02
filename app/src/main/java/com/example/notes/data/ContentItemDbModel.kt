package com.example.notes.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface ContentItemDbModel {

    @Serializable
    data class ContentItemTextDb(val text: String) : ContentItemDbModel

    @Serializable
    data class ContentItemImageDb(val url: String) : ContentItemDbModel
}