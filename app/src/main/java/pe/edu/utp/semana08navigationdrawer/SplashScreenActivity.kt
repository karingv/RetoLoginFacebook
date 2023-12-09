package pe.edu.utp.semana08navigationdrawer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val splashScreen = installSplashScreen()

        splashScreen.setKeepOnScreenCondition{ true }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

