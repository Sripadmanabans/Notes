package com.sripad.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import com.sripad.database.dao.NotesDao
import com.sripad.database.tables.NoteEntity
import org.joda.time.DateTimeZone
import org.joda.time.LocalDateTime

@Database(entities = [NoteEntity::class], version = 1)
@TypeConverters(value = [LocalDateTimeConverter::class])
internal abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao
}

internal object LocalDateTimeConverter {

    @JvmStatic
    @TypeConverter
    fun longToLocalDateTime(time: Long) = LocalDateTime(time, DateTimeZone.getDefault())

    @JvmStatic
    @TypeConverter
    fun localDateTimeToLong(dateTime: LocalDateTime) = dateTime.toDateTime(DateTimeZone.getDefault()).millis
}