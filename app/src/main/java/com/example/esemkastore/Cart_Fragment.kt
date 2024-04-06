package com.example.esemkastore

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.provider.Settings.Global
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkastore.databinding.CartCardBinding
import com.example.esemkastore.databinding.FragmentCartBinding
import com.example.esemkastore.databinding.SpinnerLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

class Cart_Fragment : Fragment() {
    var totalcount = 0
    var serviceId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var bind = FragmentCartBinding.inflate(inflater, container, false)
        var cart = requireActivity().getSharedPreferences("LOCAL", Context.MODE_PRIVATE).getString("cart", "[]")
        var jonson = JSONArray(cart)
        var shred = requireActivity().getSharedPreferences("LOCAL", Context.MODE_PRIVATE)
        var editor = shred.edit()
//        var konteks: Context = requireContext()
        Log.d("oke",jonson.toString())

        if (jonson.length() == 0) {
            var transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.main_container, Empty_Fragment())
            transaction?.commit()
        }
        else {
            var adapter = object : RecyclerView.Adapter<ItemVH>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH {
                    var inflate = CartCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    return ItemVH(inflate)
                }

                override fun getItemCount(): Int {
                    return jonson.length()
                }

                override fun onBindViewHolder(holder: ItemVH, position: Int) {
                    var cek = jonson.getJSONObject(position)
                    holder.binding.cartName.text = cek.getString("name")
                    holder.binding.cartQty.text = "Count : ${cek.getInt("count")}"
                    holder.binding.cartPrice.text = "Price : ${cek.getInt("price")}"


                    holder.binding.delBtn.setOnClickListener {
                        var jsonRemove = cek.getString("name")

//                        var count = 0;
                        for (i in 0 until jonson.length()) {
                            var jsobject = jonson.getJSONObject(i)
                            totalcount -= jsobject.getInt("price")
                            if (jsobject.getString("name") == jsonRemove) {
                                jonson.remove(i)
                                editor.putString("cart", jonson.toString())
                                editor.apply()
                                if (jonson.length() == 0) {
                                    var transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
                                    transaction?.replace(R.id.main_container, Empty_Fragment())
                                    transaction?.commit()
                                    notifyDataSetChanged()
                                }
                                notifyDataSetChanged()
                                bind.totalPrice.text = totalcount.toString()
                                break
                            }
                        }
                    }

                    GlobalScope.launch(Dispatchers.IO) {
                        try {
                            var img = BitmapFactory.decodeStream(URL("http://10.0.2.2:5000/api/Home/Item/Photo/${cek.getInt("id")}").openStream())
                            GlobalScope.launch(Dispatchers.Main) {
                                holder.binding.cartImage.setImageBitmap(img)
                            }
                        }
                        catch (e: Exception) {
                            GlobalScope.launch(Dispatchers.Main) {
                                holder.binding.cartImage.setImageResource(R.drawable.defaultttttt)
                            }
                        }
                    }
                }
            }
            bind.cartRv.adapter = adapter
            bind.cartRv.layoutManager = LinearLayoutManager(context)
        }

        GlobalScope.launch(Dispatchers.IO) {
            var conn = URL("http://10.0.2.2:5000/api/Checkout/Service").openStream().bufferedReader().readText()
            var service = JSONArray(conn)

            GlobalScope.launch(Dispatchers.Main) {
                try {
                    var adapter = object : ArrayAdapter<JSONObject>(requireContext(), android.R.layout.simple_spinner_item) {
                        override fun getCount(): Int = service.length()

                        override fun getItem(position: Int): JSONObject? = service.getJSONObject(position)

                        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                            var binding: SpinnerLayoutBinding = if (convertView != null) SpinnerLayoutBinding.bind(convertView)
                            else SpinnerLayoutBinding.inflate(layoutInflater,parent,false)

                            var it = getItem(position)
                            binding.spinnerName.text = it?.getString("name")

                            return binding.root
                        }

                        override fun getDropDownView(
                            position: Int,
                            convertView: View?,
                            parent: ViewGroup
                        ): View {
                            var binding: SpinnerLayoutBinding = if (convertView != null) SpinnerLayoutBinding.bind(convertView)
                            else SpinnerLayoutBinding.inflate(layoutInflater,parent,false)

                            var it = getItem(position)
                            binding.spinnerName.text = it?.getString("name")
                            binding.spinnerDays.text = "( ${it?.getString("duration")}day(s) )"
                            binding.spinnerPrice.text = "Rp${it?.getString("price")} "

                            return binding.root
                        }
                    }
                    bind.itemSpinner.adapter = adapter
                    bind.itemSpinner.onItemSelectedListener = object : OnItemSelectedListener {
                        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                            var item: JSONObject = adapter.getItem(p2) as JSONObject
                            serviceId = item.getInt("id")
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {}
                    }
                }
                catch (e: Exception) {
                    Toast.makeText(context, "Your Cart Is Empty", Toast.LENGTH_SHORT).show()
                }
            }
        }

        var count = 0;
        for (i in 0 until jonson.length()) {
            var arr = jonson.getJSONObject(i)
//            Log.d("arr", arr.getString("price"))
            count += arr.getInt("price")
        }

        totalcount = count

        bind.totalPrice.text = count.toString()

        var calender = Calendar.getInstance()
        var year = calender.get(Calendar.YEAR)
        var month = calender.get(Calendar.MONTH)
        var day = calender.get(Calendar.DAY_OF_MONTH)

        Log.d("tgl", "${year}-${month}-${day}")

        bind.button.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                var apiConn = URL("http://10.0.2.2:5000/api/Checkout/Transaction").openConnection() as HttpURLConnection
                apiConn.requestMethod = "POST"
                apiConn.setRequestProperty("Content-Type", "application/json")

                var jsons = JSONObject().apply {
                    put("userId", Runtime.userID)
                    put("serviceId", serviceId)
                    put("totalPrice", totalcount)
                    put("orderDate", "${year}-${month}-${day}")
                    put("acceptanceDate", "${year}-${month}-${day}")
                    put("detail", JSONArray().apply {
                        for (i in 0 until jonson.length()) {
                            var obj = jonson.getJSONObject(i)
                            put(JSONObject().apply {
                                put("ItemId", obj.getString("id"))
                                put("Count", obj.getString("count"))
                            })
                        }
                    })
                }

                apiConn.outputStream.write(jsons.toString().toByteArray())

                if (apiConn.responseCode in 200..299) {
                    var editor = shred.edit()
                    editor.clear().apply()
                    var transaction: FragmentTransaction? = fragmentManager?.beginTransaction()
                    transaction?.replace(R.id.main_container, Empty_Fragment())
                    transaction?.commit()
                }
            }
        }

        return bind.root
    }

    class ItemVH(var binding: CartCardBinding) : RecyclerView.ViewHolder(binding.root)
}