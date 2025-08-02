package com.example.notes.presentation.screens.creation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.domain.AddNoteUseCase
import com.example.notes.domain.ContentItem
import com.example.notes.domain.ContentItem.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val addNoteUseCase: AddNoteUseCase,
): ViewModel() {


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
                       val newContent = prev.content.mapIndexed { index, item ->
                           if (index == command.index && item is ContentItem.ContentItemText){
                               item.copy(text = command.content)
                           }else{
                               item
                           }
                       }
                       prev.copy(content = newContent)
                   }else prev
                }
            }
            is CreateNotesCommand.InputTitle -> {
                _state.update { prev ->
                    if (prev is CreateNoteState.Creation){
                        prev.copy(
                            title = command.title
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
                            val content = prev.content.filter {
                                it !is ContentItem.ContentItemText || it.text.isNotBlank()
                            }

                            addNoteUseCase.invoke(title, content,false, System.currentTimeMillis())
                            CreateNoteState.Finished
                        } else prev
                    }
                }
            }

            is CreateNotesCommand.AddImage -> {
                _state.update {prev ->
                    if (prev is CreateNoteState.Creation){
                        val newList = prev.content.toMutableList().apply {
                            val last = last()
                            if (last is ContentItem.ContentItemText && last.text.isBlank()){
                                removeAt(lastIndex)
                            }
                            add(ContentItemImage(url = command.uri.toString()))
                            add(ContentItemText(""))

                        }
                        prev.copy(content = newList)

                    }
                    else prev

                }
            }

            is CreateNotesCommand.DeleteImage -> {

                _state.update { prev ->
                    if (prev is CreateNoteState.Creation){
                        val newList = prev.content.toMutableList().apply {
                            removeAt(command.index)
                        }
                        prev.copy(content = newList)
                    }else{
                        prev
                    }
                }
            }
        }
    }


}

sealed interface CreateNotesCommand{
    data class InputTitle(val title: String) : CreateNotesCommand
    data class InputContent(val content: String, val index: Int) : CreateNotesCommand
    data object Save : CreateNotesCommand
    data object Back : CreateNotesCommand
    data class AddImage(val uri: Uri) : CreateNotesCommand
    data class DeleteImage(val index: Int) : CreateNotesCommand
}
sealed interface CreateNoteState{
    data class Creation(
        val title: String = "",
        val content: List<ContentItem> = listOf(ContentItem.ContentItemText("")),

    ): CreateNoteState{
        val isSaveEnabled: Boolean
            get() = when{
                title.isEmpty() -> false
                content.isEmpty() -> false
                else -> {
                    content.any{
                        it !is ContentItem.ContentItemText || it.text.isNotBlank()
                    }
                }
            }
    }
    data object Finished : CreateNoteState
}