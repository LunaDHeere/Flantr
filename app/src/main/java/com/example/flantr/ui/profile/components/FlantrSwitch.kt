package com.example.flantr.ui.profile.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.flantr.ui.theme.PrimaryGradient

@Composable
fun FlantrSwitch(checked: Boolean, onCheckedChange: () -> Unit) {
    val offsetX by animateFloatAsState(targetValue = if (checked) 24f else 0f, label = "switch")

    Box(
        modifier = Modifier
            .width(52.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(100))
            .background(if (checked) PrimaryGradient else Brush.linearGradient(listOf(Color(0xFFD1D5DB), Color(0xFFD1D5DB))))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onCheckedChange() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .offset(x = offsetX.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
