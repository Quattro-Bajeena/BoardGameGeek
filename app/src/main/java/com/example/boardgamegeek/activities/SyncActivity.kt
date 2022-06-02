package com.example.boardgamegeek.activities

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.boardgamegeek.DbRepository
import com.example.boardgamegeek.UserDownloader
import com.example.boardgamegeek.databinding.ActivityGameRankingBinding
import com.example.boardgamegeek.databinding.ActivitySyncBinding
import com.example.boardgamegeek.models.User
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SyncActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySyncBinding


    lateinit var lastSync: Date
    lateinit var username: String
    lateinit var userDownloader: UserDownloader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySyncBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lastSync = Date(intent.extras!!.getLong("lastSync")!!)
        username = intent.extras!!.getString("username")!!
        userDownloader = UserDownloader(username, baseContext.filesDir)


        binding.textLastSync.text = SimpleDateFormat("yyyy-MM-dd HH:mm").format(lastSync)
        binding.buttonSyncNow.setOnClickListener{sync()}

    }

    fun sync(){
        val currentTime = Calendar.getInstance().time.time
        val timeSinceSync = currentTime.minus(lastSync.time)

        if( timeSinceSync < TimeUnit.DAYS.toMillis(1) ){
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Minęły mniej niż 24 godziny od ostatniej synchronizacji")
                .setNeutralButton("Anuluj") { dialog, id -> dialog.dismiss() }
                .setPositiveButton("Synchronizuj") {dialog, id -> CreateUser().execute()}
            builder.show()
        }
        else{
            CreateUser().execute()
        }


    }

    @Suppress("DEPRECATION")
    private inner class CreateUser : AsyncTask<String, Int, String>(){
        override fun onPreExecute() {
            super.onPreExecute()
            Snackbar.make(
                binding.root,
                "Updating user info",
                Snackbar.LENGTH_LONG
            ).show()

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            val returnIntent = Intent()
            if(result == "success"){
                userDownloader!!.loadCurrentUserInfo()
                val user = User(userDownloader!!.userName, userDownloader!!.gameAmount!!, Calendar.getInstance().time)

                val dbRepository = DbRepository(this@SyncActivity, null, null, 1)
                dbRepository.update(user, userDownloader!!.games)

                setResult(RESULT_OK, returnIntent)
            }
            else{
                setResult(2, returnIntent)
            }
            finish()

        }

        override fun doInBackground(vararg p0: String?): String {
            val success = userDownloader!!.downloadUserInfo()
            if(success == false){

                return "fail"
            }
            return "success"
        }
    }
}