package xyz.oleke.oleketv

import android.content.ContentResolver
import android.provider.Settings
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*


class API {
    private val rxAPI: RxAPI

    init{
        val retrofit = Retrofit.Builder()
            .baseUrl("http://otv-env.y32znjkj22.us-east-2.elasticbeanstalk.com")
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
        rxAPI = retrofit.create(RxAPI::class.java)
    }

    fun authUser(email: String, password: String): Deferred<Model.User>{
        return rxAPI.authUser(email,password)
    }

    fun newUser(name:String, email:String, phone:String, password:String,context:ContentResolver): Deferred<Model.User> {
        val id = UUID.randomUUID().hashCode()
        val device = Settings.Secure.getString(context,Settings.Secure.ANDROID_ID)
        return rxAPI.newUser(id,name,email,phone,password,device)
    }

    fun getPlans(): Deferred<List<Model.SubscriptionPlan>>{
        return rxAPI.getPlans()
    }

    fun getPlan(level:Int): Deferred<Model.SubscriptionPlan>{
        return rxAPI.getPlan(level)
    }

    fun newSubscription(user_id:Int, plan:Int,duration:Int): Deferred<Model.User>{
        return rxAPI.newSubscription(user_id,plan,duration)
    }

}