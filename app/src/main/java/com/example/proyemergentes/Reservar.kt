package com.example.proyemergentes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.DatePicker
import android.widget.EditText
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyemergentes.Intrface.RetrofitCliente
import com.example.proyemergentes.databinding.ActivityMainBinding
import com.example.proyemergentes.databinding.ActivityReservarBinding
import com.example.proyemergentes.dataclass.AddReserva
import com.example.proyemergentes.dataclass.Buses
import com.example.proyemergentes.dataclass.Horarios
import com.example.proyemergentes.dataclass.ModReserva
import com.example.proyemergentes.dataclass.Reservas
import com.example.proyemergentes.dataclass.Rutas
import com.example.proyemergentes.dataclass.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.text.SimpleDateFormat

class Reservar : AppCompatActivity() {
    private lateinit var datePicker: DatePicker
    lateinit var  binding: ActivityReservarBinding
    var fechaHoy=""
    private lateinit var horarioss: Horarios
    private lateinit var users: Users
    private lateinit var reserva: Reservas
    private lateinit var addReserva: AddReserva
    private lateinit var modReserva: ModReserva

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locale = Locale("es")
        Locale.setDefault(locale)


        val config = resources.configuration
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        enableEdgeToEdge()
        binding=ActivityReservarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var calendar1 = Calendar.getInstance()
        var year1 = calendar1.get(Calendar.YEAR)
        var month1 = calendar1.get(Calendar.MONTH) + 1 // Calendar.MONTH devuelve meses indexados desde 0
        var day1 = calendar1.get(Calendar.DAY_OF_MONTH)

// Formatear la fecha como String (opcional)

        fechaHoy = "$year1-$month1-$day1"


        horarioss = intent.getParcelableExtra("horarios")!!
        users = intent.getParcelableExtra("user")!!



        reserva = Reservas(
            ReservaID = -1,
            UsuarioID = users,
            HorarioID = horarioss,
            FechaReserva = fechaHoy,
            Asientos = -1,
            Estado = "A",
            RAsientos = -1,
            PrecioTotal = -1
        )




