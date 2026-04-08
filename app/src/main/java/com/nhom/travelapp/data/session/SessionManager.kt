package com.nhom.travelapp.data.session

import android.content.Context
import com.nhom.travelapp.core.utils.Constants

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    fun saveRememberMe(isRememberMe: Boolean) {
        prefs.edit().putBoolean(Constants.KEY_REMEMBER_ME, isRememberMe).apply()
    }

    fun isRememberMeEnabled(): Boolean {
        return prefs.getBoolean(Constants.KEY_REMEMBER_ME, false)
    }

    fun saveEmail(email: String) {
        prefs.edit().putString(Constants.KEY_SAVED_EMAIL, email).apply()
    }

    fun getSavedEmail(): String {
        return prefs.getString(Constants.KEY_SAVED_EMAIL, "") ?: ""
    }

    fun clearRememberedLogin() {
        prefs.edit()
            .remove(Constants.KEY_REMEMBER_ME)
            .remove(Constants.KEY_SAVED_EMAIL)
            .apply()
    }
}