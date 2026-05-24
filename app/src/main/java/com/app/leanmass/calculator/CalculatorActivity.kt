package com.app.leanmass.calculator

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
        }

        binding.btnCalculer.setOnClickListener {
            effectuerLeCalcul()
        }

        binding.btnHistorique.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            goToLogin()
        }
    }

    private fun goToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun effectuerLeCalcul() {
        val poidsStr = binding.etPoids.text.toString()
        val tailleStr = binding.etTaille.text.toString()

        if (poidsStr.isEmpty() || tailleStr.isEmpty()) {
            binding.tvResultat.text = "---"
            binding.tvStatusMessage.text = "Saisir poids/taille"
            binding.imgStatus.visibility = View.GONE
            return
        }

        val poids = poidsStr.toDouble()
        val taille = tailleStr.toDouble()
        val isHomme = binding.rbHomme.isChecked
        val sexeText = if (isHomme) "Homme" else "Femme"

        val lbm = LBMCalculator.calculateLBM(poids, taille, isHomme)

        binding.tvResultat.text = String.format(Locale.getDefault(), "%.2f kg", lbm)
        binding.cardResultat.visibility = View.VISIBLE

        val estSatisfaisant = if (isHomme) lbm >= LBMConfig.NORME_HOMME else lbm >= LBMConfig.NORME_FEMME
        binding.imgStatus.visibility = View.VISIBLE

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
