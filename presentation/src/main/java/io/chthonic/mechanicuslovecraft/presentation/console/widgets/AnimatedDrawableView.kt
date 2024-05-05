package io.chthonic.mechanicuslovecraft.presentation.console.widgets

import android.graphics.PorterDuff
import android.graphics.drawable.AnimationDrawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.chthonic.mechanicuslovecraft.presentation.R
import io.chthonic.mechanicuslovecraft.presentation.theme.Purple200

@Composable
private fun AnimatedDrawableView(
    modifier: Modifier,
    @DrawableRes animationDrawableResource: Int,
) {
    AndroidView(modifier = modifier.size(48.dp, 48.dp), //.background(Color.White),
        factory = { ctx ->
            val animationDrawable =
                AppCompatResources.getDrawable(ctx, animationDrawableResource) as AnimationDrawable
            ImageView(ctx).apply {
                setImageDrawable(animationDrawable)
                setColorFilter(Purple200.toArgb(), PorterDuff.Mode.SRC_IN)
                scaleType = ImageView.ScaleType.FIT_CENTER
                animationDrawable.start()
            }
        }
    )
}

@Composable
fun AiTalkingView(modifier: Modifier) {
    AnimatedDrawableView(modifier, R.drawable.lovecraft_talk_anim)
}

@Composable
fun AiProcessingView(modifier: Modifier) {
    AnimatedDrawableView(modifier, R.drawable.lovecraft_think_anim)
}