package com.example.u_key_app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class CategoriasPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProductosFragment.newInstance("Teclados")
            1 -> ProductosFragment.newInstance("Ratones")
            2 -> ProductosFragment.newInstance("Accesorios")
            else -> throw IllegalArgumentException("Position no válida: $position")
        }
    }
}