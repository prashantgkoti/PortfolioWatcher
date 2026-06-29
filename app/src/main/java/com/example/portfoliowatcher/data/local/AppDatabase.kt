package com.example.portfoliowatcher.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.portfoliowatcher.data.local.dao.HoldingDao
import com.example.portfoliowatcher.data.local.dao.PortfolioDao
import com.example.portfoliowatcher.data.local.dao.UserDao
import com.example.portfoliowatcher.data.local.entity.HoldingEntity
import com.example.portfoliowatcher.data.local.entity.PortfolioEntity
import com.example.portfoliowatcher.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        PortfolioEntity::class,
        HoldingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateTimeConverter::class, BigDecimalConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun portfolioDao(): PortfolioDao
    abstract fun holdingDao(): HoldingDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "portfolio_watcher_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
            }
        }
    }
}
