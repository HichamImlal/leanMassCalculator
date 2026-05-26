package com.app.leanmass.history

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.leanmass.db.DatabaseHelper
import com.app.leanmass.databinding.ActivityHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbarHistory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbarHistory.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = HistoryAdapter(emptyList())
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter

        // MASVS-AUTH-1: Protect History screen with Biometric Authentication
        checkBiometrics()

        binding.btnViderHistorique.setOnClickListener {
            viderHistorique()
        }
    }

    private fun checkBiometrics() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                chargerHistorique()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                Toast.makeText(this@HistoryActivity, "Authentification requise : $errString", Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // Just a hint, don't finish yet
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Vérification d'identité")
            .setSubtitle("Confirmez votre identité pour accéder à l'historique")
            .setNegativeButtonText("Annuler")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun chargerHistorique() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = DatabaseHelper.getDatabase(this@HistoryActivity)
            val listeRecords = db.leanMassDao().getAllRecords()

            withContext(Dispatchers.Main) {
                adapter.updateData(listeRecords)
            }
        }
    }

    private fun viderHistorique() {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = DatabaseHelper.getDatabase(this@HistoryActivity)
            db.leanMassDao().deleteAllRecords()

            withContext(Dispatchers.Main) {
                adapter.updateData(emptyList())
            }
        }
    }
}
