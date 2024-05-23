package com.dosetsu.monatree.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.dosetsu.monatree.R
import com.dosetsu.monatree.model.Anak
import com.google.firebase.database.FirebaseDatabase

class AnakSpinnerAdapter(
    private val context: Context,
    private val anakList: List<Anak>
) : ArrayAdapter<Anak>(context, android.R.layout.simple_spinner_item, anakList) {

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getCount(): Int {
        return anakList.size
    }

    override fun getItem(position: Int): Anak? {
        return anakList[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.spinner_anak_item, parent, false
        )

        val anak = anakList[position]
        view.findViewById<TextView>(R.id.tvNameAnak).text = "Ponsel ${anak.name}"

        return view
    }
}




