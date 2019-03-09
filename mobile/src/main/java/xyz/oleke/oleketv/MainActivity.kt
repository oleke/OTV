/*
*@author: Ogunleke Abiodun
* Main Activity backend
 */

package xyz.oleke.oleketv

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.MediaController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val api = API()

    private var mSurface: SurfaceView? = null

    private var account: Account? = null

    private var videoController: VideoController? = null

    private var mediaController: MediaController? =null

    private var channelList: List<Model.Channel>? = null

    var prefs: Prefs? = null

    var currentChannel: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        account = intent.getSerializableExtra("account") as? Account

        if(account!!.getUser().activeSubscription==null){
            showSubscription(account!!)
        }

        checkOtherDevice()

        prefs = Prefs(this)
        prefs!!.userID = account!!.getUser().id



        configureToolbar()
        getChannels()

        search_input_text.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(count >=3){
                    doSearch()
                }
                else if(count==0){
                    if(channelList!=null){
                        loadChannels(channelList!!)
                    }

                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })


       execute_search_button.setOnClickListener {
            doSearch()
        }

        close_search_button.setOnClickListener {
            search_view.closeSearch()
            loadChannels(channelList!!)
        }

    }

    private fun checkOtherDevice() {
        val device = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        if(account!!.getUser().device!=device){
            // Initialize a new instance of
            val builder = AlertDialog.Builder(this@MainActivity)

            // Set the alert dialog title
            builder.setTitle("Login Activity")

            // Display a message on alert dialog
            builder.setMessage("You are already logged in on another device.")

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("OK"){dialog, which ->
                //showSplash()
            }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            //dialog.show()

        }
    }


    fun doSearch(){
        val term = search_input_text.text
        var entry:MutableList<Model.Channel> = mutableListOf<Model.Channel>()
        channelList!!.iterator().forEach {
            if(it.name.contains(term,ignoreCase = true))
                entry.add(it)
        }
        loadChannels(entry.toList())
    }

    fun findIndex(term:String):Int{
        var index:Int = 0
        for (i in channelList!!.indices){
            if(channelList!![i].name.startsWith(term)){
                index = i
                break
            }
        }
        return index
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Log.d("Config",newConfig!!.orientation.toString())
        when {
            newConfig!!.orientation==Configuration.ORIENTATION_PORTRAIT -> {
                channels.visibility = View.VISIBLE
                my_toolbar.visibility = View.VISIBLE
                supportActionBar!!.show()
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                //videoController!!.resizeScreen(videoView1.width-1,videoView1.height-1)
            }
            newConfig!!.orientation==Configuration.ORIENTATION_LANDSCAPE ->{
                channels.visibility = View.GONE
                my_toolbar.visibility = View.GONE
                supportActionBar!!.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
                videoController!!.resizeScreen(videoView1.width-1,videoView1.height-1)
            }
            else -> {

            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu)

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu)
    }


    // Configure the toolbar
    private fun configureToolbar() {
        setContentView(R.layout.activity_main)
        val mainmenu: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(mainmenu)
        mainmenu.setNavigationIcon(R.drawable.ic_action_logo1)
        val actionBar = supportActionBar
        actionBar!!.setDisplayShowTitleEnabled(false)
    }


    fun loadChannels(channels: List<Model.Channel>) {
        if(channelList===null) {
            channelList = channels
        }
        var viewManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        var indexManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        var viewAdapter: RecyclerView.Adapter<*> =
            ChannelAdapter(channels) { channel: Model.Channel,position:Int -> channelClicked(channel,position) }

        findViewById<RecyclerView>(R.id.channels).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        side_bar.apply {
            setHasFixedSize(true)

            layoutManager = indexManager
            val alphas = arrayListOf("A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z")
            adapter = IndexAdapter(alphas){listView:String -> indexClicked(listView)}
        }

    }

    private fun indexClicked(listView: String): Boolean {
        val index = findIndex(listView)
        channels.layoutManager!!.offsetChildrenVertical(index)
        channels.layoutManager!!.scrollToPosition(index)
        return true
    }

    fun getChannels() {
        val request = api.getChannels()
        GlobalScope.launch(Dispatchers.Main) {
            val plans = request.await()
            this@MainActivity.loadChannels(plans)
        }
    }


    /**
     * Creates MediaPlayer and plays video
     * @param media
     */
    fun createPlayer(media: String) {
        //supportActionBar!!.hide()
        mSurface = videoView1
        videoController = VideoController(this)
        videoController!!.mSurface = mSurface
        videoController!!.createPlayer(media)
        //mediaController = MediaController(this,false)
        videoController!!.setAnchorView(videoController!!.mSurface)
        //mediaController!!.setAnchorView(videoController!!.mSurface)
        videoController!!.setMediaPlayer(videoController)
        videoController!!.contentDescription = channelList!![currentChannel].name!!
        videoController!!.setPrevNextListeners({next->
            if((currentChannel+1)<channelList!!.size){
                channelClicked(channelList!![currentChannel+1],currentChannel+1)
            }

        }, {    prev->
                if((currentChannel-1)>=0){
                    channelClicked(channelList!![currentChannel-1],currentChannel-1)
                }
        })

        videoController!!.keepScreenOn = true

        mSurface!!.setOnClickListener {
            //videoController!!.show(3000)
        }

        mSurface!!.setOnTouchListener{
            v: View, m: MotionEvent ->
            videoController!!.show(3000)
            true
        }
        //supportActionBar!!.hide()
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
    private fun channelClicked(channel: Model.Channel,position:Int): Boolean {
        currentChannel = position
        if (videoController == null) {
            createPlayer("http://"+account!!.getUser().activeSubscription!!.service_provider!!.url+"/"+channel.service_id+".ts")
            show(videoView1)
        } else {
            videoController!!.changeChannel("http://"+account!!.getUser().activeSubscription!!.service_provider!!.url+"/"+channel.service_id+".ts")
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


    /*
    * Show the Splash Screen
     */
    private fun showSplash(){
        val main = Intent(this, SplashScreen::class.java)
        startActivity(main)
        finish()
    }

}
