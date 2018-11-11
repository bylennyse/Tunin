package se.bylenny.tunin.log

import android.util.Log

class AndroidLogger(private val name: String) : Logger {

    override fun debug(message: String) {
        Log.d(name, message)
    }

    override fun error(throwable: Throwable) {
        Log.e(name, "", throwable)
    }

    override fun info(message: String) {
        Log.i(name, message)
    }

    override fun warn(message: String) {
        Log.w(name, message)
    }

    object Factory: LoggerFactory {

        fun init() {
            LoggerFactory.instance = this
        }

        override fun create(name: String): Logger {
            return AndroidLogger(name)
        }
    }
}
