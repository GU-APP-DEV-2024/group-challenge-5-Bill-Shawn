package com.zybooks.workingwithdata

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.PrintWriter

const val SAVED_DATA_KEY = "SAVED_DATA"

class MainActivity : AppCompatActivity() {
    lateinit var savedDataButton: Button
    lateinit var savedDataEditText: EditText
    lateinit var dataset: ArrayList<Pair<String, String>>
    lateinit var contactCustomAdapter: ContactCustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Handle edge-to-edge window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Shared Preferences setup
        val pref = getPreferences(Context.MODE_PRIVATE)
        savedDataEditText = findViewById(R.id.saved_data_editText)
        savedDataEditText.setText(pref.getString(SAVED_DATA_KEY, "No Saved Data"))

        savedDataButton = findViewById(R.id.saved_data_Button)
        savedDataButton.setOnClickListener {
            val editor = pref.edit()
            editor.putString(SAVED_DATA_KEY, savedDataEditText.text.toString())
            editor.apply()
        }

        // RecyclerView setup with ContactCustomAdapter
        dataset = loadFromFile()
        contactCustomAdapter = ContactCustomAdapter(dataset)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = contactCustomAdapter

        // Button to load contacts from file
        findViewById<Button>(R.id.loadContacts).setOnClickListener {
            dataset.clear()
            dataset.addAll(loadFromFile())
            contactCustomAdapter.notifyDataSetChanged()
        }

        // Button to save contacts to file
        findViewById<Button>(R.id.saveContacts).setOnClickListener {
            saveToFile()
        }

        // Button to add a new contact
        findViewById<Button>(R.id.addContact).setOnClickListener {
            dataset += Pair("New Person", "5555555555")
            contactCustomAdapter.notifyItemInserted(dataset.size - 1)
        }

        // Button to clear all contacts
        findViewById<Button>(R.id.clear).setOnClickListener {
            dataset.clear()
            contactCustomAdapter.notifyDataSetChanged()
        }

        // Button to navigate to DataViewActivity
        findViewById<Button>(R.id.btn_data_view).setOnClickListener {
            startActivity(Intent(this, DataViewActivity::class.java))
        }
    }

    // Save contacts to a JSON file
    private fun saveToFile() {
        val outputStream = openFileOutput("ContactList.txt", Context.MODE_PRIVATE)
        val writer = PrintWriter(outputStream)

        val json = JSONArray()
        for (pair in dataset) {
            val newJSONObject = JSONObject()
            newJSONObject.put("name", pair.first)
            newJSONObject.put("number", pair.second)
            json.put(newJSONObject)
        }
        writer.println(json.toString(3))
        writer.close()
    }

    // Load contacts from a JSON file
    private fun loadFromFile(): ArrayList<Pair<String, String>> {
        val ret = arrayListOf<Pair<String, String>>()
        try {
            val inputStream = openFileInput("ContactList.txt")
            val reader = inputStream.bufferedReader()
            val fileText = reader.readText()
            val jsonArray = JSONArray(fileText)
            for (i in 0 until jsonArray.length()) {
                val contact = jsonArray.getJSONObject(i)
                val name = contact.getString("name")
                val number = contact.getString("number")
                ret.add(Pair(name, number))
            }
        } catch (e: FileNotFoundException) {
            // Return empty dataset if file not found
        } catch (e: JSONException) {
            // Handle JSON parsing errors
        }
        return ret
    }
}
