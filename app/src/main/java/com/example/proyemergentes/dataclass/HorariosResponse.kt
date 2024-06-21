package com.example.proyemergentes.dataclass

import com.google.gson.annotations.SerializedName

data class HorariosResponse(
    @SerializedName("listarHorarios") var listarHorarios:ArrayList<Horarios>
)
