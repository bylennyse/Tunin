package se.bylenny.tunin

import android.app.Application
import se.bylenny.tunin.log.AndroidLogger

class TuninApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidLogger.Factory.init()
    }
}