package com.ideasapp.readcontacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.ContactsContract.Contacts
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        requestPermission()
        extractContacts()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CONTACTS),
            READ_CONTACTS_RC
        )
    }

    override fun onRequestPermissionsResult(
        requestCode:Int,
        permissions:Array<out String>,
        grantResults:IntArray,deviceId:Int
    ) {
        if(requestCode == READ_CONTACTS_RC && grantResults.isNotEmpty()) {
            val permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (permissionGranted) {
                extractContacts()
            }
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults,deviceId)
    }

    private fun extractContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "Permission not granted")
            return
        }

        Log.d("MainActivity", "Permission granted, starting extraction")
        thread {
            val cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME),
                null,
                null,
                null
            )

            if (cursor == null || !cursor.moveToFirst()) {
                Log.d("MainActivity", "No contacts found")
                cursor?.close()
            }

            while (cursor?.moveToNext() == true) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                Log.d("MainActivity", "ID: $id, Name: $name")
            }
            cursor?.close()
        }
    }

    companion object {
        const val READ_CONTACTS_RC = 100
    }
}