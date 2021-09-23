package com.sanioluke00.sundaymobilityassignment

import android.os.Parcel
import android.os.Parcelable

data class AllPlayersDataModel(
    val playerFirstname: String,
    val playerLastname: String,
    val playerIsCaptain: Boolean
) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(playerFirstname)
        parcel.writeString(playerLastname)
        parcel.writeByte(if (playerIsCaptain) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AllPlayersDataModel> {
        override fun createFromParcel(parcel: Parcel): AllPlayersDataModel {
            return AllPlayersDataModel(parcel)
        }

        override fun newArray(size: Int): Array<AllPlayersDataModel?> {
            return arrayOfNulls(size)
        }
    }
}