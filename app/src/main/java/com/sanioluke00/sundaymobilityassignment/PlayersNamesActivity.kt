package com.sanioluke00.sundaymobilityassignment

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PlayersNamesActivity : AppCompatActivity() {

    lateinit var playersnames_mainlay: View
    lateinit var playersnames_countryname: TextView
    lateinit var playersnames_captainname: TextView
    lateinit var playersnames_sortbtn: ImageButton
    lateinit var playersnames_back_btn: ImageButton
    lateinit var playersnames_playersname: TextView

    var players_data_array: ArrayList<AllPlayersDataModel>? = ArrayList()
    var sortedByFirstName_playersname = ArrayList<AllPlayersDataModel>()
    var sortedByLastName_playersname = ArrayList<AllPlayersDataModel>()
    var byWhichTpyeSsorted: String? = null
    var country_name: String? = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_players_names)

        playersnames_mainlay = findViewById(R.id.playersnames_mainlay)
        playersnames_countryname = findViewById(R.id.playersnames_countryname)
        playersnames_captainname = findViewById(R.id.playersnames_captainname)
        playersnames_sortbtn = findViewById(R.id.playersnames_sortbtn)
        playersnames_playersname = findViewById(R.id.playersnames_playersname)
        playersnames_back_btn = findViewById(R.id.playersnames_back_btn)

        setPlayersNameValues()

        playersnames_sortbtn.setOnClickListener {
            val dialog = Dialog(this@PlayersNamesActivity)
            dialog.setContentView(R.layout.sortplayers_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(true)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.attributes.windowAnimations = android.R.style.Animation_Dialog

            val sortplayers_group = dialog.findViewById<RadioGroup>(R.id.sortplayers_group)
            val sortplayers_cancelbtn = dialog.findViewById<TextView>(R.id.sortplayers_cancelbtn)
            dialog.show()

            if (byWhichTpyeSsorted != null) {
                if (byWhichTpyeSsorted.equals("firstnamesort"))
                    sortplayers_group.check(R.id.sortplayers_byfirstname)
                else if (byWhichTpyeSsorted.equals("lastnamesort"))
                    sortplayers_group.check(R.id.sortplayers_bylastname)
            }

            sortplayers_group.setOnCheckedChangeListener { group: RadioGroup?, checkedId: Int ->

                if (checkedId == R.id.sortplayers_byfirstname) {
                    byWhichTpyeSsorted = "firstnamesort"
                } else if (checkedId == R.id.sortplayers_bylastname) {
                    byWhichTpyeSsorted = "lastnamesort"
                } else {
                    byWhichTpyeSsorted = null
                }
                creatingSortedArrayList(players_data_array!!, byWhichTpyeSsorted)
                dialog.dismiss()
            }

            sortplayers_cancelbtn.setOnClickListener { dialog.cancel() }
        }

        playersnames_back_btn.setOnClickListener { finish() }
    }

    private fun setPlayersNameValues() {

        var players_name_txt: String? = ""
        val intent = intent
        country_name = intent.getStringExtra("this_country_name")
        players_data_array = intent.getParcelableArrayListExtra("this_country_players_data")

        if (country_name != null) {
            playersnames_countryname.text = country_name
        }
        if (players_data_array?.size!! > 0) {

            players_name_txt = ""

            for (i in players_data_array!!) {
                if (i.playerIsCaptain) {
                    playersnames_captainname.text = "${i.playerFirstname}  ${i.playerLastname}"
                    break
                }
            }
            loadPlayers(players_data_array!!)
        }
    }

    private fun creatingSortedArrayList(
        arrayList: ArrayList<AllPlayersDataModel>,
        whichType: String?
    ) {
        sortedByFirstName_playersname = arrayList
        sortedByLastName_playersname = arrayList

        if (whichType == "firstnamesort") {
            sortedByFirstName_playersname.sortBy { it.playerFirstname }
            loadPlayers(sortedByFirstName_playersname)
        } else if (whichType == "lastnamesort") {
            sortedByLastName_playersname.sortBy { it.playerLastname }
            loadPlayers(sortedByLastName_playersname)
        }

    }

    private fun loadPlayers(array: ArrayList<AllPlayersDataModel>) {

        var players_name_txt: String? = ""
        var num = 0
        for (i in array) {
            if (!i.playerIsCaptain) {
                num += 1
                players_name_txt =
                    "$players_name_txt$num.   ${i.playerFirstname}   ${i.playerLastname}\n"
            }
        }
        playersnames_playersname.text = players_name_txt
    }

}