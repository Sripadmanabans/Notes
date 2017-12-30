package com.sripad.database.agent

import com.sripad.database.dao.NotesDao
import com.sripad.database.tables.NoteEntity
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.joda.time.LocalDateTime
import timber.log.Timber

data class NoteContainer(
        val id: Long,
        val title: String,
        val note: String,
        val favorite: Boolean,
        val starred: Boolean,
        val createdOn: LocalDateTime,
        val modifiedOn: LocalDateTime
)

interface DatabaseAgent {

    fun insertNote(title: String, note: String): Single<Long>

    fun updateNote(noteToUpdate: NoteContainer, title: String, note: String): Single<Int>

    fun deleteNote(noteToDelete: NoteContainer): Single<Int>

    fun toggleFavoriteStatus(noteToUpdate: NoteContainer, favoriteStatus: Boolean): Single<Int>

    fun toggleStarredStatus(noteToUpdate: NoteContainer, starredStatus: Boolean): Single<Int>

    fun retrieveStarredNotes(): Flowable<List<NoteContainer>>

    fun retrieveFavoriteNotes(): Flowable<List<NoteContainer>>
}

class DefaultDatabaseAgent internal constructor(private val notesDao: NotesDao) : DatabaseAgent {

    override fun insertNote(title: String, note: String): Single<Long> {
        val localDateTime = LocalDateTime.now()
        val noteEntity = NoteEntity(title = title, note = note, createdOn = localDateTime, modifiedOn = localDateTime)
        return Single.fromCallable { notesDao.insert(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[insertNote] Note inserted at $it") }
    }

    override fun updateNote(noteToUpdate: NoteContainer, title: String, note: String): Single<Int> {
        val modifiedOn = LocalDateTime.now()
        val noteEntity = noteToUpdate.toNoteEntity(title = title, note = note, modifiedOn = modifiedOn)
        return Single.fromCallable { notesDao.update(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[updateNote] Number of notes updated: $it") }
    }

    override fun deleteNote(noteToDelete: NoteContainer): Single<Int> {
        val noteEntity = noteToDelete.toNoteEntity()
        return Single.fromCallable { notesDao.delete(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[deleteNote] No of notes deleted: $it") }
    }

    override fun toggleFavoriteStatus(noteToUpdate: NoteContainer, favoriteStatus: Boolean): Single<Int> {
        val noteEntity = noteToUpdate.toNoteEntity(favorite = favoriteStatus)
        return Single.fromCallable { notesDao.update(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[toggleFavoriteStatus] No of notes updated: $it") }
    }

    override fun toggleStarredStatus(noteToUpdate: NoteContainer, starredStatus: Boolean): Single<Int> {
        val noteEntity = noteToUpdate.toNoteEntity(starred = starredStatus)
        return Single.fromCallable { notesDao.update(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[toggleStarredStatus] No of notes updated: $it") }
    }

    override fun retrieveStarredNotes(): Flowable<List<NoteContainer>> {
        return notesDao.retrieveStarredNotes().subscribeOn(Schedulers.io()).map { it.toNoteContainers() }
    }

    override fun retrieveFavoriteNotes(): Flowable<List<NoteContainer>> {
        return notesDao.retrieveFavoriteNotes().subscribeOn(Schedulers.io()).map { it.toNoteContainers() }
    }

    private fun NoteContainer.toNoteEntity(
            id: Long? = null,
            title: String? = null,
            note: String? = null,
            favorite: Boolean? = null,
            starred: Boolean? = null,
            createdOn: LocalDateTime? = null,
            modifiedOn: LocalDateTime? = null
    ): NoteEntity {
        return NoteEntity(
                id = id ?: this.id,
                title = title ?: this.title,
                note = note ?: this.note,
                favorite = favorite ?: this.favorite,
                starred = starred ?: this.starred,
                createdOn = createdOn ?: this.createdOn,
                modifiedOn = modifiedOn ?: this.modifiedOn
        )
    }

    private fun List<NoteEntity>.toNoteContainers(): List<NoteContainer> {
        return this.map {
            NoteContainer(
                    id = it.id,
                    title = it.title,
                    note = it.note,
                    favorite = it.favorite,
                    starred = it.starred,
                    createdOn = it.createdOn,
                    modifiedOn = it.modifiedOn
            )
        }
    }
}