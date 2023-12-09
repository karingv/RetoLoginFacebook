package pe.edu.utp.semana08navigationdrawer


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import pe.edu.utp.semana08navigationdrawer.fragments.HomeFragment
import pe.edu.utp.semana08navigationdrawer.fragments.Opc01Fragment
import pe.edu.utp.semana08navigationdrawer.fragments.Opc02Fragment

enum class ProviderType {
    BASIC
}

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerPrincipal: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var nvPrincipal: NavigationView
    private lateinit var analytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
//        val splashScreen = installSplashScreen()


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        splashScreen.setKeepOnScreenCondition{ true }


        this.drawerPrincipal = findViewById(R.id.dlPrincipal)
        this.toolbar = findViewById(R.id.tbPrincipal)
        this.nvPrincipal = findViewById(R.id.nvPrincipal)



        setSupportActionBar(this.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        this.setNavigationDrawer()
        fragmentTransaction(HomeFragment())

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
        when (item.itemId) {
            R.id.nav_home -> fragmentTransaction(HomeFragment())
            R.id.nav_opc01 -> fragmentTransaction(Opc01Fragment())
            R.id.nav_opc02 -> fragmentTransaction(Opc02Fragment())
        }
        item.isChecked = true
        drawerPrincipal.closeDrawer(GravityCompat.START)
        return true

    }


}