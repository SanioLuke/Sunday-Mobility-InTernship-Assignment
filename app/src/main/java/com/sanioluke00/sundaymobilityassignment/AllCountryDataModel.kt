package com.sanioluke00.sundaymobilityassignment

import android.os.Parcel
import android.os.Parcelable

data class AllCountryDataModel(
    val countryname: String,
    val countryDataArray: ArrayList<AllPlayersDataModel>
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        arrayListOf<AllPlayersDataModel>().apply {
            parcel.readList(
                this,
                AllPlayersDataModel::class.java.classLoader
            )
        }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(countryname)
        parcel.writeTypedArray(countryDataArray.toTypedArray(), flags)
/*        if (Build.VERSION.SDK_INT >= 29) {
            parcel.writeParcelableList(countryDataArray,flags)
        } else {
            parcel.writeList(countryDataArray as List<AllPlayersDataModel>)
        }*/
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllCountryDataModel> {
        override fun createFromParcel(parcel: Parcel): AllCountryDataModel {
            return AllCountryDataModel(parcel)
        }

        override fun newArray(size: Int): Array<AllCountryDataModel?> {
            return arrayOfNulls(size)
        }
    }
}