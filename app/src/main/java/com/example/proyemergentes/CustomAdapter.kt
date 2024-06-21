package com.example.proyemergentes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyemergentes.dataclass.Horarios

class CustomAdapter(
    private var context: Context,
    private var listarHorarios: ArrayList<Horarios>
) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private var onClick: OnItemClicked? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listarHorarios.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val horario = listarHorarios[position]
        holder.itemTitle.text ="Modelo: ${horario.BusID.Modelo}"
        holder.itemDescrip.text ="Horario ${horario.HoraSalida}"
        holder.itemDestino.text="Rumbo a ${horario.RutaID.Destino}"
        holder.itemCapacida.text="Capacidad de ${horario.BusID.Capacidad} asientos"
        val imagenResource = when (horario.RutaID.Destino) {
            "Bermejo" -> R.drawable.bus2
            "Yacuiba" -> R.drawable.bus3
            "Villamontes" -> R.drawable.bus4
            // Agrega más casos según tus necesidades
            else -> R.drawable.bus5 // Imagen por defecto o manejo de caso no definido
        }
        holder.itemImage.setImageResource(imagenResource)
        holder.btnCancelar.setOnClickListener {
            onClick?.pasarHorario(horario)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView = itemView.findViewById(R.id.citem_img)
        var itemTitle: TextView = itemView.findViewById(R.id.title_view)
        var itemDescrip: TextView = itemView.findViewById(R.id.title_descripcion)
        var itemDestino: TextView = itemView.findViewById(R.id.IDDestino)
        var itemCapacida: TextView = itemView.findViewById(R.id.idCapacidad)

        var btnCancelar: ImageButton = itemView.findViewById(R.id.btnreservas)
    }

    interface OnItemClicked {
        fun pasarHorario(horario: Horarios)

    }

    fun setOnClick(onClick: OnItemClicked?) {
        this.onClick = onClick
    }
}
