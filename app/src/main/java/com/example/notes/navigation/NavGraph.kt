package com.example.notes.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notes.presentation.screens.creation.CreateNoteScreen
import com.example.notes.presentation.screens.editing.EditNoteScreen
import com.example.notes.presentation.screens.notes.NotesScreen



@Composable
fun NavGraph(){

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Notes.route,
    ){
        composable(Screen.Notes.route){
            NotesScreen(
                onFABClick = {
                    navController.navigate(Screen.AddNote.route)
                }
            ){
                navController.navigate(Screen.EditNote.createRoute(it.id))
            }
        }

        composable(Screen.AddNote.route){
            CreateNoteScreen {
                navController.popBackStack()
            }
        }
        composable(Screen.EditNote.route){
            val noteId = it.arguments?.getString("note_id")?.toInt() ?: 0

            EditNoteScreen(
                noteId = noteId,
                onFinished = { navController.popBackStack() }
            )
        }
    }

}
sealed class Screen(val route: String){
    data object Notes : Screen("notes")
    data object AddNote : Screen("add_note")
    data object EditNote : Screen("edit_note/{note_id}"){
        fun createRoute(id: Int): String{
            return "edit_note/$id"
        }
        fun getNoteId(args: Bundle?) : Int{
            return args?.getString("note_id")?.toInt() ?: 0
        }

    }
}



