package com.nhom.travelapp.core.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseProvider {

    val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    val database: FirebaseDatabase by lazy {
        Firebase.database
    }

    val usersRef: DatabaseReference by lazy {
        database.getReference("users")
    }
}