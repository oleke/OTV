package xyz.oleke.oleketv

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import org.videolan.libvlc.IVLCVout
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import java.util.*
import android.view.Gravity
import android.R.attr.gravity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.widget.FrameLayout
import android.widget.ImageButton




class VideoController(activity: Activity) : IVLCVout.Callback, MediaPlayer.EventListener,
    MediaController(activity,false), MediaController.MediaPlayerControl {
    var screenMode = 0
    var fullScreen:ImageButton? = null
    override fun setAnchorView(view: View?) {
        super.setAnchorView(view)

        fullScreen = ImageButton(super.getContext())

        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        fullScreen!!.setBackgroundColor(Color.TRANSPARENT)
        params.gravity = Gravity.RIGHT
        params.rightMargin = 8
        params.topMargin = 16
        addView(fullScreen, params)
        fullScreen!!.setImageResource(R.drawable.ic_action_fullscreen)


        fullScreen!!.setOnClickListener {
            toggleFullScreen()
        }
    }

    fun toggleFullScreen(){
        Log.d(TAG, "toggleFullScreen");
        mediaPlayer!!.pause()

        if(screenMode==0){
            fullScreen!!.setImageResource(R.drawable.ic_action_fullscreen_exit)
            activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            screenMode = 1

        }else{
            fullScreen!!.setImageResource(R.drawable.ic_action_fullscreen_exit)
            activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            screenMode= 0
        }
        mediaPlayer!!.play()
    }


    override fun isPlaying(): Boolean {
        return mediaPlayer!!.isPlaying
    }

    override fun canSeekForward(): Boolean {
        return false
    }

    override fun getDuration(): Int {
        return mediaPlayer!!.media.duration.toInt()
    }

    override fun pause() {
        mediaPlayer!!.pause()
    }

    override fun getBufferPercentage(): Int {
        return buffering

    }

    override fun seekTo(pos: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer!!.position.toInt()
    }

    override fun canSeekBackward(): Boolean {
        return false
    }

    override fun start() {
        return mediaPlayer!!.play()
    }

    override fun getAudioSessionId(): Int {
        return mediaPlayer!!.audioTrack
    }

    override fun canPause(): Boolean {
        return true
    }


    // create TAG for logging
    companion object {
        private var TAG = "VideoController"
    }

    // declare media player object
    private var mediaPlayer: MediaPlayer? = null
    // declare surface view object
    var mSurface: SurfaceView? = null
    // declare surface holder object
    var holder: SurfaceHolder? = null

    // declare libvlc object
    private var libvlc: LibVLC? = null

    // declare/initialize activity
    private var activity: Activity? = null

    private var buffering: Int = 0

    init {
        this.activity = activity
    }


    /**
     * Creates MediaPlayer and plays video

     * @param media
     */
    fun createPlayer(media: String) {
        if (mediaPlayer != null && libvlc != null) {
            releasePlayer()
        }
        Log.i(TAG, "Creating vlc player")
        try {
            // create arraylist to assign option to create libvlc object
            val options = ArrayList<String>()
            options.add("--aout=opensles")
            options.add("--http-reconnect")
            options.add("--audio-time-stretch") // time stretching
            options.add("--network-caching=1500")
            options.add("-vvv") // verbosity

            // create libvlc object
            libvlc = LibVLC(activity, options)

            // get surface view holder to display video
            this.holder = mSurface!!.holder
            holder!!.setKeepScreenOn(true)

            // Creating media player
            mediaPlayer = MediaPlayer(libvlc)

            // Setting up video output
            val vout = mediaPlayer!!.vlcVout
            vout.setVideoView(mSurface)
            vout.addCallback(this)
            vout.attachViews()
            //val m = Media(libvlc, Uri.parse(media))
            val m = setMedia(media)
            mediaPlayer!!.media = m
            mediaPlayer!!.play()

        } catch (e: Exception) {
            Toast.makeText(
                activity, "Error in creating player!", Toast
                    .LENGTH_LONG
            ).show()
        }

    }

    /*
   * release player
   * */
    fun releasePlayer() {
        Log.i(TAG, "releasing player started")
        if (libvlc == null)
            return
        mediaPlayer!!.stop()
        var vout: IVLCVout = mediaPlayer!!.vlcVout
        vout.removeCallback(this)
        vout.detachViews()
        mediaPlayer!!.release()
        mediaPlayer = null
        holder = null
        libvlc!!.release()
        libvlc = null

        Log.i(TAG, "released player")
    }

    /**
     * Set new media url
     */
    fun setMedia(mediaURI: String): Media {
        return Media(libvlc, Uri.parse(mediaURI))
    }

    /**
     * Change Current Media
     */
    fun changeChannel(channelURL: String) {
        try {
            mediaPlayer!!.stop()
            mediaPlayer!!.media = setMedia(channelURL)
            mediaPlayer!!.play()
        } catch (e: Exception) {
            Toast.makeText(
                activity, "Error changing channel!", Toast
                    .LENGTH_LONG
            ).show()
        }
    }

    override fun onEvent(event: MediaPlayer.Event) {

        when (event.type) {
            MediaPlayer.Event.EndReached -> {
                this.releasePlayer()
            }

            MediaPlayer.Event.Buffering ->{
                buffering = event.buffering.toInt()
            }

            MediaPlayer.Event.Playing -> Log.i("playing", "playing")
            MediaPlayer.Event.Paused -> Log.i("paused", "paused")
            MediaPlayer.Event.Stopped -> Log.i("stopped", "stopped")
            else -> Log.i("nothing", "nothing")
        }
    }

    override fun onSurfacesCreated(vlcVout: IVLCVout?) {
        val sw = mSurface!!.width
        val sh = mSurface!!.height

        if (sw * sh == 0) {
            Log.e(TAG, "Invalid surface size")
            return
        }

        mediaPlayer!!.vlcVout.setWindowSize(sw, sh)
        mediaPlayer!!.aspectRatio = "4:3"
        mediaPlayer!!.scale = 0f
    }

    fun resizeScreen(sw:Int, sh:Int){
        this.holder!!.setFixedSize(sw,sh)
    }

    override fun onSurfacesDestroyed(vlcVout: IVLCVout?) {
        releasePlayer()
    }


    //Get the initialized media player{
    fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer!!
    }

}