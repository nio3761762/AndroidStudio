package com.example.proyemergentes.Intrface

import com.example.proyemergentes.dataclass.AddReserva
import com.example.proyemergentes.dataclass.Horarios
import com.example.proyemergentes.dataclass.HorariosResponse
import com.example.proyemergentes.dataclass.ModReserva
import com.example.proyemergentes.dataclass.Reservas
import com.example.proyemergentes.dataclass.UserRequerido
import com.example.proyemergentes.dataclass.Users
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request

object AppConstantes{
   val url="http://192.168.0.10:3000"
}


interface WebService {
   //login
    @POST("/login")
    suspend fun login(
       @Body loginRequest: UserRequerido): Response<Users>
    //users
    //listar
    @GET("/users")
    suspend fun  obtenerUsers():Response<Users>
// recuperar usuario
    @GET("/users/{id}")
    suspend fun  obtenerUser(
        @Path("id") Usuario:String,
    ):Response<Users>
//Nuevo usuario
    @POST("/addusers")
    suspend fun  agregarUser(
       @Body user:Users
    ):Response<String>

    @PUT("/users/{id}")
    suspend fun Updateuser(
       @Path("id") Usuario:String,
       @Body user:Users
    ):Response<String>
    //Horarios
    @GET("/horarios")
    suspend fun  obtenerHorarios():Response<HorariosResponse>


    @GET("/horarios/{id}")
    suspend fun  obtenerHorario(
        @Path("id") HorarioID: Int,
    ):Response<Horarios>

    //Reservas
    //Nueva reserva
    @POST("/reservasadd")
    suspend fun  AgregarReserva(
        @Body reservas: AddReserva
    ):Response<Reservas>
//actualizar reservas
    @PUT("/reservas/{id}")
    suspend fun UpdateReserva(
        @Path("id") ReservaID:Int,
        @Body reservas: ModReserva
    ):Response<String>
//recuperar reserva
    @GET("/reservas/{id}")
    suspend fun  obtenerReserva(
        @Path("id") ReservaID:Int,
    ):Response<Reservas>
    //Buscar para reservar
    @GET("/reservas/{fecha}/{horarioID}/{estado}/{busID}")
    suspend fun obtenerReservaActivo(
        @Path("fecha") Fecha: String,
        @Path("horarioID") HorarioID: Int,
        @Path("estado") Estado: String,
        @Path("busID") BusID: Int
    ): Response<Reservas>

}
object RetrofitCliente {
    private var authToken: String? = null // Variable para almacenar el token JWT

    // Método para establecer dinámicamente el token JWT
    fun setAuthToken(token: String?) {
        authToken = token
    }

    val webService: WebService by lazy {
        val client = OkHttpClient.Builder().apply {
            addInterceptor(AuthInterceptor()) // Agregar interceptor personalizado
        }.build()

        Retrofit.Builder()
            .baseUrl(AppConstantes.url)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build()
            .create(WebService::class.java)
    }

    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            var request = chain.request()
            authToken?.let { token ->
                request = request.newBuilder()
                    .header("Authorization", "Bearer $token") // Agregar token a los encabezados
                    .build()
            }
            return chain.proceed(request)
        }
    }
}


