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
import com.harlie.dogs.util.extractArrayFromJson
import com.harlie.dogs.util.readJsonAsset
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONArray
import java.io.InputStream


@Database(entities = [DogBreed::class], version = 1)
abstract class DogDatabase() : RoomDatabase() {

    abstract fun dogDao(): DogDao

    companion object {
        val _tag = "LEE: <" + DogDatabase::class.java.simpleName + ">"

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
                var rawJson: String? = readJsonAsset(context, "dog_data.json")
                Timber.tag(_tag).d("loadDatabaseDefaultData: read rawJson length=${rawJson?.length}")
                if (rawJson != null) {
                    val list: MutableList<DogBreed> = extractArrayFromJson(rawJson)
                    Timber.tag(_tag).d("loadDatabaseDefaultData: set PREPOPULATE_DATA with ${list.size} elements")
                    this.PREPOPULATE_DATA = list
                    saveDefaultDataToDatabase()
                }
            }
        }

        private fun saveDefaultDataToDatabase() {
            Timber.tag(_tag).d("saveDefaultDataToDatabase")
            if (INSTANCE != null && PREPOPULATE_DATA.isNotEmpty()) {
                GlobalScope.async {
                    val dao = INSTANCE!!.dogDao()
                    val result = dao.insertAll(*PREPOPULATE_DATA.toTypedArray())
                    Timber.tag(_tag).d("saveDefaultDataToDatabase: result.size=${result.size}")
                }
            }
        }

    }

}