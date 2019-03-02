/*
*@author: Ogunleke Abiodun
* API Codes
 */
package xyz.oleke.oleketv

import kotlinx.coroutines.Deferred
import  retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.Serializable


object Model{
    data class User(val id: Int, val name: String, val email: String, val phone:String, val device:String, val activeSubscription: ActiveSubscription?):Serializable
    data class ActiveSubscription(val id: Int, val start_date: String, val end_date:String, val service_provider:ServiceProvider?):Serializable
    data class ServiceProvider(val id: Int, val name: String, val subscription_plan:SubscriptionPlan?, val username:String, val password:String, val url:String):Serializable
    data class SubscriptionPlan(val level: Int, val name:String, val price:Float):Serializable{
        override fun toString(): String {
            return name
        }
    }
    data class ChannelGroup(val id: Int, val name:String):Serializable
    data class Channel(val id: Int, val name: String, val logo: String, val subscription_plan:Int, val channel_group:Int,val service_id:Int):Serializable
}


interface  RxAPI{

    /*
    * Login user
     */
    @GET("/api/auth")
    fun authUser(@Query("email") email:String, @Query("password") password:String):Deferred<Model.User>

    /*
    * Add new user
     */
    @POST("/api/users")
    fun newUser(@Query("id") id:Int, @Query("name") name:String, @Query("email") email:String, @Query("phone") phone:String, @Query("password") password:String, @Query("device") device:String):Deferred<Model.User>

    /*
    * Get Subscription Plans
     */
    @GET("/api/plans?all")
    fun getPlans(): Deferred<List<Model.SubscriptionPlan>>


    /*
    * Get Subscription Plan
     */
    @GET("/api/plans")
    fun getPlan(@Query("level") level:Int): Deferred<Model.SubscriptionPlan>

    /*
    * Add Subscription
     */
    @POST("/api/subscribe")
    fun newSubscription(@Query("user_id") user_id:Int, @Query("plan") plan:Int, @Query("duration") duration:Int):Deferred<Model.User>


    /*
   * Get Channels
    */
    @GET("/api/channels?all")
    fun getChannels(): Deferred<List<Model.Channel>>

    /*
  * Get ChannelGroups
   */
    @GET("/api/groups?all")
    fun getChannelGroups(): Deferred<List<Model.ChannelGroup>>

}