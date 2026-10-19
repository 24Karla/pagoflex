package com.example.pagoflex.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pagoflex.model.Usuario
import com.example.pagoflex.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen(
    usuario: Usuario?,
    onVolver: () -> Unit,
    viewModel: PerfilViewModel = viewModel()
){
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val perfil = if (usuario == null) uiState.perfil else uiState.perfil.copy(
        nombre = usuario.nombre,
        ciudad = usuario.ciudad
    )
}