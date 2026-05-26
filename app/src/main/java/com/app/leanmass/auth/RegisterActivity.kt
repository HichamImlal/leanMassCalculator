package com.app.leanmass.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.app.leanmass.R
import com.app.leanmass.calculator.CalculatorActivity
import com.app.leanmass.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegisterAction.setOnClickListener {
            register()
        }

        binding.btnBackToLogin.setOnClickListener {
            finish()
        }
    }

    /**
     * MASVS-AUTH-2: Enforce strong password policy
     * Minimum 8 characters, at least one uppercase, one lowercase and one digit.
     */
    private fun isPasswordStrong(password: String): Boolean {
        val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$".toRegex()
        return passwordRegex.matches(password)
    }

    private fun register() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            return
        }

        // MASVS-AUTH-2 Validation
        if (!isPasswordStrong(password)) {
            Toast.makeText(this, "Le mot de passe doit faire au moins 8 caractères, contenir une majuscule et un chiffre.", Toast.LENGTH_LONG).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    // MASVS-SESSION-1: Reset session timestamp on successful registration
                    getSharedPreferences("session_prefs", MODE_PRIVATE).edit()
                        .putLong("last_timestamp", System.currentTimeMillis())
                        .apply()

                    Toast.makeText(this, "Compte créé avec succès", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, CalculatorActivity::class.java))
                    finishAffinity()
                } else {
                    Toast.makeText(this, "Erreur d'inscription : ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }
}
