package com.nhom.travelapp.data.session

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("travel_app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_SAVED_EMAIL = "saved_email"
    }

    fun saveRememberMe(isRememberMe: Boolean) {
        prefs.edit().putBoolean(KEY_REMEMBER_ME, isRememberMe).apply()
    }

    fun isRememberMeEnabled(): Boolean {
        return prefs.getBoolean(KEY_REMEMBER_ME, false)
    }

    fun saveEmail(email: String) {
        prefs.edit().putString(KEY_SAVED_EMAIL, email).apply()
    }

    fun getSavedEmail(): String {
        return prefs.getString(KEY_SAVED_EMAIL, "") ?: ""
    }

    fun clearRememberedLogin() {
        prefs.edit()
            .remove(KEY_REMEMBER_ME)
            .remove(KEY_SAVED_EMAIL)
            .apply()
    }
}