package com.example.boardgamegeek.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.boardgamegeek.R
import com.example.boardgamegeek.models.Game
import com.example.boardgamegeek.models.GameRanking
import java.text.SimpleDateFormat

class RankingAdapter(context: Context, ranking: MutableList<GameRanking>) :
    ArrayAdapter<GameRanking>(context, 0, ranking) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val gameRanking = getItem(position)
        val currentView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_ranking, parent, false)

        val textRanking = currentView.findViewById<TextView>(R.id.textRanking)
        val textDate = currentView.findViewById<TextView>(R.id.textDate)


        if(gameRanking != null){
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

            textDate.text = dateFormat.format(gameRanking.date)
            textRanking.text = gameRanking.ranking
        }

        return currentView
    }
}