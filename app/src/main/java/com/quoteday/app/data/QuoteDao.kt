package com.quoteday.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes ORDER BY id ASC")
    fun getAll(): Flow<List<Quote>>

    @Query("SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandom(): Quote?

    @Insert
    suspend fun insert(quote: Quote): Long

    @Query("SELECT COUNT(*) FROM quotes")
    suspend fun getCount(): Int

    @Update
    suspend fun update(quote: Quote)

    @Query("DELETE FROM quotes WHERE id = :id")
    suspend fun deleteById(id: Int)
}
