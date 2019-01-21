/*
*@author: Ogunleke Abiodun
* Splash Screen Transition
 */
package xyz.oleke.oleketv

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity

class SplashScreen : AppCompatActivity() {
    private val delay: Long = 2000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            val main = Intent(this, MainActivity::class.java)
            startActivity(main)
            this.finish()
        }, delay)
    }

}
