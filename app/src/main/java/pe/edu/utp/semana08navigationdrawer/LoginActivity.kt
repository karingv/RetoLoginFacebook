package pe.edu.utp.semana08navigationdrawer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import java.security.Provider

class LoginActivity : AppCompatActivity() {
    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button
    private lateinit var etCorreo: EditText
    private lateinit var etContraseña: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin)
        etCorreo = findViewById(R.id.etCorreo)
        etContraseña = findViewById(R.id.etContraseña)
        initData()
    }

    fun initData() {
        btnRegister.setOnClickListener {
            if (etCorreo.text.isNotEmpty() && etContraseña.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    etCorreo.text.toString(),
                    etContraseña.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }

                }


            }
        }

    }

    private fun showHome( email: String, provider: ProviderType) {
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", etCorreo.toString())
            putExtra("provider", provider.name)

        }
        startActivity(homeIntent)
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se he producido un error ")
        builder.setPositiveButton("Acce", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


}