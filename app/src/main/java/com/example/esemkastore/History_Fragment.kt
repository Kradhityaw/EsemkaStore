package com.example.esemkastore

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.esemkastore.databinding.FragmentHistoryBinding
import com.example.esemkastore.databinding.HistoryCardBinding
import com.example.esemkastore.databinding.ItemsDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.net.URL
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [History_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class History_Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var inflate = FragmentHistoryBinding.inflate(inflater,container,false)

        GlobalScope.launch(Dispatchers.IO) {
            var conn = URL("http://10.0.2.2:5000/api/History/Transaction/1").openStream().bufferedReader().readText()
            var jsons = JSONArray(conn)

            GlobalScope.launch(Dispatchers.Main) {
                var adapt = object : RecyclerView.Adapter<HistoryVH>() {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryVH {
                        var inflate = HistoryCardBinding.inflate(LayoutInflater.from(parent.context), container, false)
                        return HistoryVH(inflate)
                    }

                    override fun getItemCount(): Int {
                        return jsons.length()
                    }

                    override fun onBindViewHolder(holder: HistoryVH, position: Int) {
                        var cek = jsons.getJSONObject(position)
                        var sample = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                        var outSmple = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
                        var date = sample.parse(cek.getString("orderDate"))
                        holder.binding.historyDate.text = outSmple.format(date)

                        var array = cek.getJSONArray("detail")
                        val adapter = object : RecyclerView.Adapter<CardHis>() {
                            override fun onCreateViewHolder(
                                parent: ViewGroup,
                                viewType: Int
                            ): CardHis {
                                var inflate = ItemsDetailBinding.inflate(inflater, parent,false)
                                return CardHis(inflate)
                            }

                            override fun getItemCount(): Int {
                                return array.length()
                            }

                            override fun onBindViewHolder(holder: CardHis, position: Int) {
                                var cek2 = array.getJSONObject(position).getJSONObject("item")
                                holder.bindd.cartName.text = cek2.getString("name")
                                var format = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                                var sum = cek2.getInt("price") * array.getJSONObject(position).getInt("count")
                                holder.bindd.cartPrice.text = "Price: ${format.format(sum)}"
                                holder.bindd.cartQty.text = "Count : ${array.getJSONObject(position).getInt("count")}"

                                GlobalScope.launch(Dispatchers.IO) {
                                    try {
                                        var img = BitmapFactory.decodeStream(URL("http://10.0.2.2:5000/api/Home/Item/Photo/${cek2.getInt("id")}").openStream())
                                        GlobalScope.launch(Dispatchers.Main) {
                                            holder.bindd.cartImage.setImageBitmap(img)
                                        }
                                    }
                                    catch (e : Exception) {
                                        GlobalScope.launch(Dispatchers.Main) {
                                            holder.bindd.cartImage.setImageResource(R.drawable.defaultttttt)
                                        }
                                    }
                                }
                            }
                        }

                        holder.binding.itemsRv.adapter = adapter
                        holder.binding.itemsRv.layoutManager = LinearLayoutManager(context)

                    }
                }
                inflate.historyRv.adapter = adapt
                inflate.historyRv.layoutManager = LinearLayoutManager(context)
            }
        }

        return inflate.root
    }

    class HistoryVH(var binding: HistoryCardBinding) : RecyclerView.ViewHolder(binding.root)
    class CardHis(var bindd: ItemsDetailBinding) : RecyclerView.ViewHolder(bindd.root)

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment History_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            History_Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}