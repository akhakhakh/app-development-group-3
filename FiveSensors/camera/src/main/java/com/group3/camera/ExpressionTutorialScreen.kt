package com.group3.camera

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ExprBlue    = Color(0xFF3885F0)
private val ExprDark    = Color(0xFF1A1A1F)
private val ExprSubtext = Color(0xFF8C8C91)

private data class ExpressionInfo(
    val name: String,
    val description: String,
    val drawableId: Int,
    val cardBg: Color,
    val cardBorder: Color,
)

private val expressions = listOf(
    ExpressionInfo(
        name        = "NEUTRAL",
        description = "Keep a relaxed, straight-faced expression.",
        drawableId  = R.drawable.emoticon_neutral,
        cardBg      = Color(0xFFF5F5F5),
        cardBorder  = Color(0xFF8C8C91),
    ),
    ExpressionInfo(
        name        = "SMILE",
        description = "Smile while keeping your eyes open.",
        drawableId  = R.drawable.smile,
        cardBg      = Color(0xFFD9EBFF),
        cardBorder  = Color(0xFF3885F0),
    ),
    ExpressionInfo(
        name        = "WINK LEFT",
        description = "Close your left eye and keep your right eye wide open.",
        drawableId  = R.drawable.neutral_leftwink,
        cardBg      = Color(0xFFFCF7E0),
        cardBorder  = Color(0xFFFAD12E),
    ),
    ExpressionInfo(
        name        = "SMILE + WINK LEFT",
        description = "Smile and close your left eye at the same time.",
        drawableId  = R.drawable.smile_leftwink,
        cardBg      = Color(0xFFFCF7E0),
        cardBorder  = Color(0xFFFAD12E),
    ),
    ExpressionInfo(
        name        = "WINK RIGHT",
        description = "Close your right eye and keep your left eye wide open.",
        drawableId  = R.drawable.neutral_rightwink,
        cardBg      = Color(0xFFE4FFEE),
        cardBorder  = Color(0xFF26AA3F),
    ),
    ExpressionInfo(
        name        = "SMILE + WINK RIGHT",
        description = "Smile and close your right eye at the same time.",
        drawableId  = R.drawable.smile_rightwink,
        cardBg      = Color(0xFFE4FFEE),
        cardBorder  = Color(0xFF26AA3F),
    ),
    ExpressionInfo(
        name        = "CLOSE EYES",
        description = "Shut both eyes completely.",
        drawableId  = R.drawable.neutral_closeeyes,
        cardBg      = Color(0xFFF0E8FF),
        cardBorder  = Color(0xFF8B5CF6),
    ),
    ExpressionInfo(
        name        = "SMILE + CLOSE EYES",
        description = "Smile while keeping both eyes completely shut.",
        drawableId  = R.drawable.smile_closeeyes,
        cardBg      = Color(0xFFF0E8FF),
        cardBorder  = Color(0xFF8B5CF6),
    ),
    ExpressionInfo(
        name        = "LOOK LEFT",
        description = "Turn your head to your left.",
        drawableId  = R.drawable.neutral_lookleft,
        cardBg      = Color(0xFFFFF0E0),
        cardBorder  = Color(0xFFF97316),
    ),
    ExpressionInfo(
        name        = "SMILE + LOOK LEFT",
        description = "Smile while turning your head to your left.",
        drawableId  = R.drawable.smile_lookleft,
        cardBg      = Color(0xFFFFF0E0),
        cardBorder  = Color(0xFFF97316),
    ),
    ExpressionInfo(
        name        = "LOOK RIGHT",
        description = "Turn your head to your right.",
        drawableId  = R.drawable.neutral_lookright,
        cardBg      = Color(0xFFFFE8F0),
        cardBorder  = Color(0xFFEC4899),
    ),
    ExpressionInfo(
        name        = "SMILE + LOOK RIGHT",
        description = "Smile while turning your head to your right.",
        drawableId  = R.drawable.smile_lookright,
        cardBg      = Color(0xFFFFE8F0),
        cardBorder  = Color(0xFFEC4899),
    ),
    ExpressionInfo(
        name        = "LOOK UP",
        description = "Tilt your chin down and gaze upward.",
        drawableId  = R.drawable.neutral_lookup,
        cardBg      = Color(0xFFE8F8FF),
        cardBorder  = Color(0xFF0EA5E9),
    ),
    ExpressionInfo(
        name        = "SMILE + LOOK UP",
        description = "Smile while gazing upward.",
        drawableId  = R.drawable.smile_lookup,
        cardBg      = Color(0xFFE8F8FF),
        cardBorder  = Color(0xFF0EA5E9),
    ),
    ExpressionInfo(
        name        = "LOOK DOWN",
        description = "Lower your chin toward your chest.",
        drawableId  = R.drawable.neutral_lookdown,
        cardBg      = Color(0xFFFFF4F4),
        cardBorder  = Color(0xFFEF4444),
    ),
    ExpressionInfo(
        name        = "SMILE + LOOK DOWN",
        description = "Smile while lowering your chin toward your chest.",
        drawableId  = R.drawable.smile_lookdown,
        cardBg      = Color(0xFFFFF4F4),
        cardBorder  = Color(0xFFEF4444),
    ),
    ExpressionInfo(
        name        = "TILT LEFT",
        description = "Tilt your head sideways to your left.",
        drawableId  = R.drawable.tiltleft,
        cardBg      = Color(0xFFF0FFF4),
        cardBorder  = Color(0xFF22C55E),
    ),
    ExpressionInfo(
        name        = "TILT RIGHT",
        description = "Tilt your head sideways to your right.",
        drawableId  = R.drawable.tiltright,
        cardBg      = Color(0xFFFDF4FF),
        cardBorder  = Color(0xFFA855F7),
    ),
)

@Composable
fun ExpressionTutorialScreen(onNext: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { expressions.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(ExprDark),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "EXPRESSIONS",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Learn these before you play",
            color = ExprSubtext,
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp)
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 16.dp),
            contentPadding = PaddingValues(horizontal = 36.dp),
            pageSpacing = 14.dp,
        ) { page ->
            ExpressionCard(expressions[page])
        }

        Text(
            text = "${pagerState.currentPage + 1} / ${expressions.size}  ·  swipe to browse",
            color = ExprSubtext,
            fontSize = 11.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 14.dp)
        )

        Button(
            onClick = onNext,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .padding(top = 16.dp, bottom = 24.dp)
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ExprBlue)
        ) {
            Text(
                text = "LET'S GO!",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ExpressionCard(expr: ExpressionInfo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(expr.cardBg, RoundedCornerShape(16.dp))
            .border(2.dp, expr.cardBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = expr.drawableId),
            contentDescription = expr.name,
            modifier = Modifier.size(180.dp)
        )

        Spacer(Modifier.height(28.dp))

        Text(
            text = expr.name,
            color = ExprDark,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 2,
            lineHeight = 28.sp
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = expr.description,
            color = ExprSubtext,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 2,
            lineHeight = 20.sp
        )
    }
}
