/*
*@author: Ogunleke Abiodun
* Main Activity backend
 */

package xyz.oleke.oleketv

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.SurfaceView
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mSurface: SurfaceView? = null

    private var account: Account? = null

    private var videoController: VideoController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        account = intent.getSerializableExtra("account") as? Account

        if(account!!.getUser().activeSubscription==null){
            showSubscription(account!!)
        }
        configureToolbar()
        loadChannels()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu)

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu)
    }


    // Configure the toolbar
    fun configureToolbar() {
        setContentView(R.layout.activity_main)
        val mainmenu: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(mainmenu)
        mainmenu.setNavigationIcon(R.drawable.ic_action_logo1)
        val actionBar = supportActionBar
        //actionBar!!.setDisplayShowTitleEnabled(false)
    }


    fun loadChannels() {

        var viewManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        val myDataset = ArrayList<Channel>(3)
        myDataset.add(
            Channel(
                "CNN",
                "http://portal.geniptv.com:8080/live/SBdhvpwoih/XkTGg0Yt3Z/1764.ts",
                "https://cdn.cnn.com/cnn/.e1mo/img/4.0/logos/CNN_logo_400x400.png"
            )
        )
        myDataset.add(
            Channel(
                "BBC",
                "http://portal.geniptv.com:8080/live/SBdhvpwoih/XkTGg0Yt3Z/2006.ts",
                "https://m.files.bbci.co.uk/modules/bbc-morph-news-waf-page-meta/2.3.0/bbc_news_logo.png"
            )
        )
        myDataset.add(
            Channel(
                "Aljazeera",
                "http://portal.geniptv.com:8080/live/SBdhvpwoih/XkTGg0Yt3Z/4612.ts",
                "https://yt3.ggpht.com/a-/AAuE7mDKm-m6CgM4tCg9NtXQvAYQWxBJcFR1FgxzmA=s900-mo-c-c0xffffffff-rj-k-no"
            )
        )
        var viewAdapter: RecyclerView.Adapter<*> =
            ChannelAdapter(myDataset) { channel: Channel -> channelClicked(channel) }

        var recyclerView: RecyclerView = findViewById<RecyclerView>(R.id.channels).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

    }


    /**
     * Creates MediaPlayer and plays video
     * @param media
     */
    fun createPlayer(media: String) {
        mSurface = videoView1
        videoController = VideoController(this)
        videoController!!.mSurface = mSurface
        videoController!!.createPlayer(media)

    }


    /*
    * Show Hidden View
     */
    fun show(view: View) {
        view.visibility = View.VISIBLE
    }

    /*
    * Handle the channel click event
     */
    fun channelClicked(channel: Channel): Boolean {
        if (videoController == null) {
            createPlayer(channel.getURL())
            show(videoView1)
        } else {
            videoController!!.changeChannel(channel.getURL())
        }

        return true
    }


    /*
    * Show the subscription Page
     */
    private fun showSubscription(account: Account){
        val main = Intent(this, Subscription::class.java)
        main.putExtra("account",account)
        startActivity(main)
    }

}
