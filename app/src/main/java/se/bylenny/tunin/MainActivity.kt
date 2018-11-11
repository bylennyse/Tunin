package se.bylenny.tunin

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.SearchView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import se.bylenny.tunin.list.Item
import se.bylenny.tunin.list.SpotifyListAdapter
import se.bylenny.tunin.log.Logger
import se.bylenny.tunin.log.lazyLogger
import se.bylenny.tunin.spotify.Spotify
import se.bylenny.tunin.spotify.models.SpotifyList
import se.bylenny.tunin.spotify.models.SpotifySession
import java.io.IOException
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), Searcher {

    enum class State {
        CONNECT,
        CONNECTING,
        SEARCH,
        LIST
    }

    private var state: State = State.CONNECT
        set(value) {
            log.debug("state: $field => $value")
            field = value
            when (value) {
                State.SEARCH -> {
                    search_bar.visibility = View.VISIBLE
                    connect_button.visibility = View.GONE
                    list.visibility = View.GONE
                }
                State.CONNECT -> {
                    search_bar.visibility = View.GONE
                    connect_button.visibility = View.VISIBLE
                    list.visibility = View.GONE
                }
                State.CONNECTING -> {
                    search_bar.visibility = View.GONE
                    connect_button.visibility = View.VISIBLE
                    list.visibility = View.GONE
                }
                State.LIST -> {
                    search_bar.visibility = View.VISIBLE
                    connect_button.visibility = View.GONE
                    list.visibility = View.VISIBLE
                }
            }
        }


    private val log: Logger by lazyLogger()
    private val disposables: CompositeDisposable = CompositeDisposable()
    private val spotify: Spotify by lazy { Spotify() }
    private val searchListener = SearchListener(this)
    private val query = PublishSubject.create<String>()

    private fun onSessionStarted(session: SpotifySession) {
        log.debug("onSessionStarted $session")
        state = State.SEARCH
    }

    override fun onSearch(text: String) {
        log.debug("onSearch $text ${query.hasObservers()}")
        query.onNext(text)
    }

    override fun onStartSearch() {
        log.debug("onStartSearch")
        state = State.SEARCH
    }

    override fun onStopSearch() {
        log.debug("onStopSearch")
        state = State.LIST
    }

    private val adapter = SpotifyListAdapter(R.layout.list_item)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        connect_button.setOnClickListener {
            log.debug("Connecting...")
            state = State.CONNECTING
            val intent = spotify.createAuthorizationIntent()
            startActivity(intent)
        }

        search_bar.apply {
            setOnQueryTextListener(searchListener)
            setOnQueryTextFocusChangeListener(searchListener)
            setOnCloseListener(searchListener)
        }

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter
    }

    private fun onUpdateSearch(query: String) {
        log.debug("onUpdateSearch $query")
        state = State.LIST
        spotify.api
            .search(spotify.authorization, query)
            .map { convertToList(it.body() ?: throw IOException("${it.errorBody()?.string()}")) }
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                log.debug("onUpdateSearch size:${it.size}")
                adapter.list = it
                log.debug(it.toString())
            }, log::error)
            .addTo(disposables)
    }

    private fun convertToList(input: SpotifyList): List<Item> {
        val albums: List<Item> = input.albums?.items?.map { convertToItem(it) } ?: emptyList()
        val artists: List<Item> = input.artists?.items?.map { convertToItem(it) } ?: emptyList()
        val playlists: List<Item> = input.playlists?.items?.map { convertToItem(it) } ?: emptyList()
        val tracks: List<Item> = input.tracks?.items?.map { convertToItem(it) } ?: emptyList()
        return albums.plus(artists).plus(playlists).plus(tracks)
    }

    private fun convertToItem(item: se.bylenny.tunin.spotify.models.Item): Item = Item(
        item.id,
        item.name,
        item.type,
        item.href ?: ""
    )

    override fun onNewIntent(intent: Intent?) {
        log.debug("onNewIntent ${intent?.data}")
        intent?.data?.let { uri ->
            if (!Spotify.isCallback(uri))
                null
            else
                spotify.handleCallback(uri)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this@MainActivity::onSessionStarted, log::error)
                    .addTo(disposables)
        } ?: super.onNewIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        query
            .filter { it.length > 2 }
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe(this::onUpdateSearch, log::error)
            .addTo(disposables)
        log.debug("query has Observers: ${query.hasObservers()}")
    }

    override fun onPause() {
        super.onPause()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}

interface Searcher {
    fun onSearch(text: String)
    fun onStartSearch()
    fun onStopSearch()
}

class SearchListener(
    private val searcher: Searcher
) :
    SearchView.OnQueryTextListener,
    View.OnFocusChangeListener,
    SearchView.OnCloseListener {

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (hasFocus)
            searcher.onStartSearch()
        else
            searcher.onStopSearch()
    }

    override fun onClose(): Boolean {
        searcher.onStopSearch()
        return false
    }

    override fun onQueryTextSubmit(text: String?): Boolean {
        text?.let { searcher.onSearch(it) }
        return false
    }

    override fun onQueryTextChange(text: String?): Boolean {
        text?.let { searcher.onSearch(it) }
        return false
    }
}

private fun Disposable.addTo(disposables: CompositeDisposable) {
    disposables.add(this)
}
