package com.example.proyemergentes.dataclass

import android.os.Parcel
import android.os.Parcelable
import com.example.proyemergentes.dataclass.Buses
import com.example.proyemergentes.dataclass.Rutas

data class Horarios(
    var HorarioID: Int,
    var RutaID: Rutas,
    var BusID: Buses,
    var FechaSalida: String,
    var HoraSalida: String,
    var FechaLlegada: String,
    var Precio: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(Rutas::class.java.classLoader) ?: Rutas(0, "", ""),
        parcel.readParcelable(Buses::class.java.classLoader) ?: Buses(0, "", "", 0),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(HorarioID)
        parcel.writeParcelable(RutaID, flags)
        parcel.writeParcelable(BusID, flags)
        parcel.writeString(FechaSalida)
        parcel.writeString(HoraSalida)
        parcel.writeString(FechaLlegada)
        parcel.writeInt(Precio)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Horarios> {
        override fun createFromParcel(parcel: Parcel): Horarios {
            return Horarios(parcel)
        }

        override fun newArray(size: Int): Array<Horarios?> {
            return arrayOfNulls(size)
        }
    }
}
