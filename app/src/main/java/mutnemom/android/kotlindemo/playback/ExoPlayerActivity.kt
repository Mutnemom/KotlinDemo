package mutnemom.android.kotlindemo.playback

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import mutnemom.android.kotlindemo.databinding.ActivityExoPlayerBinding
import mutnemom.android.kotlindemo.extensions.toast

class ExoPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExoPlayerBinding

    private var mp4Url =
        "https://s3-ap-southeast-1.amazonaws.com/dev.elibrary-private-contents/encoded-media-upload/video/sample_video_1.mp4"

    private var mp3Url =
        "https://s3-ap-southeast-1.amazonaws.com/dev.elibrary-private-contents/encoded-media-upload/audio/sample_audio_1.mp3"

    private var mediaUrl =
        "https://s3-ap-southeast-1.amazonaws.com/dev.elibrary-private-contents/encoded-media-upload/audio/cf7b8258-309e-48fc-aa4a-eabf317c299c/playlist.m3u8"

    private var mediaUrl2 =
        "https://s3-ap-southeast-1.amazonaws.com/dev.elibrary-private-contents/a32b5f03-2931-40a7-99a6-4a2e0af4a839/encrypted-contents/364e6720-7526-4417-96de-d66110204b22/b7b80d5b-6666-49ad-93c1-101186173841.m3u8"

    private var mediaQuality =
        "https://s3-ap-southeast-1.amazonaws.com/dev.elibrary-private-contents/encoded-media-upload/video/bbf361f4-c10c-4fbe-b0ab-855ebebb271b/playlist.m3u8"

//    private var mediaUrl =
//        "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"

//    private var mediaUrl = "http://demo.unified-streaming.com/video/tears-of-steel/tears-of-steel.ism/.m3u8"

    private var player: ExoPlayer? = null

    private var playbackPosition: Long = 0L
    private var currentWindow: Int = 0
    private var playWhenReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityExoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setEvent()
    }

    override fun onResume() {
        super.onResume()
        initPlayback()
    }

    override fun onPause() {
        super.onPause()
        releasePlayback()
    }

    private fun setEvent() {
        binding.apply {
            btnSpeed.setOnClickListener { doubleSpeed() }

            btnPause.setOnClickListener { player?.playWhenReady = false }
            btnPlay.setOnClickListener { player?.playWhenReady = true }
        }
    }

    private fun initPlayback() {
        if (player == null) {
            val trackSelection = AdaptiveTrackSelection.Factory(
                DefaultBandwidthMeter(),
                1000,
                2000,
                2000,
                2000,
                .8f
            )

            player = ExoPlayerFactory.newSimpleInstance(
                DefaultRenderersFactory(this),
                DefaultTrackSelector(),
                DefaultLoadControl()
            )

            binding.exoPlayback.player = this.player
            player?.playWhenReady = playWhenReady
            player?.seekTo(currentWindow, playbackPosition)
        }

        val mediaSource = buildMediaSource(Uri.parse(mediaUrl2))
        player?.prepare(mediaSource, true, false)
        player?.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            }

            override fun onTracksChanged(
                trackGroups: TrackGroupArray?,
                trackSelections: TrackSelectionArray?
            ) {
                toast("track changed")
            }

            override fun onLoadingChanged(isLoading: Boolean) {
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
            }

            override fun onPositionDiscontinuity(reason: Int) {
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
                toast("speed: ${playbackParameters?.speed}")
            }

            override fun onSeekProcessed() {
            }

        })
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

        // for mp3, mp4
//        return ExtractorMediaSource.Factory(
//            DefaultHttpDataSourceFactory(userAgent)
//        ).createMediaSource(uri)

        // for m3u8
        return HlsMediaSource
            .Factory(
                DefaultHttpDataSourceFactory(
                    userAgent,
                    null,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    false
                )
            )
            .createMediaSource(uri)
    }

    private fun doubleSpeed() {
        player?.apply {
            val currentSpeed = playbackParameters?.speed ?: .5f
            val expected = currentSpeed * 2

            playbackParameters = PlaybackParameters(expected, 1f)
        }
    }






    private fun openLineAt() {
        when (isAppInstalled(this, linePackageName)) {
            true -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lineAtHytexts))
                startActivity(intent)
            }

            else -> popupToLoadLineApps()
        }
    }

    private fun popupToLoadLineApps() {
//        CustomAlertDialog().makeSimpleDialog(
//            this,
//            getString(R.string.text_warning_title),
//            getString(R.string.text_install_line_suggestion),
//            true
//        ) {
//            it?.dismiss()
//        }
    }

}

const val lineAtHytexts = "line://ti/p/@hytexts"
const val linePackageName = "jp.naver.line.android"
fun isAppInstalled(context: Context, packageName: String): Boolean {
    return context.packageManager.getLaunchIntentForPackage(packageName) != null
}
