package com.dausinvestama.eaterly.data

import android.os.Parcel
import android.os.Parcelable

data class KantinList(var ImageList: String, var NamaKantin: String, var idkantin: Int, var orderid: Int) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ImageList)
        parcel.writeString(NamaKantin)
        parcel.writeInt(idkantin)
        parcel.writeInt(idkantin)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<KantinList> {
        override fun createFromParcel(parcel: Parcel): KantinList {
            return KantinList(parcel)
        }

        override fun newArray(size: Int): Array<KantinList?> {
            return arrayOfNulls(size)
        }
    }

}