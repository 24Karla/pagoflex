package com.example.pagoflex.viewmodel

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import com.example.pagoflex.model.Perfil
import com.example.pagoflex.model.PerfilUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PerfilViewModel : ViewModel() {
    private val perfilEjemplo = Perfil(
        nombre = "Jesus Gonzales",
        ciudad = "Santiago, Chile",
        cuenta = "RUT 12345678-9",
        ntele = 986603607,
        orgRecom = "Panaderia Trigal",
        orgContac = true,
        reportAbierto = 1,
        reportActual = "No reconozco - Gimnasio Smartfit - En espera de respuesta"
    )

    private val _uiState = MutableStateFlow(PerfilUiState(perfil = perfilEjemplo))
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()
}