package com.quoteday.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE quotes ADD COLUMN author TEXT NOT NULL DEFAULT ''")
    }
}

@Database(entities = [Quote::class], version = 2, exportSchema = false)
abstract class QuoteDatabase : RoomDatabase() {
    abstract fun quoteDao(): QuoteDao

    companion object {
        @Volatile
        private var INSTANCE: QuoteDatabase? = null

        fun getDatabase(context: Context): QuoteDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    QuoteDatabase::class.java,
                    "quote_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed a few starter quotes
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val dao = database.quoteDao()
                                    dao.insert(Quote(text = "The only way to do great work is to love what you do.", author = "Steve Jobs"))
                                    dao.insert(Quote(text = "In the middle of every difficulty lies opportunity.", author = "Albert Einstein"))
                                    dao.insert(Quote(text = "It does not matter how slowly you go as long as you do not stop.", author = "Confucius"))
                                    dao.insert(Quote(text = "Life is what happens when you're busy making other plans.", author = "John Lennon"))
                                    dao.insert(Quote(text = "The future belongs to those who believe in the beauty of their dreams.", author = "Eleanor Roosevelt"))
                                }
                            }
                        }
                    })
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
