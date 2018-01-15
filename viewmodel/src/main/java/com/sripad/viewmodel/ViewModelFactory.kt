package com.sripad.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor(
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