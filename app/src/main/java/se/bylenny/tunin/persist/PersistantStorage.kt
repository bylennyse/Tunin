package se.bylenny.tunin.persist

import android.content.SharedPreferences
import com.squareup.moshi.JsonAdapter

interface PersistantStorage<T> {
    fun store(data: T)
    fun restore(): T?
    fun clear()
}

class SharedPreferencesPersistantStorage<T>(
    private val adapter: JsonAdapter<T>,
    private val type: Class<T>,
    private val sharedPreferences: SharedPreferences
): PersistantStorage<T> {
    override fun store(data: T) {
        sharedPreferences.edit().putString(type.name, adapter.toJson(data)).apply()
    }

    override fun restore(): T? {
        return sharedPreferences.getString(type.name, null)?.let {
            adapter.fromJson(it)
        }
    }

    override fun clear() {
        sharedPreferences.edit().remove(type.name).apply()
    }

}