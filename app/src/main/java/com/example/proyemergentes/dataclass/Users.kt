package com.example.proyemergentes.dataclass

import android.os.Parcel
import android.os.Parcelable

data class Users(var Usuario: String,
                 var Password: String,
                 var Email:String,
                 var Token:String ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Usuario)
        parcel.writeString(Password)
        parcel.writeString(Email)
        parcel.writeString(Token)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Users> {
        override fun createFromParcel(parcel: Parcel): Users {
            return Users(parcel)
        }

        override fun newArray(size: Int): Array<Users?> {
            return arrayOfNulls(size)
        }
    }
}
