package com.example.boardgamegeek.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamegeek.DbRepository
import com.example.boardgamegeek.UserDownloader
import com.example.boardgamegeek.databinding.ActivityMainBinding
import com.example.boardgamegeek.models.Game
import com.example.boardgamegeek.models.User
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userDownloader: UserDownloader
    private lateinit var dbRepository: DbRepository

    private var user: User? = null
    var games: MutableList<Game> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.buttonGames.setOnClickListener {showGames("games")}
        binding.buttonAddons.setOnClickListener {showGames("expansions")}
        binding.buttonSync.setOnClickListener{showSync()}
        binding.buttonClearData.setOnClickListener{askClearData()}


        dbRepository = DbRepository(this, null, null,1)
        initialize()


    }

    fun initialize(){
        user = dbRepository.getUser()
        if(user == null){
            val intent = Intent(this, ConfigureActivity::class.java)
            startActivityForResult(intent, 1)
        }
        else{
            refreshControls()
        }


    }

    fun refreshControls(){
        binding.textUsername.text = user!!.username
        binding.textGameAmount.text = user!!.gameAmount.toString()
        binding.textAddonAmount.text = "0"

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        binding.textSynchronization.text = dateFormat.format(user!!.syncDate)
    }

    fun showGames(category:String){
        val intent = Intent(this, GamesActivity::class.java)
        intent.putExtra("category", category)
        startActivity(intent)
    }

    fun showSync(){
        val intent = Intent(this, SyncActivity::class.java)
        intent.putExtra("lastSync", user!!.syncDate.time)
        intent.putExtra("username", user!!.username)
        startActivityForResult(intent, 1)
    }

    fun askClearData(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Czy chcesz wyczyścić dane?")
            .setNegativeButton("Anuluj") { dialog, id -> dialog.dismiss() }
            .setPositiveButton("Wyczyść") {dialog, id -> clearData()}
        builder.show()
    }

    fun clearData(){
        dbRepository.clearData()
        finishAffinity()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                user = dbRepository.getUser()
                refreshControls()
            }
            if (resultCode == 2) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Błąd przy ściąganiu danych")
                    .setNeutralButton("Anuluj") { dialog, id -> dialog.dismiss();finishAffinity() }
                builder.show()
            }
        }
    }




}