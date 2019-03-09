package xyz.oleke.oleketv

import android.content.Context
import android.content.SharedPreferences

class Prefs (context: Context) {
    val PREFS_FILENAME = "xyz.oleke.oleketv.prefs"
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0)
    val USER_ID = "user_id"

    var userID: Int
        get() = prefs.getInt(USER_ID,0)
        set(value) = prefs.edit().putInt(USER_ID,value).apply()
}