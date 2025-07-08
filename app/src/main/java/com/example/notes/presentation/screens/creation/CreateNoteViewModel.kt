package com.example.notes.presentation.screens.creation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NotesRepositoryIml
import com.example.notes.domain.AddNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateNoteViewModel(context: Context): ViewModel() {
    private val repository = NotesRepositoryIml.getInstance(context)

    private val addNoteUseCase = AddNoteUseCase(repository)


    private val _state : MutableStateFlow<CreateNoteState> = MutableStateFlow(CreateNoteState.Creation())
    val state
        get() = _state.asStateFlow()

    fun processCommand(command: CreateNotesCommand){
        when(command) {
            CreateNotesCommand.Back -> {
                _state.update {
                    CreateNoteState.Finished
                }
            }
            is CreateNotesCommand.InputContent -> {
                _state.update { prev ->
                   if (prev is CreateNoteState.Creation){
                       prev.copy(
                           content = command.content,
                           isSaveEnabled = prev.title.isNotBlank() && command.content.isNotBlank()
                       )
                   }else CreateNoteState.Creation(content = command.content)
                }
            }
            is CreateNotesCommand.InputTitle -> {
                _state.update { prev ->
                    if (prev is CreateNoteState.Creation){
                        prev.copy(
                            title = command.title,
                            isSaveEnabled = prev.content.isNotBlank() && command.title.isNotBlank()
                            )
                    }
                    else{
                        CreateNoteState.Creation(title = command.title)
                    }

                }

            }
            CreateNotesCommand.Save -> {
                viewModelScope.launch {
                    _state.update { prev ->
                        if (prev is CreateNoteState.Creation) {
                            val title = prev.title
                            val content = prev.content
                            addNoteUseCase.invoke(title, content,false, System.currentTimeMillis())
                            CreateNoteState.Finished
                        } else prev
                    }
                }
            }
        }
    }


}

sealed interface CreateNotesCommand{
    data class InputTitle(val title: String) : CreateNotesCommand
    data class InputContent(val content: String) : CreateNotesCommand
    data object Save : CreateNotesCommand
    data object Back : CreateNotesCommand
}
sealed interface CreateNoteState{
    data class Creation(
        val title: String = "",
        val content: String = "",
        val isSaveEnabled: Boolean = false
    ) : CreateNoteState
    data object Finished : CreateNoteState
}