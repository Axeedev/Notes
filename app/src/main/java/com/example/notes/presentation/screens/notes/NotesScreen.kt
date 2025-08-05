package com.example.notes.presentation.screens.notes

import android.content.Context
import android.text.Layout
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.notes.R
import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note
import com.example.notes.presentation.ui.theme.OtherNotesColors
import com.example.notes.presentation.ui.theme.PinnedNotesColors
import com.example.notes.presentation.ui.theme.brown
import com.example.notes.presentation.ui.theme.white
import com.example.notes.utils.DateFormatter
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = hiltViewModel(),
    onFABClick: () -> Unit,
    onNoteClick: (Note) -> Unit
) {
    val screenState by viewModel.state.collectAsState()
    Log.d("NotesScreen", "recomposition")
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onFABClick.invoke()
                },
                containerColor = brown,
                contentColor = white,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "add notes"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize(),
            contentPadding = innerPadding
        ) {
            item {
                Title(text = "All Notes", modifier = Modifier.padding(start = 24.dp, top = 16.dp))
            }
            item { Spacer(Modifier.height(16.dp)) }
            item {
                SearchBar(query = screenState.query) {
                    Log.d("NotesScreen", it)
                    viewModel.processCommand(NotesCommand.InputSearchQuery(it))
                }
            }

            if (screenState.pinnedNotes.isNotEmpty()) {

                item { Spacer(Modifier.height(16.dp)) }
                item {
                    SubTitle(
                        text = "Pinned",
                        modifier = Modifier.padding(start = 24.dp)
                    )
                }
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp),
                    ) {
                        items(
                            screenState.pinnedNotes,
                            key = { it.id }
                        ) { note ->

                            val color = PinnedNotesColors[note.id % PinnedNotesColors.size]
                            NoteCard(
                                modifier = Modifier
                                    .widthIn(min = 140.dp, max = 160.dp),
                                note = note,
                                onLongClick = {
                                    viewModel.processCommand(
                                        NotesCommand.SwitchedPinnedStatus(
                                            note.id
                                        )
                                    )
                                },
                                onNoteClick = onNoteClick,
                                backgroundColor = color
                            )

                        }
                    }
                }
            }
            item { Spacer(Modifier.height(16.dp)) }

            item { Spacer(Modifier.height(24.dp)) }

            item {
                SubTitle(
                    text = "Others",
                    modifier = Modifier.padding(start = 24.dp)
                )
            }
            item { Spacer(Modifier.height(16.dp)) }
            items(
                screenState.unpinnedNotes,
                key = { it.id }
            ) { note ->
                val color = OtherNotesColors[note.id % OtherNotesColors.size]
                val imageContent = note.content.filterIsInstance<ContentItem.ContentItemImage>()
                if (imageContent.isNotEmpty()) {
                    NoteCardWithImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
                        imageUrl = imageContent[0].url,
                        note = note,
                        onNoteClick = onNoteClick
                    ) { viewModel.processCommand(NotesCommand.SwitchedPinnedStatus(note.id)) }
                } else {
                    NoteCard(
                        note = note,
                        onLongClick = {
                            viewModel.processCommand(
                                NotesCommand.SwitchedPinnedStatus(
                                    note.id
                                )
                            )
                        },
                        backgroundColor = color,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 24.dp, end = 24.dp),
                        onNoteClick = onNoteClick
                    )
                    Spacer(Modifier.height(16.dp))
                }
                Log.d("NotesViewModel", note.toString())
            }

        }
    }


}

@Composable
fun NoteCardWithImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    note: Note,
    onNoteClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit
) {
    Box(
        modifier = modifier
            .combinedClickable(
                onLongClick = { onLongClick.invoke(note) },
                onClick = { onNoteClick.invoke(note) }
            )
    ) {
        AsyncImage(
            modifier = Modifier
                .heightIn(max = 150.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            model = imageUrl,
            contentScale = ContentScale.FillWidth,
            contentDescription = "Image",

        )
        Column(
            modifier = Modifier
                .padding(all = 16.dp)
                .fillMaxWidth()

                .align(Alignment.BottomStart)
        ) {

            Text(
                text = note.title,
                fontSize = 14.sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(
                modifier = Modifier
                    .height(8.dp)
            )
            Text(
                text = DateFormatter.format(note.updatedAt),
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backgroundColor: Color,
    onLongClick: (Note) -> Unit,
    onNoteClick: (Note) -> Unit
) {

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .combinedClickable(
                onLongClick = {
                    onLongClick.invoke(note)
                },
                onClick = { onNoteClick.invoke(note) }
            )
            .padding(16.dp)

    ) {
        Text(
            text = note.title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = DateFormatter.format(note.updatedAt),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        note.content
            .filterIsInstance<ContentItem.ContentItemText>()
            .joinToString("\n") { it.text }
            .let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

    }
}

@Composable
fun Title(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )

}

@Composable
fun SubTitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        text = text,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )

}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(all = 24.dp)
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(10),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
        value = query,
        onValueChange = {
            Log.d("NotesScreen", "recomposition")
            onQueryChange(it)
        },
        placeholder = {
            Text(
                "Search...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search notes",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = RoundedCornerShape(10),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}

@Preview
@Composable
fun Show() {
    NotesScreen(onFABClick = {}) {}
}
