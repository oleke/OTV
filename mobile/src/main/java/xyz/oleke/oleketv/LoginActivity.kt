package xyz.oleke.oleketv

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

import kotlinx.android.synthetic.main.activity_login.*
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"

    var _emailText: EditText? = null
    var _passwordText: EditText? = null
    var _loginButton: Button? = null
    var _forgotLink: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        _emailText = input_email
        _passwordText = input_password
        _loginButton = btn_login
        _forgotLink = link_forgot

        _loginButton!!.setOnClickListener{
            login()
        }

        _forgotLink!!.setOnClickListener {

        }

    }

    fun login() {
        Log.d(TAG, "Login")

        if (!validate()) {
            onLoginFailed()
            return
        }

        _loginButton!!.isEnabled = false

        val email = _emailText!!.text.toString()
        val password = _passwordText!!.text.toString()

        // TODO: Implement your own authentication logic here.

        val api = API()
        GlobalScope.launch(Dispatchers.Main) {

            val request = api.authUser(email,password)
            try{
                val response = request.await()
                if(response?.name != null){
                    Log.d(TAG,response.name)
                    onLoginSuccess(Account(response))
                }
                else{
                    Toast.makeText(baseContext,"User not found",Toast.LENGTH_LONG).show()
                    onLoginFailed()
                }
            }
            catch (e: HttpException) {
                Log.d(TAG,e.code().toString())
                onLoginFailed()
            } catch (e: Throwable) {
                Log.d(TAG,"Ooops: Something else went wrong: "+e.message!!)
                onLoginFailed()
            }
        }

    }


    fun onLoginSuccess(account:Account) {
        _loginButton!!.isEnabled = true
        setResult(Activity.RESULT_OK, null)
        showMain(account)
        finish()
    }

    fun onLoginFailed() {
        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()

        _loginButton!!.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true

        val email = _emailText!!.text.toString()
        val password = _passwordText!!.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText!!.error = "enter a valid email address"
            valid = false
        } else {
            _emailText!!.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            _passwordText!!.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            _passwordText!!.error = null
        }

        return valid
    }

    private fun showMain(account: Account){
        val main = Intent(this, MainActivity::class.java)
        main.putExtra("account",account)
        startActivity(main)
        finish()
    }

}
