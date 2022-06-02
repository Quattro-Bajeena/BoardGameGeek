package com.example.boardgamegeek.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.example.boardgamegeek.DbRepository
import com.example.boardgamegeek.adapters.GamesAdapter
import com.example.boardgamegeek.databinding.ActivityGamesBinding
import com.example.boardgamegeek.models.Game

class GamesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGamesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.listGames.setOnItemClickListener(this::showGameRanking)

        val category = intent.extras!!.getString("category")!!

        val dbRepository = DbRepository(this, null, null, 1)
        var games = dbRepository.getGames(category)

        var adapter = GamesAdapter(this, games)
        binding.listGames.adapter = adapter



    }



    private fun showGameRanking(parent: AdapterView<*>, view: View, position: Int, id: Long){
        val game = parent.getItemAtPosition(position) as Game
        val intent = Intent(this, GameRankingActivity::class.java)
        intent.putExtra("id", game.id)
        startActivity(intent)
    }
}