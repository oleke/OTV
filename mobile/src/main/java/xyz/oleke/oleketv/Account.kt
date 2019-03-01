package xyz.oleke.oleketv

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import java.io.Serializable

class Account:Serializable {
    private var user:Model.User? =null

    constructor(account: GoogleSignInAccount?){

    }

    constructor(account: Model.User){
        user = account
    }


    fun getUser():Model.User{
        return this.user!!
    }
}