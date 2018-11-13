package se.bylenny.tunin.injection

import dagger.Component
import se.bylenny.tunin.main.MainActivity

@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(mainActivity: MainActivity)
}