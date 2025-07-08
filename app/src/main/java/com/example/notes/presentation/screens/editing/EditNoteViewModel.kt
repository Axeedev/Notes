package com.example.notes.presentation.screens.editing

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NotesRepositoryIml
import com.example.notes.domain.DeleteNoteUseCase
import com.example.notes.domain.EditNoteUseCase
import com.example.notes.domain.GetNoteUseCase
import com.example.notes.domain.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditNoteViewModel(private val id : Int, context: Context) : ViewModel() {

    private val repository = NotesRepositoryIml.getInstance(context)

    private val editNoteUseCase = EditNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)


    private val _state: MutableStateFlow<EditNoteState> = MutableStateFlow(EditNoteState.Initial)
    val state
        get() = _state.asStateFlow()


    fun processCommand(command: EditNoteCommand) {
        when (command) {
            EditNoteCommand.Back -> {
                _state.update {
                    EditNoteState.Finished
                }
            }

            EditNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update { prev ->
                        if (_state.value is EditNoteState.Editing) {
                            val note = (_state.value as EditNoteState.Editing).note
                            editNoteUseCase.invoke(note)
                            EditNoteState.Finished
                        } else {
                            prev
                        }
                    }
                }
            }

            is EditNoteCommand.EditContent -> {
                _state.update { prev ->
                    if (prev is EditNoteState.Editing) {
                        val new = prev.note.copy(content = command.content)
                        prev.copy(note = new)
                    } else {
                        prev
                    }
                }
            }

            is EditNoteCommand.EditTitle -> {
                _state.update { prev ->
                    if (prev is EditNoteState.Editing) {
                        val new = prev.note.copy(title = command.title)
                        prev.copy(note = new)
                    } else prev
                }
            }

            EditNoteCommand.Delete -> {
                viewModelScope.launch {
                    val value = _state.value
                    if (value is EditNoteState.Editing){
                        val id = value.note.id
                        deleteNoteUseCase.invoke(id)
                        EditNoteState.Finished
                    }
                }
            }
        }
    }
    init {
        viewModelScope.launch {
            val note = getNoteUseCase.invoke(id)
            _state.update {
                EditNoteState.Editing(note)
            }
        }
    }


}

sealed interface EditNoteState {
    data class Editing(val note: Note) : EditNoteState {
        val isSaveEnabled
            get() = note.title.isNotEmpty() && note.content.isNotEmpty()
    }

    data object Initial : EditNoteState
    data object Finished : EditNoteState
}

sealed interface EditNoteCommand {
    data object Back : EditNoteCommand
    data object Save : EditNoteCommand
    data object Delete : EditNoteCommand
    data class EditTitle(val title: String) : EditNoteCommand
    data class EditContent(val content: String) : EditNoteCommand
}