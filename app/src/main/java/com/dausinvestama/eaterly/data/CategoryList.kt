package com.dausinvestama.eaterly.data

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.dausinvestama.eaterly.fragment.HomeFragment

data class CategoryList( var Categorylist: String, var ImageList: String , var id_kategori: Int)
    : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, p1: Int) {
        dest.writeString(Categorylist)
        dest.writeString(ImageList)
        dest.writeInt(id_kategori)
    }

    companion object CREATOR : Parcelable.Creator<CategoryList> {
        override fun createFromParcel(parcel: Parcel): CategoryList {
            return CategoryList(parcel)
        }

        override fun newArray(size: Int): Array<CategoryList?> {
            return arrayOfNulls(size)
        }
    }
}