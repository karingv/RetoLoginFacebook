package pe.edu.utp.semana08navigationdrawer


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.isGone
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.facebook.login.LoginManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import pe.edu.utp.semana08navigationdrawer.fragments.HomeFragment
import pe.edu.utp.semana08navigationdrawer.fragments.Opc01Fragment
import pe.edu.utp.semana08navigationdrawer.fragments.Opc02Fragment

enum class ProviderType {
    BASIC,
    GOOGLE,
    FACEBOOK,
    GITHUB
}

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerPrincipal: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var nvPrincipal: NavigationView

    private lateinit var provider: String

    private lateinit var etCorreo: EditText
    private lateinit var etName: EditText
    private lateinit var etProvider: EditText
    private lateinit var tvTitle: TextView

    private lateinit var btnCerrar: Button
    override fun onCreate(savedInstanceState: Bundle?) {
//        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initData()
//        splashScreen.setKeepOnScreenCondition{ true }

        this.drawerPrincipal = findViewById(R.id.dlPrincipal)
        this.toolbar = findViewById(R.id.tbPrincipal)
        this.nvPrincipal = findViewById(R.id.nvPrincipal)

        FirebaseApp.initializeApp(this)

        setSupportActionBar(this.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.setNavigationDrawer()
        fragmentTransaction(Opc01Fragment())

        val bundle = intent.extras
        val email = bundle?.getString("email")
        provider = bundle?.getString("provider").toString()
        val name = bundle?.getString("name")

        val imagenPerfil = bundle?.getString("picture")
        setup(email ?: "", provider ?: "", imagenPerfil ?: "", name ?: "")


        //Guardado de datos
        val prefs =getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email",email)
        prefs.putString("provider", provider)
        prefs.apply()
    }

    private fun initData() {
        etCorreo = findViewById(R.id.etCorreo)
        etName = findViewById(R.id.etName)
        etProvider = findViewById(R.id.etProvider)

        tvTitle = findViewById(R.id.tvTitle)

        btnCerrar = findViewById(R.id.btnCerrar)
        Log.d("TAG+++", "Valor de X: ")

    }


    private fun setup(email: String, provider: String, imagenPerfil: String, name: String){

            title = "API Clima - Login [Simple-Facebook]"
            tvTitle.text = "Bienvenido: ${name}"
            etCorreo.setText(email)
            etName.setText(name)
            etProvider.setText(provider)

            val mensajeCompleto = "~En mantenimiento: ${name.split(" ").firstOrNull()}~"
            Opc01Fragment.textBienvenido  = if (!mensajeCompleto.isNullOrEmpty()) {
                mensajeCompleto
            } else {
                "~En mantenimiento~"
            }

            val navHeader = nvPrincipal.getHeaderView(0)
            val perfil = navHeader.findViewById<ImageView>(R.id.ivPerfil)

            val correo = navHeader.findViewById<TextView>(R.id.tvCorreo)
            correo.text = email

            Log.d("imagen", "URL: $imagenPerfil")
            if (imagenPerfil.isNotEmpty()) {
                Picasso.get().load(imagenPerfil).into(perfil)
            }

            btnCerrar.setOnClickListener {
                cerrarSession(provider)
            }

        }

    private fun cerrarSession(provider: Any) {
//                val pref = getSharedPreferences("pe.edu.utp.semana08navigationdrawer")
        val prefs =getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()

        if (provider == ProviderType.FACEBOOK.name){
            LoginManager.getInstance().logOut()
        }

        FirebaseAuth.getInstance().signOut()
        onBackPressed()
    }


    private fun setNavigationDrawer() {
        val toogle = ActionBarDrawerToggle(
            this,
            this.drawerPrincipal,
            this.toolbar, R.string.openDrawer, R.string.closeDrawer
        )
        toogle.isDrawerIndicatorEnabled = true
        this.drawerPrincipal.addDrawerListener(toogle)
        toogle.syncState()
        this.nvPrincipal.setNavigationItemSelectedListener(this)

    }

    private fun fragmentTransaction(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.flContenido, fragment).commit()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        etCorreo.isGone = true
        etName.isGone = true
        etProvider.isGone = true
        when (item.itemId) {
            R.id.nav_home -> {
                fragmentTransaction(HomeFragment())
            }
            R.id.nav_opc01 -> {
                fragmentTransaction(Opc01Fragment())
                etCorreo.isGone = false
                etName.isGone = false
                etProvider.isGone = false
            }
            R.id.nav_opc02 -> {
                fragmentTransaction(Opc02Fragment())
        }
        }
        item.isChecked = true
        drawerPrincipal.closeDrawer(GravityCompat.START)
        return true

    }


}