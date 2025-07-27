package com.example.todolist_pk.model

data class Tarea (
    var id: Long,
    var texto: String,
    var completada: Boolean = false,
    var fechaCreacion: Long = System.currentTimeMillis()

)