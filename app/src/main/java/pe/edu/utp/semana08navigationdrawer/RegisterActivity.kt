package pe.edu.utp.semana08navigationdrawer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class RegisterActivity : AppCompatActivity() {
    private lateinit var btnRegister: Button
    private lateinit var btnBack: Button

    private lateinit var etCorreo: EditText
    private lateinit var etContraseña: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initData()

        btnRegister.setOnClickListener {
            registerBasic()
        }

        btnBack.setOnClickListener {
            val homeIntent = Intent(this, LoginActivity::class.java)
            startActivity(homeIntent)
        }

    }
    fun initData() {
        btnRegister = findViewById(R.id.btnRegister)
        etCorreo = findViewById(R.id.etCorreo)
        etContraseña = findViewById(R.id.etContraseña)
        btnBack = findViewById(R.id.btnBack)
    }
    private fun registerBasic() {
        if (etCorreo.text.isNotEmpty() && etContraseña.text.isNotEmpty()) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                etCorreo.text.toString(),  etContraseña.text.toString()).addOnCompleteListener {
                if (it.isSuccessful) {
                    showAlert("Excelente","¡Registrado!")
                } else {
                    // Mostrar un mensaje descriptivo del error
                    val errorMsg  = when (val ex = it.exception) {
                        is FirebaseAuthException -> ex.message
                        else -> "Se ha producido un error."
                    }
                    showAlert("Error",errorMsg ?: "Se ha producido un error.")
                }

            }
        }
    }
    private fun showAlert(title: String, errorMsg: String)  {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¡${title}!")
        builder.setMessage("${errorMsg}")
        builder.setPositiveButton("Accept", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}