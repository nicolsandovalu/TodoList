package com.example.todolist_pk.adapter

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist_pk.MainActivity
import com.example.todolist_pk.model.Tarea
import com.example.todolist_pk.R
import kotlin.toString


class TareasAdapter(
    private val tareas: MutableList<Tarea>,
    private val onCheckedChanged: (Tarea) -> Unit,
    private val onCambio: (() -> Unit)? = null,
    private val onItemRemoved: (() -> Unit)? = null // Nuevo callback para eliminación
) : RecyclerView.Adapter<TareasAdapter.TareaViewHolder>() {

    inner class TareaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cbCompletada: CheckBox = itemView.findViewById(R.id.cbCompletada)
        private val tvTextoTarea: TextView = itemView.findViewById(R.id.tvTextoTarea)
        private var currentTarea: Tarea? = null

        init {
            cbCompletada.setOnCheckedChangeListener { _, isChecked ->
                currentTarea?.let { tarea ->
                    onCheckedChanged(tarea.copy(completada = isChecked))
                }
            }
        }

        fun bind(tarea: Tarea) {
            currentTarea = tarea
            tvTextoTarea.text = tarea.texto
            // Evita notificaciones no deseadas
            cbCompletada.setOnCheckedChangeListener(null)
            cbCompletada.isChecked = tarea.completada
            cbCompletada.setOnCheckedChangeListener { _, isChecked ->
                onCheckedChanged(tarea.copy(completada = isChecked))
            }

            itemView.setOnClickListener {
                showEditDialog(tarea)
            }
        }

        private fun showEditDialog(tarea: Tarea) {
            EditText(itemView.context).apply {
                setText(tarea.texto)
                AlertDialog.Builder(itemView.context)
                    .setTitle("Editar tarea")
                    .setView(this)
                    .setPositiveButton("Guardar") { _, _ ->
                        onCheckedChanged(tarea.copy(texto = text.toString()))
                        onCambio?.invoke()
                    }
                    .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        return TareaViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_tarea, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        holder.bind(tareas[position])
    }

    override fun getItemCount(): Int = tareas.size

    fun eliminarTarea(position: Int) {
        if (position in 0 until tareas.size) {
            tareas.removeAt(position)
            notifyItemRemoved(position)
            onItemRemoved?.invoke() // Notificar que se eliminó un item
        }
    }

    fun actualizarLista(nuevaLista: List<Tarea>) {
        tareas.clear()
        tareas.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    fun getTareas(): List<Tarea> = tareas.toList()
}