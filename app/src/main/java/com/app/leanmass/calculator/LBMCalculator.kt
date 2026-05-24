package com.app.leanmass.calculator

object LBMCalculator {
    fun calculateLBM(poids: Double, taille: Double, isHomme: Boolean): Double {
        return if (isHomme) {
            (0.407 * poids) + (0.267 * taille) - 19.2
        } else {
            (0.252 * poids) + (0.473 * taille) - 48.3
        }
    }
}
