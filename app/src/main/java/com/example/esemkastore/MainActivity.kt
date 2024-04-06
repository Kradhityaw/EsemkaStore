package com.example.esemkastore

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.esemkastore.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.json.JSONArray

class MainActivity : AppCompatActivity() {
    lateinit var bind : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        val items = getSharedPreferences("LOCAL", Context.MODE_PRIVATE).getString("cart", "[]")
        val array = JSONArray(items)

        var tl = bind.mainTl
        tl.addTab(tl.newTab().setText("Home"))
        tl.addTab(tl.newTab().setText("Cart (${array.length()})"))
        tl.addTab(tl.newTab().setText("History"))

        Log.d("data", Runtime.jumlah.toString())
        setSupportActionBar(bind.mainToolbar)
        supportActionBar?.title = "Home"

        var data = supportFragmentManager.beginTransaction()
        data.replace(R.id.main_container, Home_Fragment())
        data.commit()

        bind.mainTl.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var pos = tab?.position
                when (pos) {
                    0 -> {
                        supportFragmentManager.beginTransaction().replace(R.id.main_container, Home_Fragment()).commit()
                        setSupportActionBar(bind.mainToolbar)
                        supportActionBar?.title = "Home"
                    }
                    1 -> {
                        supportFragmentManager.beginTransaction().replace(R.id.main_container, Empty_Fragment()).commit()
                        setSupportActionBar(bind.mainToolbar)
                        supportActionBar?.title = "Cart"
                    }
                    2 -> {
                        supportFragmentManager.beginTransaction().replace(R.id.main_container, History_Fragment()).commit()
                        setSupportActionBar(bind.mainToolbar)
                        supportActionBar?.title = "History"
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }


//    override fun data(id: String) {
//        val bundle = Bundle()
//        bundle.putString("id", id)
//
//        val transaction = supportFragmentManager.beginTransaction()
//        val detail = Detail_Fragment()
//        detail.arguments = bundle
//
//        transaction.replace(R.id.home_container, detail)
//        transaction.commit()
//    }
}