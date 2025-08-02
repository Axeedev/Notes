@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.notes.presentation.screens.notes

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NotesRepositoryIml
import com.example.notes.domain.GetAllNotesUseCase
import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import com.example.notes.domain.SearchNotesUseCase
import com.example.notes.domain.SwitchPinnedStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotesUseCase : GetAllNotesUseCase,
    private val searchNotesUseCase : SearchNotesUseCase,
    private val switchPinnedStatusUseCase : SwitchPinnedStatusUseCase,
) : ViewModel() {




    private val query = MutableStateFlow("")

    private val _state = MutableStateFlow(NotesScreenState())

    val state
        get() = _state.asStateFlow()

    fun processCommand(notesCommand: NotesCommand) {
        when (notesCommand) {
            is NotesCommand.InputSearchQuery -> {
                val new = notesCommand.query
                query.update { new }
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
        query
            .onEach { s ->
                _state.update { it.copy(query = s) }
            }
            .flatMapLatest {
                if (it.isBlank()) {
                    getAllNotesUseCase()
                } else searchNotesUseCase(it)
            }
            .onEach { currentList ->
                Log.d("NotesViewModel", currentList.joinToString(", "))
                val pinnedNotes = currentList.filter {
                    it.isPinned
                }
                val otherNotes = currentList.filter {
                    !it.isPinned
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
