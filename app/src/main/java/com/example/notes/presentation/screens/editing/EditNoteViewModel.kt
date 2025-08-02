package com.example.notes.presentation.screens.editing

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NotesRepositoryIml
import com.example.notes.domain.ContentItem
import com.example.notes.domain.ContentItem.*
import com.example.notes.domain.DeleteNoteUseCase
import com.example.notes.domain.EditNoteUseCase
import com.example.notes.domain.GetNoteUseCase
import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = EditNoteViewModel.Factory::class)
class EditNoteViewModel @AssistedInject constructor(
    @Assisted("id") private val id: Int,
    private val editNoteUseCase: EditNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase
) : ViewModel() {


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
                        val newContent = prev.note.content.mapIndexed { index, item ->
                            if (index == command.index){
                                ContentItemText(command.content)
                            }
                            else item
                        }
                        val new = prev.note.copy(content = newContent)
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
                    if (value is EditNoteState.Editing) {
                        val id = value.note.id
                        deleteNoteUseCase.invoke(id)
                        EditNoteState.Finished
                    }
                }
            }

            is EditNoteCommand.AddImage -> {
                _state.update { prev ->
                    if (prev is EditNoteState.Editing){
                            val new = prev.note.content.toMutableList().apply {
                            val last = last()
                            if (last is ContentItemText && last.text.isBlank()){
                                removeAt(lastIndex)
                            }
                            add(ContentItemImage(command.url))
                            add(ContentItemText(""))

                        }
                        prev.copy(prev.note.copy(content = new))


                    }
                    else prev

                }
            }
            is EditNoteCommand.RemoveImage -> {
                _state.update {prev ->
                    if (prev is EditNoteState.Editing){
                        prev.note.content.toMutableList().apply {
                            removeAt(command.index)
                        }.let {
                            prev.copy(prev.note.copy(content = it))
                        }
                    }
                    else prev
                }

            }
        }
    }

    init {
        viewModelScope.launch {
            _state.update {

                val note = getNoteUseCase.invoke(id)
                val content = if (note.content.lastOrNull() !is ContentItem.ContentItemText){
                    note.content + ContentItem.ContentItemText("")
                }
                else{
                    note.content
                }
                EditNoteState.Editing(note.copy(content = content))
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("id") noteId: Int
        ): EditNoteViewModel
    }


}

sealed interface EditNoteState {
    data class Editing(val note: Note) : EditNoteState {
        val isSaveEnabled: Boolean
            get() {
                return when {
                    note.title.isBlank() -> false
                    note.content.isEmpty() -> false
                    else ->{
                        note.content.any {contentItem ->
                            contentItem !is ContentItem.ContentItemText || contentItem.text.isNotBlank()
                        }

                    }
                }
            }
    }

    data object Initial : EditNoteState
    data object Finished : EditNoteState
}

sealed interface EditNoteCommand {
    data object Back : EditNoteCommand
    data object Save : EditNoteCommand
    data object Delete : EditNoteCommand
    data class EditTitle(val title: String) : EditNoteCommand
    data class EditContent(val content: String, val index: Int) : EditNoteCommand
    data class AddImage(val url: String) : EditNoteCommand
    data class RemoveImage(val index: Int) : EditNoteCommand
}