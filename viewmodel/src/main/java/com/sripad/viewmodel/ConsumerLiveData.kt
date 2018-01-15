package com.sripad.viewmodel

import android.arch.lifecycle.LiveData
import io.reactivex.functions.Consumer

/**
 * To map a rx stream into live data.
 */
class ConsumerLiveData<T> : LiveData<T>(), Consumer<T> {

    override fun accept(t: T) = postValue(t)
}