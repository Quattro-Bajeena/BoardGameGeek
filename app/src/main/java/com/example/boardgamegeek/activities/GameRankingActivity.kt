package com.example.boardgamegeek.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.boardgamegeek.DbRepository
import com.example.boardgamegeek.adapters.RankingAdapter
import com.example.boardgamegeek.databinding.ActivityGameRankingBinding
import com.example.boardgamegeek.databinding.ActivityGamesBinding

class GameRankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameRankingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val id = intent.extras!!.getString("id")!!
        val dbRepository = DbRepository(this, null, null, 1)

        val rankings = dbRepository.getGameRankings(id)
        val adapter = RankingAdapter(this, rankings)
        binding.listRanking.adapter = adapter
    }


}