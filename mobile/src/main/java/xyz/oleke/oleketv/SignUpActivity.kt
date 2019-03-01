package xyz.oleke.oleketv

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_sign_up.*
import android.app.Activity
import kotlinx.android.synthetic.main.activity_splash_screen.*
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.EditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException


class SignUpActivity : AppCompatActivity() {

    private val TAG = "SignupActivity"

    var nameText: EditText? = null
    var emailText: EditText? = null
    var mobileText: EditText? = null
    var passwordText: EditText? = null
    var reEnterPasswordText: EditText? = null
    var signupButton: Button? = null
    var loginLink: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        actionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        nameText = input_uname
        emailText = input_email
        mobileText = input_mobile
        passwordText= input_password
        reEnterPasswordText = input_confirm_password
        signupButton = btn_register
        loginLink = link_signin


        signupButton!!.setOnClickListener {
            signup()
        }

        loginLink!!.setOnClickListener{
            val main = Intent(this, LoginActivity::class.java)
            startActivity(main)
            this.finish()
        }
    }


    fun signup() {
        Log.d(TAG, "Signup")

        if (!validate()) {
            onSignupFailed()
            return
        }

        signupButton!!.isEnabled = false

        //Todo: Progress Spineer

        val name = nameText!!.text.toString()
        val email = emailText!!.text.toString()
        val mobile = mobileText!!.text.toString()
        val password = passwordText!!.text.toString()

        // TODO: Implement your Signup logic here.

        val api = API()
        GlobalScope.launch(Dispatchers.Main) {

            val request = api.newUser(name,email,mobile,password,contentResolver)
            try{
                val response = request.await()
                if(response!=null && response.name!=null){
                    Log.d(TAG,response.name)
                    onSignupSuccess(Account(response))
                }
                else{
                    Toast.makeText(baseContext,"Unable to register new user",Toast.LENGTH_LONG).show()
                    onSignupFailed()
                }
            }
            catch (e: HttpException) {
                Log.d(TAG,e.code().toString())
                onSignupFailed()
            } catch (e: Throwable) {
                Log.d(TAG,"Ooops: Something else went wrong: "+e.message!!)
                onSignupFailed()
            }
        }
    }


    fun onSignupSuccess(account:Account) {
        signupButton!!.isEnabled = true
        setResult(Activity.RESULT_OK, null)
        showSubscription(account)
        //finish()
    }

    fun onSignupFailed() {
        Toast.makeText(baseContext, "Signup failed!", Toast.LENGTH_LONG).show()

        signupButton!!.isEnabled = true
    }

    fun validate(): Boolean {
        var valid = true

        val name = nameText!!.text.toString()
        val email = emailText!!.text.toString()
        val mobile = mobileText!!.text.toString()
        val password = passwordText!!.text.toString()
        val reEnterPassword = reEnterPasswordText!!.text.toString()

        if (name.isEmpty() || name.length < 3) {
            nameText!!.setError("at least 3 characters",ContextCompat.getDrawable(this,R.drawable.ic_validation_error))
            valid = false

        } else {
            nameText!!.setError(null)
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText!!.setError("enter a valid email address",ContextCompat.getDrawable(this,R.drawable.ic_validation_error))
            valid = false
        } else {
            emailText!!.setError(null)
        }

        if (mobile.isEmpty() || mobile.length != 10) {
            mobileText!!.setError("Enter Valid Mobile Number",ContextCompat.getDrawable(this,R.drawable.ic_validation_error))
            valid = false
        } else {
            mobileText!!.setError(null)
        }

        if (password.isEmpty() || password.length < 4) {
            passwordText!!.setError("more than 4 alphanumeric characters",ContextCompat.getDrawable(this,R.drawable.ic_validation_error))
            valid = false
        } else {
            passwordText!!.setError(null)
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length < 4 || reEnterPassword.length > 10 || reEnterPassword != password) {
            reEnterPasswordText!!.setError("Password Do not match",ContextCompat.getDrawable(this,R.drawable.ic_validation_error))
            valid = false
        } else {
            reEnterPasswordText!!.setError(null)
        }

        return valid
    }

    private fun showSubscription(account: Account){
        val main = Intent(this, Subscription::class.java)
        main.putExtra("account",account)
        startActivity(main)
    }


}
