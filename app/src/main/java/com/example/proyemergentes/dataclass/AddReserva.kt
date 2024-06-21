package com.example.proyemergentes.dataclass

data class AddReserva(
                      var UsuarioID: String,
                      var HorarioID: Int,
                      var FechaReserva: String,
                      var Asientos: Int,
                      var Estado: String,
                      var RAsientos: Int,
                      var PrecioTotal:Int,
                      var Destino:String)

