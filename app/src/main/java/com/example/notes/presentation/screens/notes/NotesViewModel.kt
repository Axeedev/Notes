@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.notes.presentation.screens.notes

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NotesRepositoryIml
import com.example.notes.domain.GetAllNotesUseCase
import com.example.notes.domain.Note
import com.example.notes.domain.SearchNotesUseCase
import com.example.notes.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel(context: Context) : ViewModel() {
    private val repository = NotesRepositoryIml.getInstance(context)
    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val searchNotesUseCase = SearchNotesUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)

    private val query = MutableStateFlow("")

    private val _state = MutableStateFlow(NotesScreenState())

    val state
        get() = _state.asStateFlow()

    fun processCommand(notesCommand: NotesCommand) {
        when (notesCommand) {
            is NotesCommand.InputSearchQuery -> {
                query.update { notesCommand.query.trim() }
                Log.d("NotesViewModel", notesCommand.query)
            }

            is NotesCommand.SwitchedPinnedStatus -> {
                viewModelScope.launch {
                    switchPinnedStatusUseCase.invoke(notesCommand.id)
                }
            }
        }
    }

    init {
        query.flatMapLatest {
            if (it.isBlank()) {
                getAllNotesUseCase()
            } else searchNotesUseCase(it)
        }
            .onEach { currentList ->
                val pinnedNotes = currentList.filter {
                    it.isPinned == true
                }
                val otherNotes = currentList.filter {
                    it.isPinned == false
                }
                _state.update {
                    it.copy(pinnedNotes = pinnedNotes, unpinnedNotes = otherNotes)
                }
            }
            .launchIn(viewModelScope)
    }
}

sealed interface NotesCommand {

    data class SwitchedPinnedStatus(val id: Int) : NotesCommand

    data class InputSearchQuery(val query: String) : NotesCommand

}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val unpinnedNotes: List<Note> = listOf()
)
