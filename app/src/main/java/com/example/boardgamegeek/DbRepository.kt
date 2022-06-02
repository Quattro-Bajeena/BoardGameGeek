package com.example.boardgamegeek

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.boardgamegeek.models.Game
import com.example.boardgamegeek.models.GameRanking
import com.example.boardgamegeek.models.User
import java.text.SimpleDateFormat
import java.util.*

class DbRepository(context: Context, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    companion object{
        private val  DATABASE_VERSION = 1
        private val DATABASE_NAME = "boardgamesDB.db"
        val TABLE_GAMES = "Games"
        val COLUMN_GAMEID = "id"
        val COLUMN_GAMENAME = "name"
        val COLUMN_GAMETHUMBNAIL = "thumbnail"
        val COLUMN_GAMEPUBLISHED = "published"
        val COLUMN_GAMERANKING = "ranking"
        val COLUMN_GAMESUBTYPE = "subtype"

        val TABLE_USER = "User"
        val COLUMN_USERNAME = "username"
        val COLUMN_USERGAMEAMOUT = "game_amount"
        val COLUMN_USERSYNCDATE = "sync_date"

        val TABLE_GAMERANKING = "GameRanking"
        val COLUMN_RANKINGID = "id"
        val COLUMN_RANKINGDATE = "date"
        val COLUMN_RANKINGRANK = "ranking"

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_GAMES_TABLE = ("CREATE TABLE " +
                TABLE_GAMES+"("+
                COLUMN_GAMEID+" INTEGER PRIMARY KEY," +
                COLUMN_GAMENAME+" TEXT,"+
                COLUMN_GAMETHUMBNAIL+" TEXT,"+
                COLUMN_GAMEPUBLISHED+" TEXT,"+
                COLUMN_GAMERANKING+" TEXT,"+
                COLUMN_GAMESUBTYPE+" TEXT"+")"
                )

        val CREATE_USER_TABLE = ("CREATE TABLE " +
                TABLE_USER+"("+
                COLUMN_USERNAME+" TEXT PRIMARY KEY," +
                COLUMN_USERGAMEAMOUT+" INTEGER,"+
                COLUMN_USERSYNCDATE+" TEXT"+")"
                )

        val CREATE_RANKING_TABLE = ("CREATE TABLE " +
                TABLE_GAMERANKING+"("+
                COLUMN_RANKINGID+" TEXT PRIMARY KEY," +
                COLUMN_RANKINGDATE+" TEXT,"+
                COLUMN_RANKINGRANK+" TEXT"+")"
                )

        db.execSQL(CREATE_GAMES_TABLE)
        db.execSQL(CREATE_USER_TABLE)
        db.execSQL(CREATE_RANKING_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES")
        onCreate(db)
    }

    fun addUser(user: User){

        val values = ContentValues()
        values.put(COLUMN_USERNAME, user.username)
        values.put(COLUMN_USERGAMEAMOUT, user.gameAmount)
        values.put(COLUMN_USERSYNCDATE, dateFormat.format(user.syncDate))
        val db = this.writableDatabase
        db.insert(TABLE_USER, null, values)

        db.close()

    }

   fun update(user: User, games: MutableList<Game>){
        val query = "DELETE FROM $TABLE_USER"
        val db = this.writableDatabase
        db.execSQL(query)

        addUser(user)

        for (game in games){
            val gameRanking = GameRanking(game.id, user.syncDate, game.ranking)
            addGameRanking(gameRanking)
        }
    }

    fun getUser():User?{
        val query = "SELECT * FROM $TABLE_USER"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        var user: User? = null

        if(cursor.moveToFirst()){
            val username = cursor.getString(0)
            val gameAmount = Integer.parseInt(cursor.getString(1))
            val syncDate = dateFormat.parse(cursor.getString(2))

            user = User(username, gameAmount, syncDate)
            cursor.close()
        }
        db.close()
        return user
    }

    fun addGames(games: MutableList<Game>){
        for (game in games){
            addGame(game)
        }
    }

    private fun addGame(game: Game){
        val values = ContentValues()
        values.put(COLUMN_GAMEID, game.id)
        values.put(COLUMN_GAMENAME, game.name)
        values.put(COLUMN_GAMETHUMBNAIL, game.thumbnail)
        values.put(COLUMN_GAMEPUBLISHED, game.published)
        values.put(COLUMN_GAMERANKING, game.ranking)
        values.put(COLUMN_GAMESUBTYPE, game.subtype)

        val db = this.writableDatabase
        db.insert(TABLE_GAMES, null, values)
        db.close()
    }

    fun getGames() : MutableList<Game>{
        val games = mutableListOf<Game>()
        val query = "SELECT * FROM $TABLE_GAMES"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if(cursor.moveToFirst()){
            while(cursor.isAfterLast == false){
                val id = cursor.getString(0)
                val name = cursor.getString(1)
                val thumbnail = cursor.getString(2)
                val published = cursor.getString(3)
                val ranking = cursor.getString(4)
                val subtype = cursor.getString(5)

                val game = Game(id, name, thumbnail, published, ranking, subtype)
                games.add(game)

                cursor.moveToNext()
            }
        }
        db.close()
        return games
    }


    fun addGameRankings(sync_date: Date, games: MutableList<Game>){
        for (game in games){
            val gameRanking = GameRanking(game.id, sync_date, game.ranking)
            addGameRanking(gameRanking)
        }
    }

    private fun addGameRanking(gameRanking: GameRanking){
        val values = ContentValues()
        values.put(COLUMN_RANKINGID, gameRanking.id)
        values.put(COLUMN_RANKINGDATE, dateFormat.format(gameRanking.date))
        values.put(COLUMN_RANKINGRANK, gameRanking.ranking)
        val db = this.writableDatabase
        db.insert(TABLE_GAMERANKING, null, values)
        db.close()
    }

    fun getGameRankings(id: String) : MutableList<GameRanking>{
        val rankings = mutableListOf<GameRanking>()
        val query = "SELECT * FROM $TABLE_GAMERANKING WHERE $COLUMN_RANKINGID = $id"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if(cursor.moveToFirst()){
            while(cursor.isAfterLast == false){
                val date = dateFormat.parse(cursor.getString(1))
                val rank = cursor.getString(2)
                val ranking = GameRanking(id, date, rank)
                rankings.add(ranking)
                cursor.moveToNext()
            }
        }
        db.close()
        return rankings
    }



    fun resetDatabase(){
        onUpgrade(this.writableDatabase, 0,0)
    }

}