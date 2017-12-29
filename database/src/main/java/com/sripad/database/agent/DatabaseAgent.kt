package com.sripad.database.agent

import com.sripad.database.dao.NotesDao
import com.sripad.database.tables.NoteEntity
import io.reactivex.Completable
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

    fun insertNote(title: String, note: String): Completable

    fun updateNote(noteToUpdate: NoteContainer, title: String, note: String): Completable

    fun deleteNote(noteToDelete: NoteContainer): Completable

    fun toggleFavoriteStatus(noteToUpdate: NoteContainer, favoriteStatus: Boolean): Completable

    fun toggleStarredStatus(noteToUpdate: NoteContainer, starredStatus: Boolean): Completable

    fun retrieveStarredNotes(): Flowable<List<NoteContainer>>

    fun retrieveFavoriteNotes(): Flowable<List<NoteContainer>>
}

class DefaultDatabaseAgent internal constructor(private val notesDao: NotesDao) : DatabaseAgent {

    override fun insertNote(title: String, note: String): Completable {
        val localDateTime = LocalDateTime.now()
        val noteEntity = NoteEntity(title = title, note = note, createdOn = localDateTime, modifiedOn = localDateTime)
        return Single.fromCallable { notesDao.insert(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[insertNote] Note inserted at $it") }
                .toCompletable()
    }

    override fun updateNote(noteToUpdate: NoteContainer, title: String, note: String): Completable {
        val modifiedOn = LocalDateTime.now()
        val noteEntity = noteToUpdate.toNoteEntity(title = title, note = note, modifiedOn = modifiedOn)
        return Single.fromCallable { notesDao.update(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[updateNote] Number of notes updated: $it") }
                .toCompletable()
    }

    override fun deleteNote(noteToDelete: NoteContainer): Completable {
        val noteEntity = noteToDelete.toNoteEntity()
        return Single.fromCallable { notesDao.delete(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[deleteNote] No of notes deleted: $it") }
                .toCompletable()
    }

    override fun toggleFavoriteStatus(noteToUpdate: NoteContainer, favoriteStatus: Boolean): Completable {
        val noteEntity = noteToUpdate.toNoteEntity(favorite = favoriteStatus)
        return Single.fromCallable { notesDao.update(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[toggleFavoriteStatus] No of notes updated: $it") }
                .toCompletable()
    }

    override fun toggleStarredStatus(noteToUpdate: NoteContainer, starredStatus: Boolean): Completable {
        val noteEntity = noteToUpdate.toNoteEntity(starred = starredStatus)
        return Single.fromCallable { notesDao.update(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[toggleStarredStatus] No of notes updated: $it") }
                .toCompletable()
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