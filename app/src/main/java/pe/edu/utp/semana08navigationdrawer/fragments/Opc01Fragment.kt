package pe.edu.utp.semana08navigationdrawer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import pe.edu.utp.semana08navigationdrawer.R


class Opc01Fragment : Fragment() {
    companion object {
        var textBienvenido: String = ""
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var dataView = inflater.inflate(R.layout.fragment_opc01, container, false)

        dataView.findViewById<TextView>(R.id.tvOpc01)?.text = "${textBienvenido}"

        return dataView
    }


}