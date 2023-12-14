package pe.edu.utp.semana08navigationdrawer

import android.app.Activity
import android.content.Context
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
import com.facebook.FacebookSdk
import com.facebook.Profile
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider

class LoginActivity : AppCompatActivity() {
    private val GOOGLE_SIGN_IN = 100

    private lateinit var btnCrearCuenta: Button
    private lateinit var btnLogin: Button
    private lateinit var etCorreo: EditText
    private lateinit var etContraseña: EditText
    private lateinit var btnFacebook: Button
    private lateinit var btnGithub: Button
    private lateinit var btnGoogle: Button
    private var callbackManager = CallbackManager.Factory.create();

    private lateinit var firebaseAuth :FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FacebookSdk.sdkInitialize(applicationContext)
        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()

        session()

        initData()
        btnCrearCuenta.setOnClickListener {
            val homeIntent = Intent(this, RegisterActivity::class.java)
            startActivity(homeIntent)
        }
        btnLogin.setOnClickListener {
            if (etCorreo.text.isNotEmpty() && etContraseña.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    etCorreo.text.toString(),  etContraseña.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "" ,it.result?.user?.displayName ?: "" , ProviderType.BASIC, "")
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

            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email","public_profile"))
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
                                    val profile= Profile.getCurrentProfile()
                                    if (profile != null) {
                                        showHomeWithFacebook(profile,it.result?.user?.email ?: "",ProviderType.FACEBOOK, it.result?.user?.displayName ?: "")
                                    }

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

        btnGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }

        btnGithub.setOnClickListener {

            if (etCorreo.text.isNotEmpty() && etContraseña.text.isNotEmpty()) {

            }else {

                val provider = OAuthProvider.newBuilder("github.com")

                provider.addCustomParameter("login", etCorreo.text.toString())

                provider.scopes = listOf("user:email")

                val pendingResultTask = firebaseAuth.pendingAuthResult
                if (pendingResultTask != null) {
                    // There's something already here! Finish the sign-in for your user.
                    pendingResultTask
                        .addOnSuccessListener {authResult ->

                            val user = authResult.user
                            val email = user?.email ?: ""
                            val name = user?.displayName ?: ""
                            val profilePicUrl = user?.photoUrl?.toString() ?: ""

                            // Llamar a la función para mostrar la página principal
                            showHome(email, name, ProviderType.GITHUB, profilePicUrl)

                        }
                        .addOnFailureListener {
                            Log.d("github", "Error al iniciar sesión con GitHub: $it")
                        }
                } else {

                    firebaseAuth
                        .startActivityForSignInWithProvider(this, provider.build())
                        .addOnSuccessListener {authResult ->

                            val user = authResult.user
                            val email = user?.email ?: ""
                            val name = user?.displayName ?: ""
                            val profilePicUrl = user?.photoUrl?.toString() ?: ""

                            // Llamar a la función para mostrar la página principal
                            showHome(email, name, ProviderType.GITHUB, profilePicUrl)
                        }
                        .addOnFailureListener {
                            Log.d("github", "Error al iniciar sesión con GitHub: $it")
                        }
                }
            }

        }
    }

    private fun session(){
        val prefs =getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email=prefs.getString("email",null)
        val provider=prefs.getString("provider", null)

        if(email!= null && provider!=null){
            showHome( email, "",ProviderType.valueOf(provider),"")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account= task.getResult(ApiException::class.java)

                if(account != null) {

                    val credential= GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener{
                        if(it.isSuccessful){
                            val profilePicUrl = account.photoUrl?.toString() ?: ""
                            showHome(account.email ?: "", account.displayName ?: "", ProviderType.GOOGLE, profilePicUrl)
                        }else{
                            showAlert("Se ha producido un error.")
                        }
                    }
                }

            }catch (e: ApiException){
                showAlert("Se ha producido un error.")
            }

        }




        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun initData() {
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta)
        btnLogin = findViewById(R.id.btnLogin)
        etCorreo = findViewById(R.id.etCorreo)
        etContraseña = findViewById(R.id.etContraseña)
        btnFacebook = findViewById(R.id.btnFacebook)
        btnGoogle = findViewById(R.id.btnGoogle)
        btnGithub = findViewById(R.id.btnGithub)
    }



    private fun showHome( email: String, name: String, provider: ProviderType, profilePicUrl:String) {
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("name", name)
            putExtra("provider", provider.name)
            putExtra("picture", profilePicUrl)
        }
        startActivity(homeIntent)
    }

    private fun showHomeWithFacebook(profile: Profile ,email: String, provider: ProviderType, name: String) {
        val userId= profile.id
        Log.d("idface","idfacebook $userId")
        val imageUrl= "https://graph.facebook.com/$userId/picture?type=large"

        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("name", name)
            Log.d("email","el email es $email")
            putExtra("provider", provider.name)
            putExtra("picture",imageUrl)
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