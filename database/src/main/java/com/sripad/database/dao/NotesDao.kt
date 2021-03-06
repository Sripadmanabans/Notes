package com.sripad.database.dao

import android.arch.persistence.room.*
import com.sripad.database.tables.NoteEntity
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
internal interface NotesDao {

    @Insert
    fun insert(noteEntity: NoteEntity): Long

    @Update
    fun update(noteEntity: NoteEntity): Int

    @Delete
    fun delete(noteEntity: NoteEntity): Int

    @Query("SELECT * FROM Notes WHERE favorite = 1 ORDER BY modifiedOn DESC")
    fun retrieveFavoriteNotes(): Single<List<NoteEntity>>

    @Query("SELECT * FROM Notes WHERE starred = 1 ORDER BY modifiedOn DESC")
    fun retrieveStarredNotes(): Single<List<NoteEntity>>

    @Query("SELECT * FROM Notes ORDER BY modifiedOn DESC")
    fun retrieveNotes(): Flowable<List<NoteEntity>>

    @Query("SELECT * FROM Notes WHERE id = :noteId")
    fun retrieveNote(noteId: Long): Single<NoteEntity>
}