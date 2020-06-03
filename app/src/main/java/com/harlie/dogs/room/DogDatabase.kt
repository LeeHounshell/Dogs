package com.harlie.dogs.room

import android.content.Context
import android.os.Build
import androidx.annotation.NonNull
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import com.harlie.dogs.MyApplication
import com.harlie.dogs.model.DogBreed
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONArray
import java.io.InputStream


@Database(entities = [DogBreed::class], version = 1)
abstract class DogDatabase() : RoomDatabase() {

    abstract fun dogDao(): DogDao

    companion object {
        private val _tag = "LEE: <" + DogDatabase::class.java.simpleName + ">"

        @Volatile
        private var INSTANCE: DogDatabase? = null
        private const val LOCK = "lock"
        private lateinit var PREPOPULATE_DATA: List<DogBreed>

        private val roomCallBack: Callback = object : Callback() {
            override fun onCreate(@NonNull db: SupportSQLiteDatabase) {
                Timber.tag(_tag).d("Callback: onCreate")
                super.onCreate(db)
                GlobalScope.async {
                    loadDatabaseDefaultData(MyApplication.applicationContext())
                }
            }
        }

        fun getInstance(context: Context): DogDatabase? {
            Timber.tag(_tag).d("getInstance")
            INSTANCE ?: synchronized(LOCK) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it.build()
                }
            }
            return INSTANCE
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context,
                DogDatabase::class.java,
                "dog_database.db"
            )
                // prepopulate the database after onCreate was called
                .addCallback(roomCallBack)
/*
                // Future database migrations
                .addMigrations(
                    Migration_1_2,
                    Migration_2_3
                )
*/

        private fun loadDatabaseDefaultData(context: Context) {
            Timber.tag(_tag).d("loadDatabaseDefaultData")
            this.PREPOPULATE_DATA = emptyList()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                var rawJson: String? = null
                try {
                    val  inputStream: InputStream = context.assets.open("dog_data.json")
                    rawJson = inputStream.bufferedReader().use{it.readText()}
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
                Timber.tag(_tag).d("loadDatabaseDefaultData: read rawJson length=${rawJson?.length}")
                if (rawJson != null) {
                    val array = JSONArray(rawJson)
                    Timber.tag(_tag).d("loadDatabaseDefaultData: processing length=${array.length()}")
                    val list: MutableList<DogBreed> = mutableListOf()
                    if (array.length() > 0) {
                        val gson = Gson()
                        var i = 0
                        while (i < array.length()) {
                            list.add(
                                gson.fromJson(
                                    array.getJSONObject(i).toString(),
                                    DogBreed::class.java
                                )
                            )
                            i++
                        }
                    }
                    Timber.tag(_tag).d("loadDatabaseDefaultData: set PREPOPULATE_DATA with ${list.size} elements")
                    this.PREPOPULATE_DATA = list
                    if (INSTANCE != null && PREPOPULATE_DATA.isNotEmpty()) {
                        GlobalScope.async {
                            val dao = INSTANCE!!.dogDao()
                            val result = dao.insertAll(*PREPOPULATE_DATA.toTypedArray())
                            Timber.tag(_tag).d("loadDatabaseDefaultData: result.size=${result.size}")
                        }
                    }
                }
            }
        }

    }

}