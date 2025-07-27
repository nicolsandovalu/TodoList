package com.example.todolist_pk

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist_pk.adapter.TareasAdapter
import com.example.todolist_pk.model.Tarea
import com.example.todolist_pk.utils.SwipeToDeleteCallback
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: TareasAdapter
    private val listaTareas = mutableListOf<Tarea>()
    private val tareasVisibles = mutableListOf<Tarea>()
    private var filtroActual: String = "Todas"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etNuevaTarea = findViewById<EditText>(R.id.etNuevaTarea)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        val rvTareas = findViewById<RecyclerView>(R.id.rvTareas)
        val tvContador = findViewById<TextView>(R.id.tvContador)
        val spinnerFiltro = findViewById<Spinner>(R.id.spinnerFiltro)
        val opciones = listOf("Todas", "Pendientes", "Completadas")
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFiltro.adapter = adapterSpinner
        rvTareas.layoutManager = LinearLayoutManager(this).apply{
            orientation = RecyclerView.VERTICAL
        }

        spinnerFiltro.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected (
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
            ){
            aplicarFiltro(opciones[position])
        }
            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }


        adapter = TareasAdapter(
            tareasVisibles,
            onCheckedChanged = { tareaModificada ->
                val index = listaTareas.indexOfFirst { it.id == tareaModificada.id }

                if (index != -1) {
                    listaTareas[index] = tareaModificada
                    aplicarFiltro(filtroActual)
                }
            },
            onCambio = {
                aplicarFiltro(filtroActual)
            }

        )


        rvTareas.layoutManager = LinearLayoutManager(this)
        rvTareas.adapter = adapter

        val swipeHandler = SwipeToDeleteCallback { position ->
            val tareaEliminada = tareasVisibles[position]
            listaTareas.removeAll { it.id == tareaEliminada.id }
            adapter.eliminarTarea(position)
            actualizarContador(tvContador)

            // Actualiza la lista principal y muestra snackbar
            listaTareas.removeAll { it.id == tareaEliminada.id }
            eliminarTarea(tareaEliminada, position)
            actualizarContador(tvContador)

        }

        ItemTouchHelper(swipeHandler).attachToRecyclerView(rvTareas)

        btnAgregar.setOnClickListener {
            val texto = etNuevaTarea.text.toString().trim()
            if (texto.isNotEmpty()) {
                val nuevaTarea = Tarea(System.currentTimeMillis(), texto)
                listaTareas.add(nuevaTarea) //primero añade a la lista principal
                aplicarFiltro(filtroActual)
                etNuevaTarea.text.clear()

            }
        }

        aplicarFiltro(filtroActual)
        actualizarContador(tvContador)
    }

    private fun eliminarTarea( tarea: Tarea, position: Int) {
        listaTareas.removeAll { it.id == tarea.id}
        aplicarFiltro(filtroActual)

        Snackbar.make(
            findViewById(android.R.id.content),
            "Tarea eliminada",
            Snackbar.LENGTH_LONG
        ).apply{
            setAction("DESHACER") {
                listaTareas.add(position, tarea)
                aplicarFiltro(filtroActual)
            }
            setActionTextColor(ContextCompat.getColor(this@MainActivity, android.R.color.white))
            show()
            }
    }

    private fun actualizarContador(tv: TextView) {
        val pendientes = adapter.getTareas().count { !it.completada }
        tv.text = "Tareas pendientes: $pendientes"
    }

    private fun aplicarFiltro(filtro: String){
        filtroActual = filtro
        val filtradas = when(filtro){
            "Pendientes" -> listaTareas.filter { !it.completada }
            "Completadas" -> listaTareas.filter { it.completada }
            else -> listaTareas
        }



        adapter.actualizarLista(filtradas)
        actualizarContador(findViewById(R.id.tvContador))

    }
}