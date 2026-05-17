package com.madhum.marga.data.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.madhum.marga.data.dao.HarvestLogDao
import com.madhum.marga.data.dao.HiveDao
import com.madhum.marga.data.dao.InspectionLogDao
import com.madhum.marga.data.dao.UserDao
import com.madhum.marga.data.model.*

/**
 * MadhuDatabase — Room DB singleton.
 * FR-09: Room DB integration — offline-capable hive-wise performance history.
 * All data persists 100% offline on device.
 */
@Database(
    entities = [User::class, Hive::class, InspectionLog::class, HarvestLog::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MadhuDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun hiveDao(): HiveDao
    abstract fun inspectionLogDao(): InspectionLogDao
    abstract fun harvestLogDao(): HarvestLogDao

    companion object {
        @Volatile
        private var INSTANCE: MadhuDatabase? = null

        fun getDatabase(context: Context): MadhuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MadhuDatabase::class.java,
                    "madhu_marga_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Type converters for Room — handles enums and other custom types.
 */
class Converters {
    @TypeConverter
    fun fromActivityLevel(value: ActivityLevel): String = value.name

    @TypeConverter
    fun toActivityLevel(value: String): ActivityLevel = ActivityLevel.valueOf(value)
}
