package com.example.notes.presentation.screens.editing

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.R
import com.example.notes.presentation.ui.theme.brown
import com.example.notes.utils.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    modifier: Modifier = Modifier,
    noteId: Int,
    context: Context = LocalContext.current.applicationContext,
    viewModel: EditNoteViewModel = viewModel {
        EditNoteViewModel(noteId, context)
    },
    onFinished: () -> Unit
) {

    val state = viewModel.state.collectAsState()
    val currentState = state.value
    when (currentState) {
        is EditNoteState.Editing -> {
            val note = currentState.note
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Edit note",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        },
                        navigationIcon = {
                            Icon(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 8.dp
                                ).clickable{
                                    onFinished.invoke()
                                },

                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Go back"
                            )
                        },
                        actions = {
                            Icon(
                                painter = painterResource(R.drawable.ic_add_photo),
                                contentDescription = "Add photo",
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .clickable {

                                    },
                                tint = Color.Black
                            )
                            Icon(
                                painter = painterResource(R.drawable.vector),
                                contentDescription = "delete note",
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clickable {
                                        viewModel.processCommand(EditNoteCommand.Delete)
                                        onFinished.invoke() }
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = MaterialTheme.colorScheme.onSurface
                        )

                    )
                }
            )
            { paddingValues ->

                Column(
                    modifier = Modifier.padding(paddingValues)
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        value = note.title,
                        onValueChange = { viewModel.processCommand(EditNoteCommand.EditTitle(it)) },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(
                                text = "Title",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = DateFormatter.format(note.updatedAt),
                        modifier = Modifier.padding(start = 24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    TextField(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        value = note.content,
                        onValueChange = {
                            viewModel.processCommand(EditNoteCommand.EditContent(it))
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = {
                            Text(
                                text = "Note something down or click on image to upload image",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                fontSize = 16.sp,
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp),
                        onClick = {
                            viewModel.processCommand(EditNoteCommand.Save)

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = brown
                        ),
                        shape = RoundedCornerShape(10)
                    ) {
                        Text("Save Note")
                    }
                }
            }

        }

        EditNoteState.Finished -> {
            LaunchedEffect(key1 = Unit) {
                onFinished()
            }
        }

        EditNoteState.Initial -> {

        }
    }


}

@Composable
@Preview
fun Show() {
    EditNoteScreen(
        noteId = 1
    ) {}
}