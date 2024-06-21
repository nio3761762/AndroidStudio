package com.example.proyemergentes.dataclass

data class Reservas(var ReservaID: Int,
                    var UsuarioID: Users,
                    var HorarioID: Horarios,
                    var FechaReserva: String,
                    var Asientos: Int,
                    var Estado: String,
                    var RAsientos: Int,
                    var PrecioTotal:Int)
