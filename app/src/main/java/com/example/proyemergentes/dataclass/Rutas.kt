package com.example.proyemergentes.dataclass

import android.os.Parcel
import android.os.Parcelable
data class Rutas(
    var RutaID: Int,
    var Origen: String,
    var Destino: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(RutaID)
        parcel.writeString(Origen)
        parcel.writeString(Destino)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Rutas> {
        override fun createFromParcel(parcel: Parcel): Rutas {
            return Rutas(parcel)
        }

        override fun newArray(size: Int): Array<Rutas?> {
            return arrayOfNulls(size)
        }
    }
}
