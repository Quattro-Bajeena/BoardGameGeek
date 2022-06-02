package com.example.boardgamegeek

import android.os.AsyncTask
import com.example.boardgamegeek.models.Game
import com.example.boardgamegeek.models.User
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.URL
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory

class UserDownloader{

    private val userFilename = "user.xml"
    private var filesDirectory: File

    var games: MutableList<Game> = mutableListOf()

    var userName: String
    var gameAmount: Int? = null


    constructor(user_name: String, files_dir:File){
        userName = user_name
        filesDirectory = files_dir
    }


    fun downloadUserInfo():Boolean{

        try{
            val url = URL("https://boardgamegeek.com/xmlapi2/collection?username=$userName&stats=1")
            val connection = url.openConnection()
            connection.connect()
            val lengthOfFile = connection.contentLength
            val isStream = url.openStream()

            val fos = FileOutputStream("$filesDirectory/$userFilename")
            val data = ByteArray(1024)
            var count = 0
            var total:Long = 0
            var progress = 0
            while(count != -1){
                total += count.toLong()
                val progress_temp = total.toInt()*100/lengthOfFile
                if(progress_temp%10 == 0 && progress != progress_temp){
                    progress = progress_temp
                }
                fos.write(data, 0, count)
                count = isStream.read(data)
            }
            isStream.close()
            fos.close()
            return true
        }
        catch (e: Exception){
            return false
        }

    }

    fun loadCurrentUserInfo(){
        val file = File("$filesDirectory/$userFilename")
        val xmlDoc: Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        xmlDoc.documentElement.normalize()

        val root = xmlDoc.documentElement as Element
        gameAmount = Integer.parseInt(root.getAttribute("totalitems"))

        val items: NodeList = xmlDoc.getElementsByTagName("item")




            for(i in 0..items.length-1){
            val itemNode: Node = items.item(i)
            if(itemNode.nodeType == Node.ELEMENT_NODE){
                val elem = itemNode as Element
                val children = elem.childNodes

                var id: String? =  elem.getAttribute("objectid")
                var name: String? = null
                var thumbnail: String? = null
                var published: String? = null
                var ranking: String? = null
                var subtype: String? = elem.getAttribute("subtype")

                for(j in 0..children.length-1){
                    val node = children.item(j)
                    if(node is Element){
                        when(node.nodeName){
                            "name" -> {
                                name = node.textContent
                            }
                            "yearpublished" -> {
                                published = node.textContent
                            }
                            "thumbnail" -> {
                                thumbnail = node.textContent
                            }
                            "stats" -> {
                                for(l in 0..node.childNodes.length-1){
                                    val nodeChild = node.childNodes.item(l)
                                    if(nodeChild is Element){
                                        if(nodeChild.nodeName == "rating"){
                                            for(k in 0..nodeChild.childNodes.length-1){
                                                val ratingChildNode = nodeChild.childNodes.item(k)
                                                if(ratingChildNode is Element){
                                                    if(ratingChildNode.nodeName == "ranks"){
                                                        for(h in 0..ratingChildNode.childNodes.length-1){
                                                            val ranksChildNode = ratingChildNode.childNodes.item(h)
                                                            if(ranksChildNode is Element){
                                                                if(ranksChildNode.getAttribute("type") == "subtype" && ranksChildNode.getAttribute("name") == "boardgame"){
                                                                    ranking = ranksChildNode.getAttribute("value")
                                                                }

                                                            }
                                                        }


                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            }

                        }
                    }
                }
                if(ranking == null){
                    ranking = "Not Ranked"
                }
                if(id != null && name != null && thumbnail != null && published != null && ranking != null && subtype != null){
                    val game = Game(id, name, thumbnail, published, ranking, subtype)
                    // download image?
                    games.add(game)
                }

            }
        }

    }



}

fun main(args:Array<String>){
    val downloader = UserDownloader("loutre_on_fire", File("files"))
    downloader.downloadUserInfo()
}