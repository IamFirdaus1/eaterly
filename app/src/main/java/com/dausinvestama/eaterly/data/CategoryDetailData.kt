package com.dausinvestama.eaterly.data

import android.os.Parcel
import android.os.Parcelable

data class CategoryDetailData(var namamakanan: String?, var idmakanan: Long, var idjenis: Long, var hargamakanan: Long, var idkantin: Long, var deskripsimakanan: String?, var namakantin: String?, var jumlahantrian: Int)
    : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(namamakanan)
        parcel.writeLong(idmakanan)
        parcel.writeLong(idjenis)
        parcel.writeLong(hargamakanan)
        parcel.writeLong(idkantin)
        parcel.writeString(deskripsimakanan)
        parcel.writeString(namakantin)
        parcel.writeInt(jumlahantrian)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CategoryDetailData> {
        override fun createFromParcel(parcel: Parcel): CategoryDetailData {
            return CategoryDetailData(parcel)
        }

        override fun newArray(size: Int): Array<CategoryDetailData?> {
            return arrayOfNulls(size)
        }
    }
}