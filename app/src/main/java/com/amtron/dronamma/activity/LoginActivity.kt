package com.amtron.dronamma.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amtron.dronamma.databinding.ActivityLogin2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogin2Binding

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)




        supportActionBar?.title = "Login"

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()


        sharedPreferences = this.getSharedPreferences("Drona", MODE_PRIVATE)
        editor = sharedPreferences.edit()


        binding.login.setOnClickListener {

            val email = binding.email.text.toString().trim()
            val password = binding.enterPassword.text.toString().trim()

            if (email.isEmpty()) {
                binding.email.error = "Email is required"
                binding.email.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.enterPassword.error = "Password is required"
                binding.enterPassword.requestFocus()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser

                    val uid = user?.uid

                    saveDataInSharedPref(uid.toString())


                    Toast.makeText(this, "Authentication successful.", Toast.LENGTH_SHORT)
                        .show()
                    // Navigate to the main activity

                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }


        }


    }

    private fun saveDataInSharedPref(uid: String) {


        val dRef = fireStore.collection("Users").document(uid)

        dRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val data = documentSnapshot.data

                    editor.putString("user", Gson().toJson(data))
                    editor.apply()


                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                    Log.d(TAG, Gson().toJson(data))
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting document: ", exception)
            }
    }
}