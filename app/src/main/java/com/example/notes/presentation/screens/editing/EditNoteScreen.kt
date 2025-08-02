package com.example.notes.presentation.screens.editing

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.example.notes.R
import com.example.notes.domain.ContentItem
import com.example.notes.presentation.ui.theme.brown
import com.example.notes.utils.DateFormatter
import java.nio.file.WatchEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(
    noteId: Int,
    viewModel: EditNoteViewModel = hiltViewModel(
        creationCallback = { factory: EditNoteViewModel.Factory ->
            factory.create(noteId)
        }
    ),
    onFinished: () -> Unit
) {

    val state = viewModel.state.collectAsState()
    val currentState = state.value
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        it?.let { uri ->
            viewModel.processCommand(EditNoteCommand.AddImage(uri.toString()))
        }

    }
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
                                modifier = Modifier
                                    .padding(
                                        start = 16.dp,
                                        end = 8.dp
                                    )
                                    .clickable {
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
                                        imagePicker.launch("image/*")
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
                                        onFinished.invoke()
                                    }
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
                    Content(
                        modifier = Modifier
                            .weight(2f),
                        content = currentState.note.content,
                        onTextChanged = {item, index ->
                            viewModel.processCommand(EditNoteCommand.EditContent(content = item, index = index,))
                        }
                    ) {
                        viewModel.processCommand(EditNoteCommand.RemoveImage(it))
                    }


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
            Box {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}


@Composable
fun Content(
    modifier: Modifier = Modifier,
    content: List<ContentItem>,
    onTextChanged: (String, Int) -> Unit,
    onCloseClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier
    ) {
        content.forEachIndexed { index, item ->
            item(key = index) {
                when (item) {
                    is ContentItem.ContentItemImage -> {
                        val hasPrev = index > 0 && content[index - 1] is ContentItem.ContentItemImage
                        content.takeIf { !hasPrev }
                            ?.drop(index)
                            ?.takeWhile { it is ContentItem.ContentItemImage }
                            ?.map { (it as ContentItem.ContentItemImage).url }
                            ?.let { urls ->
                                ImageGroup(
                                    urls = urls
                                ) {
                                    onCloseClick.invoke(it + index)
                                }
                            }
                    }


                    is ContentItem.ContentItemText -> {
                        TextFieldContent(
                            text = item.text,
                            onTextChanged = { onTextChanged.invoke(it, index) }
                        )
                    }
                }
            }
        }

    }

}


@Composable
fun ImageGroup(
    modifier: Modifier = Modifier,
    urls: List<String>,
    onCloseClick: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        urls.forEachIndexed { index, url ->

            ImageContent(
                modifier = Modifier
                    .weight(1f),
                url = url
            ) {
                onCloseClick.invoke(index)
            }
        }
    }
}

@Composable
fun ImageContent(
    modifier: Modifier = Modifier,
    url: String,
    onCloseClick: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        AsyncImage(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            model = url,
            contentDescription = "image from gallery",
            contentScale = ContentScale.FillWidth
        )
        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable { onCloseClick.invoke() },
            imageVector = Icons.Default.Close,
            contentDescription = "delete image"
        )
    }
}

@Composable
private fun TextFieldContent(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
        value = text,
        onValueChange = onTextChanged,
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

}


@Composable
@Preview
fun Show() {
    EditNoteScreen(
        noteId = 1
    ) {}
}