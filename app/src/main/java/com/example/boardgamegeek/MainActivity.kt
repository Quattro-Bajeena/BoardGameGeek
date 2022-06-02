package com.example.boardgamegeek

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamegeek.databinding.ActivityMainBinding
import com.example.boardgamegeek.models.Game
import com.example.boardgamegeek.models.User
import java.util.*

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
        binding.textSynchronization.text = user!!.syncDate.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                user = dbRepository.getUser()
                refreshControls()
            }
            if (resultCode == RESULT_CANCELED) {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Błąd przy ściąganiu danych")
                    .setNeutralButton("Close") { dialog, id -> dialog.dismiss();finishAffinity() }
                builder.show()
            }
        }
    } //onActivityResult


}