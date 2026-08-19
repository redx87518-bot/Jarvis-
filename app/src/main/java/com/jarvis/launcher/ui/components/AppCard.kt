package com.jarvis.launcher.ui.components

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jarvis.core.AppInfo
import com.jarvis.launcher.ui.theme.jarvisColors

@Composable
fun AppCard(
    app: AppInfo,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    showLabel: Boolean = true,
    onClick: (AppInfo) -> Unit,
    onLongClick: ((AppInfo) -> Unit)? = null,
) {
    val context = LocalContext.current
    val colors = jarvisColors()
    val shape = RoundedCornerShape(16.dp)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(shape)
            .clickable(onClick = { onClick(app) })
            .pointerInput(onLongClick != null) {
                if (onLongClick != null) {
                    detectTapGestures(
                        onLongPress = { onLongClick(app) },
                        onTap = { onClick(app) },
                    )
                }
            }
            .size(size)
            .padding(8.dp),
    ) {
        val iconSize = if (showLabel) 48.dp else size * 0.6f
        val iconBitmap = remember(app.packageName) {
            loadIconBitmap(context, app.packageName, iconSize)
        }

        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.surfaceVariant.copy(alpha = 0.4f))
        ) {
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap.asImageBitmap(),
                    contentDescription = app.appName,
                    contentScale = ContentScale.Crop,
                )
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.size(4.dp))
            Text(
                text = app.appName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colors.textPrimary,
            )
        }
    }
}

private fun loadIconBitmap(
    context: Context,
    packageName: String,
    size: Dp,
): Bitmap? {
    return try {
        val pm = context.packageManager
        val drawable = pm.getApplicationIcon(packageName)
        val px = (size.toPx(context.resources.displayMetrics)).toInt()
        val bitmap = Bitmap.createBitmap(px, px, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmap
    } catch (e: Exception) {
        null
    }
}

private fun Dp.toPx(displayMetrics: android.util.DisplayMetrics): Float {
    return this.value * displayMetrics.density
}
