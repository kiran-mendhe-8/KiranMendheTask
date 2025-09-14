package com.mk.kiranmendhetask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HoldingsDao {
    
    @Query("SELECT * FROM holdings")
    fun getAllHoldings(): Flow<List<HoldingEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoldings(holdings: List<HoldingEntity>)
    
    @Query("DELETE FROM holdings")
    suspend fun deleteAllHoldings()
}