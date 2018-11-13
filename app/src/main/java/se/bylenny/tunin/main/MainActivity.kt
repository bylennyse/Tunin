package se.bylenny.tunin.main

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import se.bylenny.tunin.R
import se.bylenny.tunin.TuninApplication
import se.bylenny.tunin.list.ListView
import se.bylenny.tunin.list.SpotifyListAdapter
import se.bylenny.tunin.main.MainView.State.*
import javax.inject.Inject

interface MainView {
    fun startIntent(intent: Intent)

    val listView: ListView
    var state: State
    val query: String

    enum class State {
        CONNECT,
        CONNECTING,
        SEARCH,
        LIST
    }
}

class MainActivity : AppCompatActivity(), MainView, SearchView.OnQueryTextListener {

    override var state: MainView.State = CONNECT
        set(value) {
            field = value
            when (value) {
                SEARCH -> {
                    supportActionBar?.show()
                    connect_button.visibility = View.GONE
                    list.visibility = View.GONE
                }
                CONNECT -> {
                    supportActionBar?.hide()
                    connect_button.visibility = View.VISIBLE
                    list.visibility = View.GONE
                }
                CONNECTING -> {
                    supportActionBar?.hide()
                    connect_button.visibility = View.VISIBLE
                    list.visibility = View.GONE
                }
                LIST -> {
                    supportActionBar?.show()
                    connect_button.visibility = View.GONE
                    list.visibility = View.VISIBLE
                }
            }
        }

    override val listView: ListView
        get() = adapter

    override val query: String
        get() = search_bar.query.toString()

    @Inject lateinit var presenter: MainPresenter
    @Inject lateinit var adapter: SpotifyListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        TuninApplication.component.inject(this)

        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        connect_button.setOnClickListener {
            presenter.onConnect()
        }

        search_bar.setOnQueryTextListener(this)

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        presenter.onBind(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onUnbind()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable("layoutManager", list.layoutManager?.onSaveInstanceState())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        list.layoutManager?.onRestoreInstanceState(savedInstanceState?.getParcelable("layoutManager"))
    }

    override fun startIntent(intent: Intent) {
        startActivity(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        val uri = intent?.data

        if (uri == null || presenter.handleCallback(uri)) {
            super.onNewIntent(intent)
        }
    }

    override fun onQueryTextSubmit(text: String?): Boolean {
        text?.let { presenter.onSearch(it) }
        return false
    }

    override fun onQueryTextChange(text: String?): Boolean {
        //text?.let { presenter.onSearch(it) }
        return true
    }
}
