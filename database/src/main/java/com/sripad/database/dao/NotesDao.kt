package com.sripad.database.dao

import android.arch.persistence.room.*
import com.sripad.database.tables.NoteEntity
import io.reactivex.Flowable

@Dao
internal interface NotesDao {

    @Insert
    fun insert(noteEntity: NoteEntity): Long

    @Update
    fun update(noteEntity: NoteEntity): Int

    @Delete
    fun delete(noteEntity: NoteEntity): Int

    @Query("SELECT * FROM Notes WHERE favorite = 1")
    fun retrieveFavoriteNotes(): Flowable<List<NoteEntity>>

    @Query("SELECT * FROM Notes WHERE starred = 1")
    fun retrieveStarredNotes(): Flowable<List<NoteEntity>>

    @Query("SELECT * FROM Notes ORDER BY modifiedOn DESC")
    fun retrieveNotes(): Flowable<List<NoteEntity>>
}