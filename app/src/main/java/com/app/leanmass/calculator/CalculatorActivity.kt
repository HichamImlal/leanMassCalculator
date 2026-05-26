package com.app.leanmass.calculator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.app.leanmass.R
import com.app.leanmass.auth.LoginActivity
import com.app.leanmass.config.LBMConfig
import com.app.leanmass.db.DatabaseHelper
import com.app.leanmass.history.HistoryActivity
import com.app.leanmass.model.LBMResult
import com.app.leanmass.databinding.ActivityCalculatorBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.widget.Toast
import java.util.Locale

class CalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalculatorBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null) {
            goToLogin()
            return
        }

        binding.btnCalculer.setOnClickListener {
            effectuerLeCalcul()
        }

        binding.btnHistorique.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            // MASVS-SESSION-1: Clear session timestamp on logout
            getSharedPreferences("session_prefs", MODE_PRIVATE).edit()
                .remove("last_timestamp")
                .apply()

            auth.signOut()
            goToLogin()
        }
    }

    /**
     * MASVS-SESSION-1: Implement session timeout
     * Checks if the session has expired after 5 minutes of inactivity.
     */
    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("session_prefs", Context.MODE_PRIVATE)
        val lastAction = prefs.getLong("last_timestamp", 0L)
        val now = System.currentTimeMillis()

        // 5 minutes timeout = 300,000 ms
        if (lastAction != 0L && (now - lastAction) > 300000) {
            auth.signOut()
            Toast.makeText(this, "Session expirée", Toast.LENGTH_SHORT).show()
            goToLogin()
        } else {
            // Update last action timestamp
            prefs.edit().putLong("last_timestamp", now).apply()
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun effectuerLeCalcul() {
        val poidsStr = binding.etPoids.text.toString().trim()
        val tailleStr = binding.etTaille.text.toString().trim()

        if (poidsStr.isEmpty() || tailleStr.isEmpty()) {
            binding.tvResultat.text = "---"
            binding.tvStatusMessage.text = "Saisir poids/taille"
            binding.imgStatus.visibility = View.GONE
            return
        }

        val poids = poidsStr.toDoubleOrNull() ?: 0.0
        val taille = tailleStr.toDoubleOrNull() ?: 0.0
        val isHomme = binding.rbHomme.isChecked
        val sexeText = if (isHomme) "Homme" else "Femme"

        val lbm = LBMCalculator.calculateLBM(poids, taille, isHomme)

        binding.tvResultat.text = String.format(Locale.getDefault(), "%.2f kg", lbm)
        binding.cardResultat.visibility = View.VISIBLE
        binding.imgStatus.visibility = View.VISIBLE

        val estSatisfaisant = if (isHomme) lbm >= LBMConfig.NORME_HOMME else lbm >= LBMConfig.NORME_FEMME

        if (estSatisfaisant) {
            binding.tvStatusMessage.text = getString(R.string.satisfaisant)
            binding.tvStatusMessage.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.imgStatus.setImageResource(android.R.drawable.ic_dialog_info)
        } else {
            binding.tvStatusMessage.text = getString(R.string.surveiller)
            binding.tvStatusMessage.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.imgStatus.setImageResource(android.R.drawable.stat_sys_warning)
        }

        val nouvelEnregistrement = LBMResult(
            poids = poids,
            taille = taille,
            sexe = sexeText,
            lbmResultat = lbm
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val db = DatabaseHelper.getDatabase(this@CalculatorActivity)
            db.leanMassDao().insertRecord(nouvelEnregistrement)
        }
    }
}
