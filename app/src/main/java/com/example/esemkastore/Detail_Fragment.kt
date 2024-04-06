package com.example.esemkastore

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.esemkastore.databinding.ActivityMainBinding
import com.example.esemkastore.databinding.FragmentDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

private const val ARG_PARAM1 = "param1"

class Detail_Fragment : Fragment() {
    var pesan: Int? = 0
    private var param1: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        pesan = arguments?.getInt("id",0)
        Log.d("frag", Runtime.id.toString())

        GlobalScope.launch(Dispatchers.IO) {
            val conn = URL("http://10.0.2.2:5000/api/Home/Item").openStream().bufferedReader().readText()
            val data = JSONArray(conn).getJSONObject(Runtime.id)

            GlobalScope.launch(Dispatchers.Main) {
                val format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

                binding.incrementBtn.setOnClickListener {
                    var inflate = ActivityMainBinding.inflate(layoutInflater)
                    inflate.mainToolbar.title = "oke"
                    var data2 = binding.qtyEt.text.toString().toInt() + 1
                    binding.qtyEt.setText(data2.toString())
                    binding.totalPrice.text = "Total Price : ${format.format(data.getDouble("price") * binding.qtyEt.text.toString().toDouble())}"
                }

                binding.decrementBtn.setOnClickListener {
                    var data2 = binding.qtyEt.text.toString().toInt()
                    if (data2 > 1) {
                        var sum = data2 - 1
                        binding.qtyEt.setText(sum.toString())
                        binding.totalPrice.text = "Total Price : ${format.format(data.getDouble("price") * binding.qtyEt.text.toString().toDouble())}"
                    }
                    var items = activity?.getSharedPreferences("item", Context.MODE_PRIVATE)?.getString("test", null)
                    var array = JSONObject(items)
                    Log.d("oke", array.toString())
                }

                binding.addtocart.setOnClickListener {
                    var shared = requireActivity().getSharedPreferences("LOCAL", Context.MODE_PRIVATE)
                    var editorBerkelas =shared.edit()
                    var cartArray =JSONArray(shared.getString("cart", "[]"))

                    var jonson = JSONObject().apply {
                        put("id", data.getInt("id"))
                        put("name", data.getString("name"))
                        put("description", data.getString("description"))
                        put("price", data.getInt("price") * binding.qtyEt.text.toString().toInt())
                        put("count", binding.qtyEt.text.toString())
                    }

                    cartArray.put(jonson)
                    Log.d("cart", cartArray.toString())

                    editorBerkelas.putString("cart", cartArray.toString())
                    editorBerkelas.apply()



                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                }



                binding.detailName.text = data.getString("name")
                binding.detailDescription.text = data.getString("description")

                binding.totalPrice.text = "Total Price : ${format.format(data.getDouble("price") * binding.qtyEt.text.toString().toInt())}"
                binding.detailPrice.text = format.format(data.getInt("price"))
                binding.detailStock.text = "Stock : ${data.getString("stock")}"
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val imgConn = BitmapFactory.decodeStream(URL("http://10.0.2.2:5000/api/Home/Item/Photo/${data.getString("id")}").openStream())
                        GlobalScope.launch(Dispatchers.Main) {
                            binding.detailImage.setImageBitmap(imgConn)
                        }
                    }
                    catch (_: Exception) {
                        GlobalScope.launch(Dispatchers.Main) {
                            binding.detailImage.setImageResource(R.drawable.defaultttttt)
                        }
                    }
                }
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Cart_Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

}