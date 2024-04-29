package com.example.fintech_3

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class DeleteNoteActivity : AppCompatActivity() {
    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_delete_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById(R.id.deleteNoteToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.delete)

        spinner = findViewById(R.id.spinner)

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
            android.R.layout.simple_list_item_1, arrayOf("title"), intArrayOf(
                android.R.id.text1
            )
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.setAdapter(adapter)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onDeleteNoteClick(view: View) {
        val pref = applicationContext.getSharedPreferences(getString(R.string.notes_key), Context.MODE_PRIVATE)
        val editor = pref.edit()
        val gson = Gson()

        val oldJson = pref.getString(getString(R.string.notes_key), "[]")
        val type: Type = object : TypeToken<List<Note>>() {}.type
        val arrayList: ArrayList<Note> = gson.fromJson(oldJson, type) as ArrayList<Note>
        arrayList.removeAt(spinner.selectedItemPosition)

        val newJson = gson.toJson(arrayList)
        editor.putString(getString(R.string.notes_key), newJson)
        editor.commit()

        val toast = Toast.makeText(this, R.string.toast_delete_success_text, Toast.LENGTH_LONG) // in Activity
        toast.show()
        finish()
    }
}