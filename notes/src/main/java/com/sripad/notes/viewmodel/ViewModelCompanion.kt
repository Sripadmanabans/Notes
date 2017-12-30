package com.sripad.notes.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import dagger.MapKey
import io.reactivex.functions.Consumer
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@MustBeDocumented
@Retention
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Singleton
internal class ViewModelFactory @Inject constructor(
        private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val provider = creators[modelClass]

        val providerToUse = if (provider != null) {
            provider
        } else {
            val key = creators.keys.firstOrNull { modelClass.isAssignableFrom(it) }
            creators[key]
        }

        providerToUse ?: throw IllegalArgumentException("unknown model class $modelClass")

        return try {
            providerToUse.get() as T
        } catch (exception: Exception) {
            throw RuntimeException(exception)
        }
    }
}

/**
 * To map a rx stream into live data.
 */
class ConsumerLiveData<T> : LiveData<T>(), Consumer<T> {

    override fun accept(t: T) = postValue(t)
}

internal inline fun <reified T : ViewModel> ViewModelProvider.Factory.getViewModel(activity: AppCompatActivity): T {
    return ViewModelProviders.of(activity, this).get(T::class.java)
}