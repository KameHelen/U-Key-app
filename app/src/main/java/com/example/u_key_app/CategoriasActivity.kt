package com.example.u_key_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2  // ✅ Importar ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CategoriasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)  // ✅ Ahora ViewPager2 está disponible
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)

        val adapter = CategoriasPagerAdapter(this)
        viewPager.adapter = adapter  // ✅ Ahora adapter está disponible

        val titles = listOf("Teclados", "Ratones", "Accesorios")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }
}