package se.bylenny.tunin.main

import addTo
import android.net.Uri
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import se.bylenny.tunin.list.ListItem
import se.bylenny.tunin.list.ListView
import se.bylenny.tunin.log.Logger
import se.bylenny.tunin.log.lazyLogger
import se.bylenny.tunin.main.MainView.State.CONNECTING
import se.bylenny.tunin.main.MainView.State.SEARCH
import se.bylenny.tunin.spotify.Spotify
import se.bylenny.tunin.spotify.models.SpotifySession
import java.util.concurrent.TimeUnit

interface MainPresenter: ListView.ClickListener {
    fun onBind(view: MainView)
    fun onUnbind()

    fun onResume()
    fun onPause()

    fun onConnect()
    fun handleCallback(uri: Uri): Boolean
    fun onSearch(query: String): Any
}

class MainPresenterImpl(
    private val spotify: Spotify
): MainPresenter {

    private val log: Logger by lazyLogger()

    private var view: MainView? = null

    private val disposables: CompositeDisposable = CompositeDisposable()
    private val query = PublishSubject.create<String>()

    override fun onBind(view: MainView) {
        this.view = view

        if (spotify.hasSession) {
            view.state = SEARCH
        }
    }

    override fun onUnbind() {
        view = null
        disposables.dispose()
    }

    override fun onResume() {
        view?.listView?.listener = this
        query
            .filter { it.length > 2 }
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe(this::onUpdateSearch, log::error)
            .addTo(disposables)

        // Trigger fetch
        view?.query?.let { onUpdateSearch(it) }
    }

    override fun onPause() {
        view?.listView?.listener = null
        disposables.clear()
    }

    private fun onSessionStarted(session: SpotifySession) {
        log.debug("onSessionStarted $session")
        view?.state = SEARCH
    }

    override fun onSearch(text: String) {
        log.debug("onSearch $text ${query.hasObservers()}")
        query.onNext(text)
    }

    override fun onClicked(item: ListItem) {
        val intent = spotify.createOpenIntent(item)
        view?.startIntent(intent)
    }

    override fun onConnect() {
        log.debug("Connecting...")
        view?.apply {
            state = CONNECTING
            startIntent(spotify.createAuthorizationIntent())
        }
    }

    override fun handleCallback(uri: Uri): Boolean {
        return Spotify.isCallback(uri).apply {
            spotify.handleCallback(uri)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this@MainPresenterImpl::onSessionStarted, log::error)
                .addTo(disposables)
        }
    }

    private fun onUpdateSearch(query: String) {
        log.debug("onUpdateSearch $query")

        view?.apply {
            state = if (spotify.hasSession) {
                MainView.State.LIST
            } else {
                MainView.State.CONNECT
            }
        }

        spotify.search(query)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                log.debug("onUpdateSearch size:${it.size}")
                view?.listView?.list = it
                log.debug(it.toString())
            }, log::error)
            .addTo(disposables)
    }
}