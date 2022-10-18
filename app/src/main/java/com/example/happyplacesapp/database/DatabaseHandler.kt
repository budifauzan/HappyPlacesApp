package com.example.happyplacesapp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.example.happyplacesapp.model.HappyPlaceModel

class DatabaseHandler(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "HappyPlacesDatabase"
        private const val TABLE_NAME = "HappyPlacesTable"
        private const val DATABASE_VERSION = 1

        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            ("CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT," + KEY_IMAGE + " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_DATE + " TEXT," + KEY_LOCATION + " TEXT," + KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addHappyPlace(happyPlaceModel: HappyPlaceModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlaceModel.title)
        contentValues.put(KEY_IMAGE, happyPlaceModel.image)
        contentValues.put(KEY_DESCRIPTION, happyPlaceModel.description)
        contentValues.put(KEY_DATE, happyPlaceModel.date)
        contentValues.put(KEY_LOCATION, happyPlaceModel.location)
        contentValues.put(KEY_LATITUDE, happyPlaceModel.latitude)
        contentValues.put(KEY_LONGITUDE, happyPlaceModel.longitude)

        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result
    }

    fun editHappyPlace(happyPlaceModel: HappyPlaceModel): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlaceModel.title)
        contentValues.put(KEY_IMAGE, happyPlaceModel.image)
        contentValues.put(KEY_DESCRIPTION, happyPlaceModel.description)
        contentValues.put(KEY_DATE, happyPlaceModel.date)
        contentValues.put(KEY_LOCATION, happyPlaceModel.location)
        contentValues.put(KEY_LATITUDE, happyPlaceModel.latitude)
        contentValues.put(KEY_LONGITUDE, happyPlaceModel.longitude)

        val result = db.update(TABLE_NAME, contentValues, KEY_ID + "=" + happyPlaceModel.id, null)
        db.close()
        return result
    }


    fun getHappyPlaces(): ArrayList<HappyPlaceModel> {
        val happyPlaceModels = ArrayList<HappyPlaceModel>()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.writableDatabase
        try {
            val cursor: Cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                do {
                    val happyPlaceModel = HappyPlaceModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE))
                    )
                    happyPlaceModels.add(happyPlaceModel)
                } while (cursor.moveToNext())
            }
            cursor.close()
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return happyPlaceModels
    }

}