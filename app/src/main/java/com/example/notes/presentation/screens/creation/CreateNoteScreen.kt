package com.example.notes.presentation.screens.creation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(
    modifier: Modifier = Modifier,
    createNoteViewModel: CreateNoteViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {

    val state = createNoteViewModel.state.collectAsState()
    val currState = state.value
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) {
        it?.let { createNoteViewModel.processCommand(CreateNotesCommand.AddImage(it)) }

    }


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
                                    .clickable { imagePicker.launch("image/*") }
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


                    Content(
                        modifier = Modifier.weight(1f),
                        content = currState.content,
                        onTextChange = {text, index ->
                            createNoteViewModel.processCommand(CreateNotesCommand.InputContent(text,index))
                        }
                    ) {
                        createNoteViewModel.processCommand(CreateNotesCommand.DeleteImage(it))

                    }
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
fun Content(
    modifier: Modifier = Modifier,
    content: List<ContentItem>,
    onTextChange: (String, Int) -> Unit,
    onCloseClick: (Int) -> Unit
){
    LazyColumn(
        modifier = modifier
    ) {
        content.forEachIndexed { index, item ->
            item(key = index){
                when(item) {
                    is ContentItem.ContentItemText -> {
                        TextFieldItem(
                            text = item.text
                        ){
                           onTextChange.invoke(it, index)
                        }
                    }
                    is ContentItem.ContentItemImage -> {
                        val isPrev = index > 0 && content[index-1] is ContentItem.ContentItemImage

                        content
                            .takeIf { !isPrev }
                            ?.drop(index)
                            ?.takeWhile { it is ContentItem.ContentItemImage }
                            ?.map { (it as ContentItem.ContentItemImage).url }
                            ?.let { urls ->
                                GroupImages(
                                    urls = urls
                                ){
                                    onCloseClick.invoke(it+index)
                                }
                            }

                    }
                }
            }

        }

    }
}

@Composable
fun GroupImages(
    modifier: Modifier = Modifier,
    urls: List<String>,
    onDeleteImageClick: (Int) -> Unit
){

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        urls.forEachIndexed {index, url ->
            ImageContent(
                modifier = Modifier
                    .weight(1f),
                url = url
            ) {
                onDeleteImageClick.invoke(index)
            }
        }
    }

}



@Composable
fun ImageContent(
    modifier: Modifier = Modifier,
    url: String,
    onCloseClick: () -> Unit
){
    Box(
        modifier = modifier
    ){
        AsyncImage(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp)),
            model = url,
            contentDescription = "image",
            contentScale = ContentScale.FillWidth
        )
        Icon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(24.dp)
                .clickable{onCloseClick.invoke()},
            imageVector = Icons.Default.Close,
            contentDescription = "remove image",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}



@Composable
fun TextFieldItem(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit
){
    TextField(
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        value = text,
        onValueChange = onTextChange,
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

    )
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