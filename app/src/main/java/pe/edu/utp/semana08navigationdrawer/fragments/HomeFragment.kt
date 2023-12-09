package pe.edu.utp.semana08navigationdrawer.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import pe.edu.utp.consumoserviciosclima.modelo.Ciudad
import pe.edu.utp.semana08navigationdrawer.Network
import pe.edu.utp.semana08navigationdrawer.R


class HomeFragment : Fragment() {

    private lateinit var etCiudad: EditText
    private lateinit var btnClima: Button
    private lateinit var tvCiudad: TextView
    private lateinit var tvTemperatura: TextView
    private lateinit var tvDescripcion: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootview= inflater.inflate(R.layout.fragment_home, container, false)

        inicializarComponentes(rootview)


        return rootview
    }

    private fun inicializarComponentes(view: View) {

        etCiudad = view.findViewById(R.id.etCiudad)
        btnClima = view.findViewById(R.id.btnClima)
        tvCiudad = view.findViewById(R.id.tvCiudad)
        tvTemperatura = view.findViewById(R.id.tvTemperatura)
        tvDescripcion = view.findViewById(R.id.tvDescripcion)

        btnClima.setOnClickListener {
                    val nombreCiudad = this.etCiudad.text
                    val url = "https://api.openweathermap.org/data/2.5/weather?q="+nombreCiudad+"&appid=9c90e8359399e76dbdc4121e8001772e&units=metric&lang=es"
                    this.solicitudClimaHttp(url)
        }

    }

    private fun solicitudClimaHttp(url: String){
        val queue = Volley.newRequestQueue(context as AppCompatActivity)
        val solicitud = StringRequest(Request.Method.GET, url,
            Response.Listener<String>{
                    response ->
                try {
                    Log.d("solicitudHttp", response)

                    val gson = Gson()
                    val ciudad = gson.fromJson(response, Ciudad::class.java)
                    this.tvCiudad.text = ciudad.name
                    this.tvTemperatura.text = ciudad.main?.temp.toString()
                    this.tvDescripcion.text = ciudad.weather?.get(0)?.description

                }catch (e: Exception){

                }

            }, Response.ErrorListener {

            })
        queue.add(solicitud)

    }

}