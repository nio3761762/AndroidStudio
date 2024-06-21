package com.example.proyemergentes.dataclass

import android.os.Parcel
import android.os.Parcelable

data class Buses(
    var BusID: Int,
    var Placa: String,
    var Modelo: String,
    var Capacidad: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(BusID)
        parcel.writeString(Placa)
        parcel.writeString(Modelo)
        parcel.writeInt(Capacidad)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Buses> {
        override fun createFromParcel(parcel: Parcel): Buses {
            return Buses(parcel)
        }

        override fun newArray(size: Int): Array<Buses?> {
            return arrayOfNulls(size)
        }
    }
}
