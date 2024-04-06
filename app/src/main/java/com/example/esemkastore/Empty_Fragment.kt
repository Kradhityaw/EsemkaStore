package com.example.esemkastore

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.example.esemkastore.databinding.FragmentEmptyBinding
import org.json.JSONArray

class Empty_Fragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var bind = FragmentEmptyBinding.inflate(layoutInflater, container,false)

        var shared = requireActivity().getSharedPreferences("LOCAL", Activity.MODE_PRIVATE).getString("cart", "[]")
        var jonson = JSONArray(shared)

        if (jonson.length() >= 1) {
            var transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_container, Cart_Fragment())
            transaction?.commit()
        }

        return bind.root
    }
}