package com.example.esemkastore

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.esemkastore.databinding.CardLayoutBinding
import com.example.esemkastore.databinding.FragmentDetailBinding
import com.example.esemkastore.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

class Home_Fragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bind = FragmentHomeBinding.inflate(layoutInflater, container, false)

        GlobalScope.launch(Dispatchers.IO) {
            val conn = URL("http://10.0.2.2:5000/api/Home/Item").openStream().bufferedReader().readText()
            val jsons = JSONArray(conn)

            GlobalScope.launch(Dispatchers.Main) {
                val adapter = object : RecyclerView.Adapter<CardHolder>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
                        val inflate = CardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                        return CardHolder(inflate)
                    }

                    override fun getItemCount(): Int {
                        return jsons.length()
                    }

                    override fun onBindViewHolder(holder: CardHolder, position: Int) {
                        val data = jsons.getJSONObject(position)
                        holder.binding.productName.text = data.getString("name")
                        holder.binding.productDesc.text = data.getString("description")

                        val format =NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                        holder.binding.productPrice.text = format.format(data.getInt("price"))

                        holder.itemView.setOnClickListener {
                            Runtime.id = position
                            val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
                            transaction.replace(R.id.main_container, Detail_Fragment())
                            transaction.addToBackStack(null)
                            transaction.commit()
                        }

                        GlobalScope.launch(Dispatchers.IO) {
                            val conn = URL("http://10.0.2.2:5000/api/Home/Item/Photo/${data.getString("id")}").openConnection() as HttpURLConnection
                            conn.requestMethod = "GET"
                            conn.setRequestProperty("Content-Type", "image/png")

                            try {
                                val image = BitmapFactory.decodeStream(conn.inputStream)

                                GlobalScope.launch(Dispatchers.Main) {
                                    holder.binding.productImage.setImageBitmap(image)
                                }
                            }catch (e : Exception) {
                                GlobalScope.launch(Dispatchers.Main) {
                                    holder.binding.productImage.setImageResource(R.drawable.defaultttttt)
                                }
                            }
                        }
                    }
                }
                bind.homeRv.adapter = adapter
                bind.homeRv.layoutManager = GridLayoutManager(context, 2)
            }
        }
        return bind.root
    }

    class CardHolder(val binding : CardLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}