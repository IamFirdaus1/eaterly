package com.dausinvestama.eaterly.data

import android.os.Parcel
import android.os.Parcelable

class JenisList(  var imageList: String, var id_jenis: Int, var nama_jenis: String)
    : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageList)
        parcel.writeInt(id_jenis)
        parcel.writeString(nama_jenis)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<JenisList> {
        override fun createFromParcel(parcel: Parcel): JenisList {
            return JenisList(parcel)
        }

        override fun newArray(size: Int): Array<JenisList?> {
            return arrayOfNulls(size)
        }
    }
}