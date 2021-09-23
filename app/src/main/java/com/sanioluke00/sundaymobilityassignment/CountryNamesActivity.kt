package com.sanioluke00.sundaymobilityassignment

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class CountryNamesActivity : AppCompatActivity() {

    lateinit var allCountryDataAdapter: AllCountryDataAdapter
    lateinit var countrynames_nodata_lay: View
    lateinit var countrynames_retrybtn: Button
    lateinit var countrynames_countryrec: RecyclerView
    lateinit var countrynames_progbar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country_names)

        countrynames_countryrec = findViewById(R.id.countrynames_countryrec)
        countrynames_nodata_lay = findViewById(R.id.countrynames_nodata_lay)
        countrynames_retrybtn = findViewById(R.id.countrynames_retrybtn)
        countrynames_progbar = findViewById(R.id.countrynames_progbar)

        setCountryNamesData()
        countrynames_retrybtn.setOnClickListener { setCountryNamesData() }
    }

    private fun setCountryNamesData() {
        countrynames_countryrec.isVisible = false
        countrynames_nodata_lay.isVisible = false
        countrynames_progbar.isVisible = true
        Handler().postDelayed({
            if (check_net_connection(this)) {
                countrynames_countryrec.isVisible = true
                countrynames_nodata_lay.isVisible = false
                countrynames_progbar.isVisible = false
                getDatafromURL()
            } else {
                countrynames_countryrec.isVisible = false
                countrynames_nodata_lay.isVisible = true
                countrynames_progbar.isVisible = false

                val dialog = Dialog(this)
                dialog.setContentView(R.layout.no_net_dialog)
                dialog.setCanceledOnTouchOutside(false)
                dialog.setCancelable(true)
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.window!!.attributes.windowAnimations = android.R.style.Animation_Dialog
                val msg_okbtn = dialog.findViewById<TextView>(R.id.msg_okbtn)
                msg_okbtn.setOnClickListener { dialog.dismiss() }
                dialog.show()
            }
        }, 2000)
    }

    inner class AllCountryDataAdapter() :
        RecyclerView.Adapter<AllCountryDataAdapter.ListViewHolder>() {

        lateinit var context: Context
        lateinit var allcountryDataArray: ArrayList<AllCountryDataModel>

        constructor(
            context: Context,
            allcountryDataArray: ArrayList<AllCountryDataModel>
        ) : this() {
            this.context = context
            this.allcountryDataArray = allcountryDataArray
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): AllCountryDataAdapter.ListViewHolder {
            val mview: View = LayoutInflater.from(parent.context).inflate(
                R.layout.country_names_items,
                parent,
                false
            )
            return ListViewHolder(mview)
        }

        override fun onBindViewHolder(holder: AllCountryDataAdapter.ListViewHolder, position: Int) {

            holder.countryName.text = allcountryDataArray[position].countryname
            holder.countrynames_items_mainlay.setOnClickListener { v ->
                val intent = Intent(context, PlayersNamesActivity::class.java)
                intent.putExtra(
                    "this_country_players_data",
                    allcountryDataArray[position].countryDataArray
                )
                intent.putExtra("this_country_name", allcountryDataArray[position].countryname)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return allcountryDataArray.size
        }

        inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var countryName: TextView
            var countrynames_items_mainlay: View

            init {
                countryName = itemView.findViewById(R.id.data_items_itemname)
                countrynames_items_mainlay = itemView.findViewById(R.id.countrynames_items_mainlay)
            }

        }
    }

    private fun getDatafromURL() {

        val mainURL = "https://test.oye.direct/players.json"
        val arrayList: ArrayList<AllCountryDataModel> = ArrayList()
        var count = 0

        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, mainURL, null,
            { response ->

                for (each in 0 until response.length()) {

                    var countryName: String
                    val playersData: ArrayList<AllPlayersDataModel> = ArrayList()

                    countryName = response.names()?.get(count) as String
                    Log.e("each_country_name", "The country name is $countryName")

                    val jsonArray = response.getJSONArray(response.names()?.get(count) as String)
                    for (m in 0 until jsonArray.length()) {

                        var firstname: String? = ""
                        var lastname: String? = ""
                        var fullname: String? = ""
                        var isCaptain: Boolean

                        val playerDataObj: JSONObject = jsonArray.getJSONObject(m)
                        fullname = playerDataObj.getString("name")
                        if (playerDataObj.optBoolean("captain")) {
                            isCaptain = playerDataObj.getBoolean("captain")
                        } else {
                            isCaptain = false
                        }

                        val fullname_list = fullname.split(" ", limit = 2)

                        if (fullname_list.size > 1) {
                            firstname = fullname_list[0]
                            lastname = fullname_list[1]
                        } else {
                            firstname = fullname
                            lastname = " "
                        }
                        Log.e(
                            "each_player",
                            "The first name of the player is $firstname and the last name is $lastname"
                        )

                        playersData.add(
                            AllPlayersDataModel(
                                firstname.toString(),
                                lastname,
                                isCaptain
                            )
                        )
                    }

                    arrayList.add(AllCountryDataModel(countryName, playersData))
                    count += 1
                }

                countrynames_countryrec.layoutManager = GridLayoutManager(this, 1)
                allCountryDataAdapter = AllCountryDataAdapter(this, arrayList)
                countrynames_countryrec.adapter = allCountryDataAdapter

                Log.e("dataobj_size", "The Size is : ${response.names()?.length()}")
            },
            { volleyerror ->
                Log.e("Volley_main_data", "Error on : " + volleyerror.message)
            })

        queue.add(jsonObjectRequest)
    }

    private fun check_net_connection(context: Context): Boolean {
        val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isAvailable && activeNetwork.isConnected
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}