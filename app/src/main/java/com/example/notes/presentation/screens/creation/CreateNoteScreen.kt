package com.example.notes.presentation.screens.creation

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
fun CreateNoteScreen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current.applicationContext,
    createNoteViewModel: CreateNoteViewModel = viewModel{
        CreateNoteViewModel(context)
    },
    onFinished: () -> Unit
) {

    val state = createNoteViewModel.state.collectAsState()
    val currState = state.value
    when (currState) {
        is CreateNoteState.Creation -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            TitleCreateNote(
                                text = "Create Note",
                            )
                        },
                        navigationIcon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 8.dp)
                                    .clickable {
                                        createNoteViewModel.processCommand(CreateNotesCommand.Back)
                                        onFinished.invoke()
                                    }
                            )
                        },
                        actions = {
                            Icon(
                                painter = painterResource(R.drawable.ic_add_photo),
                                contentDescription = "add photo",
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .clickable { onFinished.invoke() }
                            )
                        }

                    )


                },
                floatingActionButton = {

                }
            ) { paddingValues ->

                Column(
                    modifier = modifier
                        .padding(paddingValues)
                ) {
                    TextField(
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp),
                        value = currState.title,
                        onValueChange = {
                            createNoteViewModel.processCommand(CreateNotesCommand.InputTitle(it))
                        },
                        placeholder = {
                            Text(
                                text = "Title",
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )

                    )
                    Text(
                        text = DateFormatter.currentDate.replace(".", "/"),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                    TextField(
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        value = currState.content,
                        onValueChange = {
                            createNoteViewModel.processCommand(CreateNotesCommand.InputContent(it))
                        },
                        placeholder = {
                            Text(
                                text = "Note something down or click on image to upload image",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                fontSize = 16.sp,
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                            .weight(2f)
                    )
                    Button(
                        onClick = {
                            createNoteViewModel.processCommand(CreateNotesCommand.Save)
                            onFinished.invoke()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = brown,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContainerColor = brown.copy(alpha = 0.1f),
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10),
                        enabled = currState.isSaveEnabled
                    ) {
                        Text(
                            text = "Save note"
                        )
                    }

                }
            }
        }

        CreateNoteState.Finished -> {

        }
    }
}

@Composable
fun TitleCreateNote(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )

}

@Composable
@Preview
fun Show() {
    CreateNoteScreen{}
}