package mutnemom.android.kotlindemo.playback

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import mutnemom.android.kotlindemo.databinding.ActivityExoPlayerBinding

class ExoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExoPlayerBinding

    private var mediaUrl = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
//    private var mediaUrl = "http://demo.unified-streaming.com/video/tears-of-steel/tears-of-steel.ism/.m3u8"
    private var player: ExoPlayer? = null

    private var playbackPosition: Long = 0L
    private var currentWindow: Int = 0
    private var playWhenReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        initPlayback()
    }

    override fun onPause() {
        super.onPause()
        releasePlayback()
    }

    private fun initPlayback() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(this),
                DefaultTrackSelector(),
                DefaultLoadControl()
            )

            binding.exoPlayback.player = this.player
            player?.playWhenReady = playWhenReady
            player?.seekTo(currentWindow, playbackPosition)
        }

        val mediaSource = buildMediaSource(Uri.parse(mediaUrl))
        player?.prepare(mediaSource, true, false)
    }

    private fun releasePlayback() {
        player?.also {
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            playWhenReady = it.playWhenReady

            it.release()
        }

        player = null
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val userAgent = "momentum"

        return HlsMediaSource
            .Factory(DefaultHttpDataSourceFactory(userAgent))
            .createMediaSource(uri)
    }

}
