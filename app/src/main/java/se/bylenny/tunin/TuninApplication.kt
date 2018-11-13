package se.bylenny.tunin

import android.app.Application
import se.bylenny.tunin.injection.AppComponent
import se.bylenny.tunin.injection.AppModule
import se.bylenny.tunin.injection.DaggerAppComponent
import se.bylenny.tunin.log.AndroidLogger

class TuninApplication: Application() {

    companion object {
        lateinit var component: AppComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder().appModule(AppModule(this)).build()
        AndroidLogger.Factory.init()
    }
}