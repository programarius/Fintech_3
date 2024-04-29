package com.example.fintech_3

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class AddNoteActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var descriptionEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_note)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(findViewById(R.id.createNoteToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.create)

        nameEditText = findViewById(R.id.nameEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
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

    fun onCreateNoteClick(view: View) {
        if (nameEditText.text.toString().isEmpty() || descriptionEditText.text.toString().isEmpty()) {
            val toast = Toast.makeText(this, R.string.toast_empty_text, Toast.LENGTH_LONG) // in Activity
            toast.show()
            return
        }

        val pref = applicationContext.getSharedPreferences(getString(R.string.notes_key), Context.MODE_PRIVATE)
        val editor = pref.edit()
        val gson = Gson()

        val oldJson = pref.getString(getString(R.string.notes_key), "[]")
        val type: Type = object : TypeToken<List<Note>>() {}.type
        val arrayList: ArrayList<Note> = gson.fromJson(oldJson, type) as ArrayList<Note>
        arrayList.add(Note(name = nameEditText.text.toString(), description = descriptionEditText.text.toString()))

        val newJson = gson.toJson(arrayList)
        editor.putString(getString(R.string.notes_key), newJson)
        editor.commit()

        val toast = Toast.makeText(this, R.string.toast_create_success_text, Toast.LENGTH_LONG) // in Activity
        toast.show()
        finish()
    }
}