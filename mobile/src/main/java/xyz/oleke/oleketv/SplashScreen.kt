/*
*@author: Ogunleke Abiodun
* Splash Screen Transition
 */
package xyz.oleke.oleketv

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException


class SplashScreen : AppCompatActivity(), View.OnClickListener {

    private val TAG = "SignupActivity"

    private lateinit var mGoogleSignInClient:GoogleSignInClient

    private val RC_SIGN_IN:Int = 9999

    var prefs: Prefs? = null

    private val delay: Long = 5000

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.gsign -> googleSignUp()
            R.id.login -> showLogin()
            R.id.signup -> showSignUp()
            else ->{

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        
        prefs = Prefs(this)
        if(prefs!!.userID!==0) {
            returningUser(prefs!!.userID)
        }
        else {
            findViewById<SignInButton>(R.id.gsign).visibility = View.VISIBLE
            findViewById<Button>(R.id.signup).visibility = View.VISIBLE
            findViewById<Button>(R.id.login).visibility = View.VISIBLE

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("756406194557-egfgc6ama761a67n6ujj0u1kpi7nifbp.apps.googleusercontent.com")
                .requestEmail()
                .build()
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            findViewById<SignInButton>(R.id.gsign).setOnClickListener(this)
            findViewById<Button>(R.id.signup).setOnClickListener(this)
            findViewById<Button>(R.id.login).setOnClickListener(this)
        }
        //val gs: GoogleSignInAccount? = checkGoogleSignIn()
        //if(gs!=null)
            //showMain(Account(gs))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode === RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data).apply {
                try {
                    val account: GoogleSignInAccount  = getResult(ApiException::class.java)
                    val api = API()
                    GlobalScope.launch(Dispatchers.Main) {
                        var request = api.authUser(account.email!!,"")
                        var response:Model.User?
                        try{
                            response = request.await()
                            if(response?.name != null){
                                val main = Intent(this@SplashScreen, MainActivity::class.java)
                                main.putExtra("account",Account(response))
                                startActivity(main)
                                finish()
                            }
                            else {
                                Toast.makeText(baseContext, "Unable to login user", Toast.LENGTH_LONG).show()
                                Toast.makeText(baseContext, "Sign in failed!", Toast.LENGTH_LONG).show()
                            }
                        }
                        catch (e: HttpException) {
                            Log.d(TAG,e.code().toString())
                            if(e.code()==404){
                                request = api.newUser(account.displayName!!,account.email!!,"","",contentResolver)
                                response = request.await()
                                if (response?.name != null) {
                                    Log.d(TAG, response.name)
                                    val main = Intent(this@SplashScreen, Subscription::class.java)
                                    main.putExtra("account", Account(response))
                                    startActivity(main)
                                } else {
                                    Toast.makeText(baseContext, "Unable to register new user", Toast.LENGTH_LONG).show()
                                    Toast.makeText(baseContext, "Signup failed!", Toast.LENGTH_LONG).show()
                                }
                            }
                            else{
                                Toast.makeText(baseContext, "Signup failed!", Toast.LENGTH_LONG).show()
                            }

                        } catch (e: Throwable) {
                            Log.d(TAG,"Ooops: Something else went wrong: "+e.message!!)
                            Toast.makeText(baseContext, "Signup failed!", Toast.LENGTH_LONG).show()
                        }
                    }

                }

                catch (e:ApiException){
                    Log.w("SplashScreen", "signInResult:failed code=" + e.statusCode);
                }

            }
        }
    }


    private fun returningUser(id:Int){
        val api = API()
        GlobalScope.launch(Dispatchers.Main) {

            val request = api.getUser(id)
            try{
                val response = request.await()
                if(response?.name != null){
                    Log.d(TAG,response.name)
                    val main = Intent(this@SplashScreen, MainActivity::class.java)
                    main.putExtra("account",Account(response))
                    startActivity(main)
                    finish()
                }
                else{
                    Toast.makeText(baseContext,"User could not be returned",Toast.LENGTH_LONG).show()

                }
            }
            catch (e: HttpException) {
                Log.d(TAG,e.code().toString())

            } catch (e: Throwable) {
                Log.d(TAG,"Ooops: Something else went wrong: "+e.message!!)

            }
        }
    }


    private fun checkGoogleSignIn() : GoogleSignInAccount?{
        return GoogleSignIn.getLastSignedInAccount(this)
    }

    private fun googleSignUp(){
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,RC_SIGN_IN)

    }

    private fun showLogin()
    {
        val main = Intent(this, LoginActivity::class.java)
        startActivity(main)
    }

    private fun showSignUp(){
        val main = Intent(this, SignUpActivity::class.java)
        startActivity(main)

    }

    private fun showMain(account: Account){
        val main = Intent(this, MainActivity::class.java)
        main.putExtra("account",account)
        startActivity(main)

    }


}
