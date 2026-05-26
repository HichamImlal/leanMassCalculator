package com.app.leanmass.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.app.leanmass.calculator.CalculatorActivity
import com.app.leanmass.databinding.ActivityLoginBinding
import com.app.leanmass.R

import com.app.leanmass.util.SecureLog

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            startActivity(Intent(this, CalculatorActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            SecureLog.d("LoginActivity", "Login button clicked")
            login()
        }

        binding.btnRegister.setOnClickListener {
            SecureLog.d("LoginActivity", "Register button clicked")
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    // MASVS-SESSION-1: Reset session timestamp on successful login
                    getSharedPreferences("session_prefs", MODE_PRIVATE).edit()
                        .putLong("last_timestamp", System.currentTimeMillis())
                        .apply()

                    startActivity(Intent(this, CalculatorActivity::class.java))
                    finish()
                } else {
                    val message = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "Aucun compte trouvé avec cet email"
                        is FirebaseAuthInvalidCredentialsException -> "Email ou mot de passe incorrect"
                        else -> "Erreur de connexion : ${task.exception?.message}"
                    }
                    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                }
            }
    }
}
