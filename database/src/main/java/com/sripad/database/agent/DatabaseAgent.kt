package com.sripad.database.agent

import com.sripad.database.dao.NotesDao
import com.sripad.database.tables.NoteEntity
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.joda.time.LocalDateTime
import timber.log.Timber

data class NoteInfo(
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

    fun updateNote(noteToUpdate: NoteInfo, title: String, note: String): Single<Int>

    fun deleteNote(noteToDelete: NoteInfo): Single<Int>

    fun toggleFavoriteStatus(noteToUpdate: NoteInfo, favoriteStatus: Boolean): Single<Int>

    fun toggleStarredStatus(noteToUpdate: NoteInfo, starredStatus: Boolean): Single<Int>

    fun retrieveStarredNotes(): Single<List<NoteInfo>>

    fun retrieveFavoriteNotes(): Single<List<NoteInfo>>

    fun retrieveNotes(): Flowable<List<NoteInfo>>

    fun retrieveNote(noteId: Long): Single<NoteInfo>
}

class DefaultDatabaseAgent internal constructor(private val notesDao: NotesDao) : DatabaseAgent {

    override fun insertNote(title: String, note: String): Single<Long> {
        val localDateTime = LocalDateTime.now()
        val noteEntity = NoteEntity(title = title, note = note, createdOn = localDateTime, modifiedOn = localDateTime)
        return Single.fromCallable { notesDao.insert(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[insertNote] Note inserted at $it") }
    }

    override fun updateNote(noteToUpdate: NoteInfo, title: String, note: String): Single<Int> {
        val modifiedOn = LocalDateTime.now()
        val noteEntity = noteToUpdate.toNoteEntity(title = title, note = note, modifiedOn = modifiedOn)
        return Single.fromCallable { notesDao.update(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[updateNote] Number of notes updated: $it") }
    }

    override fun deleteNote(noteToDelete: NoteInfo): Single<Int> {
        val noteEntity = noteToDelete.toNoteEntity()
        return Single.fromCallable { notesDao.delete(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[deleteNote] No of notes deleted: $it") }
    }

    override fun toggleFavoriteStatus(noteToUpdate: NoteInfo, favoriteStatus: Boolean): Single<Int> {
        val noteEntity = noteToUpdate.toNoteEntity(favorite = favoriteStatus)
        return Single.fromCallable { notesDao.update(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[toggleFavoriteStatus] No of notes updated: $it") }
    }

    override fun toggleStarredStatus(noteToUpdate: NoteInfo, starredStatus: Boolean): Single<Int> {
        val noteEntity = noteToUpdate.toNoteEntity(starred = starredStatus)
        return Single.fromCallable { notesDao.update(noteEntity) }
                .subscribeOn(Schedulers.io())
                .doOnSuccess { Timber.v("[toggleStarredStatus] No of notes updated: $it") }
    }

    override fun retrieveStarredNotes(): Single<List<NoteInfo>> {
        return notesDao.retrieveStarredNotes()
                .subscribeOn(Schedulers.io())
                .map { noteContainers -> noteContainers.map { it.toNoteInfo() } }
    }

    override fun retrieveFavoriteNotes(): Single<List<NoteInfo>> {
        return notesDao.retrieveFavoriteNotes()
                .subscribeOn(Schedulers.io())
                .map { noteContainers -> noteContainers.map { it.toNoteInfo() } }
    }

    override fun retrieveNotes(): Flowable<List<NoteInfo>> {
        return notesDao.retrieveNotes()
                .subscribeOn(Schedulers.io())
                .map { noteContainers -> noteContainers.map { it.toNoteInfo() } }
    }

    override fun retrieveNote(noteId: Long): Single<NoteInfo> {
        return notesDao.retrieveNote(noteId).subscribeOn(Schedulers.io()).map { it.toNoteInfo() }
    }

    private fun NoteInfo.toNoteEntity(
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

    private fun NoteEntity.toNoteInfo(): NoteInfo {
        return NoteInfo(
                id = this.id,
                title = this.title,
                note = this.note,
                favorite = this.favorite,
                starred = this.starred,
                createdOn = this.createdOn,
                modifiedOn = this.modifiedOn
        )
    }
}