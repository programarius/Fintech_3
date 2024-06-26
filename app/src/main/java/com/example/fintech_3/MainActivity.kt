package com.example.fintech_3

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.listView)
        updateList()

    }

    fun onCreateClick(view: View) {
        resultLauncher.launch(Intent(this@MainActivity, AddNoteActivity::class.java))
    }

    fun onDeleteClick(view: View) {
        resultLauncher.launch(Intent(this@MainActivity, DeleteNoteActivity::class.java))
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        updateList()
    }

    fun updateList() {
        val pref = applicationContext.getSharedPreferences(getString(R.string.notes_key), Context.MODE_PRIVATE)
        val gson = Gson()

        val oldJson = pref.getString(getString(R.string.notes_key), "[]")
        val type: Type = object : TypeToken<List<Note>>() {}.type
        val arrayList: ArrayList<Note> = gson.fromJson(oldJson, type) as ArrayList<Note>

        val data: MutableList<Map<String, String?>> = ArrayList()
        for (i in 0 until arrayList.count()) {
            val datum: MutableMap<String, String?> = HashMap(2)
            datum["title"] = arrayList[i].name
            datum["subtitle"] = arrayList[i].description
            data.add(datum)
        }
        val adapter = SimpleAdapter(
            this, data,
            android.R.layout.simple_list_item_2, arrayOf("title", "subtitle"), intArrayOf(
                android.R.id.text1,
                android.R.id.text2
            )
        )

        listView.adapter = adapter
    }

    fun onApiRequestClick(view:View){
        Executors.newSingleThreadExecutor().execute {
            val url = URL("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml")

            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"  // optional default is GET

                println("\nSent 'GET' request to URL : $url; Response Code : $responseCode")

                inputStream.bufferedReader().use {
                    val pref = applicationContext.getSharedPreferences(getString(R.string.notes_key), Context.MODE_PRIVATE)
                    val editor = pref.edit()
                    val gson = Gson()

                    val oldJson = pref.getString(getString(R.string.notes_key), "[]")
                    val type: Type = object : TypeToken<List<Note>>() {}.type
                    val arrayList: ArrayList<Note> = gson.fromJson(oldJson, type) as ArrayList<Note>

                    it.lines().forEach { line ->
                        println(line)
                        if (line.contains("<Cube currency")) {
                            val words: List<String> = line.split("'")
                            val note: Note = Note(name = words[1], description = words[3])

                            arrayList.add(note)
                        }
                    }

                    val newJson = gson.toJson(arrayList)
                    editor.putString(getString(R.string.notes_key), newJson)
                    editor.commit()
                    Handler(Looper.getMainLooper()).post {
                        updateList()
                    }
                }
            }
        }
    }
}