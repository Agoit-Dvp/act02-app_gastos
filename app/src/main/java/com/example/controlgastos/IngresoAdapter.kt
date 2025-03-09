package com.example.controlgastos

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.BaseAdapter

class IngresoAdapter(private val context: Context, private val ingresos: List<Ingreso>) :
    BaseAdapter() {

    override fun getCount(): Int = ingresos.size
    override fun getItem(position: Int): Any = ingresos[position]
    override fun getItemId(position: Int): Long = ingresos[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)

        val ingreso = ingresos[position]
        val text1 = view.findViewById<TextView>(android.R.id.text1)
        val text2 = view.findViewById<TextView>(android.R.id.text2)

        text1.text = ingreso.nombre
        text2.text = "Monto: ${ingreso.monto} - ${ingreso.fecha}"

        return view
    }
}