        binding.textHora.text=horarioss.HoraSalida
        binding.Price.text=horarioss.Precio.toString()
        datePicker = findViewById(R.id.datePicker)
        val calendar = Calendar.getInstance()

        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val horaSalidaParts = horarioss.HoraSalida.split(":")
        val horaSalida = horaSalidaParts[0].toInt()
        val minutoSalida = horaSalidaParts[1].toInt()
        // Si la hora actual es después de las 18:00, establecer la fecha mínima al día siguiente
        if (currentHour >= horaSalida) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            day1 = calendar1.get(Calendar.DAY_OF_MONTH)
        }

        fechaHoy = "$year1-$month1-$day1"
        Log.e("Segurousuario", "Las claves 'Usuario' o 'Password' no están presentes en el JSON ${fechaHoy}")
        datePicker.minDate = calendar.timeInMillis

        // Configurar el DatePicker
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { _, year, month, day ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, day)

            // Verificar si la fecha debe ser bloqueada
            if (shouldBlockDate(selectedDate)) {
                // Si la fecha debe ser bloqueada, restablecer a la fecha mínima permitida
                datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)+1)
                Toast.makeText(this, "Fecha bloqueada debido a la hora actual", Toast.LENGTH_SHORT).show()
            }
        }

        setupDatePicker()

        buscarReserva()
        binding.btnAceptar.setOnClickListener {
            val raText = binding.editTextNumberSigned.text.toString()
            val ra = raText.toInt()
            if (ra < 1 || ra > 6 || (reserva.RAsientos-ra)<0 ) {
                if((reserva.RAsientos-ra)<0)
                    binding.editTextNumberSigned.error = "Sobrepasa la cantidad de hacientos disponibles"
                else
                    binding.editTextNumberSigned.error = "Solo se puede reservar hasta 6 asientos"
                return@setOnClickListener
            }else
                if((reserva.RAsientos-ra)<0 && (ra > 1 && ra < 7) ){
                    binding.editTextNumberSigned.error = "Sobrepasa la cantidad de hacientos disponibles"
                    return@setOnClickListener
                }else
                    if (reserva.ReservaID != -1) {
                        updateReserva()
//                agregarReserva()
                    } else {
                        agregarReserva()
                    }
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("horario", horarioss)
            intent.putExtra("user", users)
            startActivity(intent)

        }

        binding.btnSC.setOnClickListener {

            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("horario", horarioss)
            intent.putExtra("user", users)
            startActivity(intent)

        }


    }



    private fun setupDatePicker() {
        // Obtener la fecha y hora actual
        val calendar = Calendar.getInstance()

        // Configurar el DatePicker para mostrar solo fechas futuras
        datePicker.minDate = calendar.timeInMillis

        // Manejar la selección de fecha
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)) { _, year, month, day ->
            // Obtener la fecha seleccionada
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, day)

            // Realizar la consulta usando la fecha seleccionada
            consultarReservasPorFecha(selectedDate)
        }
    }
    private fun buscarReserva() {
        limpiarReserva()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = RetrofitCliente.webService.obtenerReservaActivo(fechaHoy, horarioss.HorarioID, "A", horarioss.BusID.BusID)
                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        val newUser = call.body()
                        if (newUser != null) {
                            reserva = newUser
                            binding.AsiDisponible.text = newUser.Asientos.toString()
                            reserva.RAsientos= newUser.Asientos
                            Log.d("SeguroReservaEncontrada", "Reserva agregada exitosamente $newUser")
                        } else {
                            binding.AsiDisponible.text = horarioss.BusID.Capacidad.toString()
                            reserva.RAsientos= horarioss.BusID.Capacidad
                        }
                        actualizarColorBoton()
                    } else {
                        binding.AsiDisponible.text = horarioss.BusID.Capacidad.toString()
                        reserva.RAsientos= horarioss.BusID.Capacidad
                        actualizarColorBoton()
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", "Error en la búsqueda de reserva: ${e.message}")
            }
        }
    }


    private fun consultarReservasPorFecha(selectedDate: Calendar) {
        fechaHoy=formatDate(selectedDate.time)
        buscarReserva()
        //  Toast.makeText(this@Reservar, "hoy ${fechaHoy}", Toast.LENGTH_SHORT).show()




    }
    private fun updateReserva() {
        Log.e("SeguroParaACtualizarReserva", "Error en la búsqueda de reserva: $reserva")
        val raText = binding.editTextNumberSigned.text.toString()
        val ra = raText.toInt()
        modReserva = ModReserva(
            UsuarioID = users.Usuario,
            HorarioID = horarioss.HorarioID,
            FechaReserva = fechaHoy,
            Asientos = reserva.RAsientos - ra,
            Estado = "A",
            RAsientos = ra,
            PrecioTotal = horarioss.Precio * ra,
            Destino= horarioss.RutaID.Destino
        )
        Log.d("Seguroparaactualizar", "Reserva agregada exitosamente $modReserva")
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val call = RetrofitCliente.webService.UpdateReserva(reserva.ReservaID, modReserva)
                withContext(Dispatchers.Main) {
                    if (!call.isSuccessful) {
                        Log.e("Error", "Error al actualizar la reserva ${call.body()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", "Error en la actualización de reserva: ${e.message}")
            }
        }
    }

    private fun agregarReserva() {
        val raText = binding.editTextNumberSigned.text.toString()
        val ra = raText.toInt()
        addReserva = AddReserva(
            UsuarioID = users.Usuario,
            HorarioID = horarioss.HorarioID,
            FechaReserva = fechaHoy,
            Asientos = reserva.RAsientos - ra,
            Estado = "A",
            RAsientos = ra,
            PrecioTotal = horarioss.Precio * ra,
            Destino= horarioss.RutaID.Destino
        )
        Log.e("SeguroParaAgregarReserva", "Error en la búsqueda de reserva: $reserva")
        CoroutineScope(Dispatchers.IO).launch {
            try {


                val call = RetrofitCliente.webService.AgregarReserva(addReserva)
                withContext(Dispatchers.Main) {
                    if (call.isSuccessful) {
                        Log.d("SeguroReserva", "Reserva agregada exitosamente ${call.body()}")
                    } else {
                        Log.e("Error", "Error al agregar la reserva")
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", "Error en la creación de reserva: ${e.message}")
            }
        }
    }

    private fun limpiarReserva() {
        reserva = Reservas(
            ReservaID = -1,
            UsuarioID = users,
            HorarioID = horarioss,
            FechaReserva = fechaHoy,
            Asientos = -1,
            Estado = "A",
            RAsientos = -1,
            PrecioTotal = -1
        )
    }

    private fun formatDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(date)
    }

    private fun shouldBlockDate(date: Calendar): Boolean {
        val currentTime = Calendar.getInstance()
        val horaSalidaParts = horarioss.HoraSalida.split(":")
        val horaSalida = horaSalidaParts[0].toInt()
        val minutoSalida = horaSalidaParts[1].toInt()

        if (date.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) &&
            date.get(Calendar.MONTH) == currentTime.get(Calendar.MONTH) &&
            date.get(Calendar.DAY_OF_MONTH) == currentTime.get(Calendar.DAY_OF_MONTH)) {

            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = currentTime.get(Calendar.MINUTE)

            if (currentHour > horaSalida) {
                return true
            } else if (currentHour == horaSalida && currentMinute >= minutoSalida) {
                return true
            }
        }
        return false
    }


    private fun shouldAdjustDate(date: Calendar): Boolean {
        val currentTime = Calendar.getInstance()

        // Obtener la hora de salida del horario actual
        val horaSalidaParts = horarioss.HoraSalida.split(":")
        val horaSalida = horaSalidaParts[0].toInt()
        val minutoSalida = horaSalidaParts[1].toInt()

        // Comparar si la fecha seleccionada es hoy y la hora actual es mayor o igual a la hora de salida
        if (date.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) &&
            date.get(Calendar.MONTH) == currentTime.get(Calendar.MONTH) &&
            date.get(Calendar.DAY_OF_MONTH) == currentTime.get(Calendar.DAY_OF_MONTH)) {

            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = currentTime.get(Calendar.MINUTE)

            if (currentHour > horaSalida) {
                // Si la hora actual es mayor que la hora de salida, ajustar la fecha al día siguiente
                return true
            } else if (currentHour == horaSalida && currentMinute >= minutoSalida) {
                // Si la hora actual es igual a la hora de salida, verificar los minutos
                return true
            }
        }

        return false
    }



    private fun actualizarColorBoton() {
        val asientosDisponibles = binding.AsiDisponible.text.toString().toInt()
        val colorId = if (asientosDisponibles > 0) R.color.verde else R.color.red
        binding.btnAceptar.setBackgroundColor(ContextCompat.getColor(this, colorId))
        binding.btnAceptar.setTextColor(ContextCompat.getColor(this, R.color.blanco))
    }

}





