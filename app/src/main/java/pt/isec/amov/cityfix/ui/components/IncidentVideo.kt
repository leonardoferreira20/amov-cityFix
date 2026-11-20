package pt.isec.amov.cityfix.ui.components

import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import pt.isec.amov.cityfix.R

@Composable
fun IncidentVideo(
    path: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    var cannotPlay by remember(path) { mutableStateOf(false) }
    if (LocalInspectionMode.current || cannotPlay) {
        Box(
            modifier = modifier
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.VideocamOff,
                contentDescription = stringResource(R.string.video_unavailable),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    var videoView by remember { mutableStateOf<VideoView?>(null) }
    var isPlaying by remember(path) { mutableStateOf(false) }
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { context ->
                VideoView(context).apply {
                    setOnPreparedListener { seekTo(1) }
                    setOnCompletionListener { isPlaying = false }
                    setOnErrorListener { _, _, _ ->
                        cannotPlay = true
                        true
                    }
                    videoView = this
                }
            },
            update = { view ->
                if (view.tag != path) {
                    view.tag = path
                    view.setVideoPath(path)
                }
            },
            onRelease = { it.stopPlayback() },
            modifier = Modifier
                .fillMaxSize()
                .semantics { this.contentDescription = contentDescription }
        )
        FilledTonalIconButton(
            onClick = {
                videoView?.let { view ->
                    if (isPlaying) view.pause() else view.start()
                    isPlaying = !isPlaying
                }
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = stringResource(if (isPlaying) R.string.video_pause else R.string.video_play)
            )
        }
    }
}
