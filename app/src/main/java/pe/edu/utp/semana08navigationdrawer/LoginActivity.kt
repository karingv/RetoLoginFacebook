package pe.edu.utp.semana08navigationdrawer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException


class LoginActivity : AppCompatActivity() {
    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button
    private lateinit var etCorreo: EditText
    private lateinit var etContraseña: EditText
    private lateinit var btnFacebook: Button
    private var callbackManager = CallbackManager.Factory.create();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)

        initData()
        btnRegister.setOnClickListener {
            if (etCorreo.text.isNotEmpty() && etContraseña.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    etCorreo.text.toString(),  etContraseña.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "" ,it.result?.user?.displayName ?: "" , ProviderType.BASIC)
                    } else {
                        // Mostrar un mensaje descriptivo del error
                        val errorMsg  = when (val ex = it.exception) {
                            is FirebaseAuthException -> ex.message
                            else -> "Se ha producido un error."
                        }
                        showAlert(errorMsg ?: "Se ha producido un error.")

                    }

                }
            }
        }
        btnLogin.setOnClickListener {
            if (etCorreo.text.isNotEmpty() && etContraseña.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    etCorreo.text.toString(),  etContraseña.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "" ,it.result?.user?.displayName ?: "" , ProviderType.BASIC)
                    } else {
                        // Mostrar un mensaje descriptivo del error
                        val errorMsg  = when (val ex = it.exception) {
                            is FirebaseAuthException -> ex.message
                            else -> "Se ha producido un error."
                        }
                        showAlert(errorMsg ?: "Se ha producido un error.")

                    }

                }
            }
        }

        btnFacebook.setOnClickListener {

            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {

                    override fun onCancel() {
                        // App code
                    }

                    override fun onError(error: FacebookException) {
                        // Mostrar un mensaje descriptivo del error
                        showAlert(error.toString() ?: "Se ha producido un error.")
                    }

                    override fun onSuccess(result: LoginResult) {
                        result?.let {
                            val token = it.accessToken
                            val credential = FacebookAuthProvider.getCredential(token.token)
                            FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    showHome(it.result?.user?.email ?: "" ,it.result?.user?.displayName ?: "" , ProviderType.FACEBOOK)
                                } else {
                                    // Mostrar un mensaje descriptivo del error
                                    val errorMsg  = when (val ex = it.exception) {
                                        is FirebaseAuthException -> ex.message
                                        else -> "Se ha producido un error."
                                    }
                                    showAlert(errorMsg ?: "Se ha producido un error.")

                                }

                            }
                        }
                    }

                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun initData() {
        btnRegister = findViewById(R.id.btnRegister)
        btnLogin = findViewById(R.id.btnLogin)
        etCorreo = findViewById(R.id.etCorreo)
        etContraseña = findViewById(R.id.etContraseña)
        btnFacebook = findViewById<Button>(R.id.btnFacebook)
    }

    private fun showHome( email: String, name: String, provider: ProviderType) {
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("name", name)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }

    private fun showAlert(errorMsg: String)  {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("${errorMsg}")
        builder.setPositiveButton("Accept", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


}