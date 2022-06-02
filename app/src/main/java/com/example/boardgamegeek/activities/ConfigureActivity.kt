package com.example.boardgamegeek.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.boardgamegeek.DbRepository
import com.example.boardgamegeek.UserDownloader
import com.example.boardgamegeek.databinding.ActivityConfigureBinding
import com.example.boardgamegeek.models.User
import com.google.android.material.snackbar.Snackbar
import java.util.*


class ConfigureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigureBinding
    private var userDownloader: UserDownloader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonOk.setOnClickListener{CreateUser().execute()}
    }

    @Suppress("DEPRECATION")
    private inner class CreateUser : AsyncTask<String, Int, String>(){
        override fun onPreExecute() {
            super.onPreExecute()
            var username = binding.editTextUsername.text.toString()
            val filesDir = baseContext.filesDir
            userDownloader = UserDownloader(username, filesDir)
            Snackbar.make(
                binding.root,
                "Loading user info",
                Snackbar.LENGTH_LONG
            ).show()

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(result == "success"){
                userDownloader!!.loadCurrentUserInfo()
                val user = User(userDownloader!!.userName, userDownloader!!.gameAmount!!, Calendar.getInstance().time)

                val dbRepository = DbRepository(this@ConfigureActivity, null, null, 1)
                dbRepository.addUser(user)
                dbRepository.addGames(userDownloader!!.games)
                dbRepository.addGameRankings(user.syncDate, userDownloader!!.games)
                val returnIntent = Intent()
                setResult(RESULT_OK, returnIntent)
                finish()
            }
            else{
                val returnIntent = Intent()
                setResult(2, returnIntent)
                finish()
            }

        }

        override fun doInBackground(vararg p0: String?): String {
            val success = userDownloader!!.downloadUserInfo()
            if(success == false){

                return "fail"
            }
            return "success"
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}