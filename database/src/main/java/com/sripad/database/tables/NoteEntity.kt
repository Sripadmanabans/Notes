package com.sripad.database.tables

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.joda.time.LocalDateTime

@Entity(tableName = "Notes")
internal data class NoteEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val title: String,
        val note: String,
        val favorite: Boolean = false,
        val starred: Boolean = false,
        val createdOn: LocalDateTime,
        val modifiedOn: LocalDateTime
)