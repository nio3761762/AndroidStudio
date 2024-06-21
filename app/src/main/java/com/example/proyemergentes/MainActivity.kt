package com.example.proyemergentes
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.proyemergentes.Intrface.RetrofitCliente
import com.example.proyemergentes.databinding.ActivityMainBinding
import com.example.proyemergentes.dataclass.Buses
import com.example.proyemergentes.dataclass.Horarios
import com.example.proyemergentes.dataclass.Rutas
import com.example.proyemergentes.dataclass.Users
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity(),CustomAdapter.OnItemClicked {
    lateinit var  binding:ActivityMainBinding
    var users= Users("","","","")
    lateinit var adapter:CustomAdapter
    var listarHorarios = arrayListOf<Horarios>()
    private lateinit var toolbarImage: ImageView
    private lateinit var toolbarImagecambia: ImageView
    private lateinit var customMenu: LinearLayout
    private var isOriginalImage = true
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.hide()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclreview.layoutManager=LinearLayoutManager(this)
        setupRecyclerView()


        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbarImage = findViewById(R.id.menu_toobar)
        customMenu = findViewById(R.id.custom_menu)
        toolbarImage.setOnClickListener {
            if (customMenu.visibility == View.GONE) {
                customMenu.visibility = View.VISIBLE
                customMenu.animate()
                    .translationY(0f)
                    .alpha(1.0f)
                    .setDuration(300)
                    .setListener(null)
            } else {
                customMenu.animate()
                    .translationY(-customMenu.height.toFloat())
                    .alpha(0.0f)
                    .setDuration(300)
                    .withEndAction {
                        customMenu.visibility = View.GONE
                    }
            }
            toggleImage()
        }

        obtenerhorarios()

        users = intent.getParcelableExtra("user")!!
//        CoroutineScope(Dispatchers.IO).launch {
//            val call = RetrofitCliente.webService.obtenerUser(users.Usuario)
//
//
//            withContext(Dispatchers.Main) {
//                if (call.isSuccessful) {
//                    val newUser = call.body()
//                    if (newUser != null) {
//                        users=newUser
//                    } else {
//                        //
//                    }
//                } else {
//                    //
//                }
//            }
//        }



        findViewById<Button>(R.id.menu_item_1).setOnClickListener {
            RetrofitCliente.setAuthToken("")
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            //handleMenuItemClick(1)
        }
//        findViewById<Button>(R.id.menu_item_2).setOnClickListener {
//            handleMenuItemClick(2)
//        }
//        findViewById<Button>(R.id.menu_item_3).setOnClickListener {
//            handleMenuItemClick(3)
//        }


    }
    private fun handleMenuItemClick(item: Int) {
        when (item) {
            1 -> {
                // Acción para el menú item 1

                Toast.makeText(this, "Menu Item 1 clicked", Toast.LENGTH_SHORT).show()
            }
            2 -> {
                // Acción para el menú item 2
                Toast.makeText(this, "Menu Item 2 clicked", Toast.LENGTH_SHORT).show()
            }
            3 -> {
                // Acción para el menú item 3
                Toast.makeText(this, "Menu Item 3 clicked", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun toggleImage() {
        if (isOriginalImage) {
            toolbarImage.setImageResource(R.drawable.menu) // Cambia R.drawable.new_image por tu nueva imagen
        } else {
            toolbarImage.setImageResource(R.drawable.menu__1_)
        }
        isOriginalImage = !isOriginalImage
    }

    fun obtenerhorarios(){
        CoroutineScope(Dispatchers.IO).launch {
            val call =RetrofitCliente.webService.obtenerHorarios()
            runOnUiThread {
                if(call.isSuccessful){
                    listarHorarios=call.body()!!.listarHorarios
                    setupRecyclerView()
                }else
                    Toast.makeText(this@MainActivity,"Error consultar datos",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun setupRecyclerView(){
        adapter= CustomAdapter(this, listarHorarios  )
        adapter.setOnClick(this@MainActivity)
        binding.recyclreview.adapter=adapter

    }
    override fun pasarHorario(horario: Horarios) {

        val intent = Intent(this@MainActivity, Reservar::class.java)
        intent.putExtra("horarios", horario)
        intent.putExtra("user", users)
        startActivity(intent)
    }

}