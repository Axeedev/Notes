package com.example.notes.presentation.screens.notes

import android.content.Context
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.R
import com.example.notes.domain.Note
import com.example.notes.presentation.ui.theme.OtherNotesColors
import com.example.notes.presentation.ui.theme.PinnedNotesColors
import com.example.notes.presentation.ui.theme.brown
import com.example.notes.presentation.ui.theme.white
import com.example.notes.utils.DateFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current.applicationContext,
    viewModel: NotesViewModel = viewModel{
        NotesViewModel(context)
    },
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
            ) {
                val color = OtherNotesColors[it.id % OtherNotesColors.size]
                NoteCard(
                    note = it,
                    onLongClick = { viewModel.processCommand(NotesCommand.SwitchedPinnedStatus(it.id)) },
                    backgroundColor = color,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp),
                    onNoteClick = onNoteClick
                )
                Spacer(Modifier.height(16.dp))
            }
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
                onClick = {onNoteClick.invoke(note)}
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
        Text(
            text = note.content,
            fontSize = 14.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
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
            onQueryChange.invoke(it)
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
