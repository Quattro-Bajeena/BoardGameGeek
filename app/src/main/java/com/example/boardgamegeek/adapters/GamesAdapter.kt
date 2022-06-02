package com.example.boardgamegeek.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.boardgamegeek.R
import com.example.boardgamegeek.models.Game

class GamesAdapter(context: Context, games: MutableList<Game>) :
    ArrayAdapter<Game>(context, 0, games) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val game = getItem(position)
        val currentView = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_game, parent, false)

        val textId = currentView.findViewById<TextView>(R.id.textId)
        val textTitle = currentView.findViewById<TextView>(R.id.textTitle)
        val textPublished = currentView.findViewById<TextView>(R.id.textPublished)
        val textRanking = currentView.findViewById<TextView>(R.id.textRanking)


        if(game != null){
            textId.text = game.id
            textTitle.text = game.name
            textPublished.text = game.published
            textRanking.text = game.ranking
        }

        return currentView
    }
}