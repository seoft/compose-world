package kr.co.seoft.cw01.compose

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlinx.coroutines.GlobalScope
import kr.co.seoft.cw01.R
import kr.co.seoft.cw01.ui.theme.Composeworld01Theme
import kr.co.seoft.cw01.utils.e
import java.lang.Integer.max
import java.lang.Integer.min
import java.util.*
import kotlin.math.roundToInt

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ChatBottomLayoutPreview() {
    val tester = HorizontalWaveTester()
    Composeworld01Theme {
        // * ChatBottomLayout 채팅 페이지 내 하단 가변 UI 레이아웃을 포함한 full 레이아웃
        ChatBottomLayoutContainer(
            Modifier.fillMaxSize(),
            ratios = tester.gaugeState.collectAsState().value,
            text = tester.timeState.collectAsState().value,
            onImageClick = {},
            onCameraClick = {},
            onSendClick = {},
            onAudioRecordEvent = {
                when (it) {
                    AudioRecordEvent.START -> {
                        tester.startFlow(GlobalScope)
                        "START".e()
                    }
                    AudioRecordEvent.SEND -> {
                        tester.stopFlow()
                        "SEND".e()
                    }
                    AudioRecordEvent.CANCEL -> {
                        tester.stopFlow()
                        "CANCEL".e()
                    }
                    AudioRecordEvent.TOO_SHORT_TO_FAIL -> {
                        tester.stopFlow()
                        "TOO_SHORT_TO_FAIL".e()
                    }
                }
            }
        )
    }
}

@Composable
fun ChatBottomLayoutContainer(
    modifier: Modifier,
    ratios: List<Float>,
    text: String,
    onImageClick: () -> Unit,
    onCameraClick: () -> Unit,
    onSendClick: () -> Unit,
    onAudioRecordEvent: (AudioRecordEvent) -> Unit
) {
    val recordViewMinBottomMargin = 92
    val recordViewMaxBottomMargin = 220
    val minRecordTime = 1500

    var recordLayoutShowState by remember { mutableStateOf(false) }
    var recordLayoutShowTime by remember { mutableStateOf(0L) }
    var recordViewBottomMargin by remember { mutableStateOf(recordViewMinBottomMargin) }

    Box(modifier = modifier.fillMaxSize()) {

        ChatRecordLayout(
            modifier = Modifier.align(Alignment.BottomCenter),
            recordLayoutShowState = recordLayoutShowState,
            recordViewBottomMargin = recordViewBottomMargin,
            inCancelBoundary = recordViewBottomMargin == recordViewMaxBottomMargin,
            ratios = ratios,
            text = text
        )

        ChatBottomLayout(
            Modifier.align(Alignment.BottomCenter),
            onImageClick = onImageClick,
            onCameraClick = onCameraClick,
            onSendClick = onSendClick,
            onAudioEventStatus = {
                when (it) {
                    is AudioViewTouchEvent.Start -> {
                        recordLayoutShowState = true
                        recordLayoutShowTime = Date().time
                        recordViewBottomMargin = recordViewMinBottomMargin
                        onAudioRecordEvent.invoke(AudioRecordEvent.START)
                    }
                    is AudioViewTouchEvent.Move -> {
                        val currentBottomMargin = max(
                            recordViewMinBottomMargin, recordViewMinBottomMargin + it.positionY
                        )
                        recordViewBottomMargin = min(recordViewMaxBottomMargin, currentBottomMargin)
                    }
                    is AudioViewTouchEvent.End -> {
                        if (recordViewBottomMargin == recordViewMaxBottomMargin) {
                            onAudioRecordEvent.invoke(AudioRecordEvent.CANCEL)
                        } else {
                            if ((recordLayoutShowTime + minRecordTime) < Date().time) {
                                onAudioRecordEvent.invoke(AudioRecordEvent.SEND)
                            } else {
                                onAudioRecordEvent.invoke(AudioRecordEvent.TOO_SHORT_TO_FAIL)
                            }
                        }
                        recordLayoutShowState = false
                        recordLayoutShowTime = -1
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChatRecordLayout(
    modifier: Modifier,
    recordLayoutShowState: Boolean,
    recordViewBottomMargin: Int,
    inCancelBoundary: Boolean,
    ratios: List<Float>,
    text: String
) {
    Box(modifier = modifier.height(268.dp)) {
        if (recordLayoutShowState) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(bottom = 220.dp)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            )
        }
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = recordLayoutShowState,
            enter = slideInVertically(initialOffsetY = { 300 })
        ) {
            HorizontalWaveLayout(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = recordViewBottomMargin.dp)
                    .background(if (inCancelBoundary) Color.Red else Color.Black),
                ratios = ratios,
                text = text
            )
        }
    }
}

@Composable
fun ChatBottomLayout(
    modifier: Modifier = Modifier,
    onImageClick: () -> Unit,
    onCameraClick: () -> Unit,
    onSendClick: () -> Unit,
    onAudioEventStatus: (AudioViewTouchEvent) -> Unit
) {
    var textState by remember { mutableStateOf("") }
    var isOptionEnableState by remember { mutableStateOf(true) }

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFD1E5FD))
    ) {

        val (chatTextField, leftLayout, rightLayout) = createRefs()

        val commonIconOuterSize = 44.dp

        Row(modifier = Modifier
            .padding(bottom = 3.dp)
            .constrainAs(leftLayout) {
                start.linkTo(parent.start)
                end.linkTo(chatTextField.start)
                bottom.linkTo(parent.bottom)
            }) {

            if (isOptionEnableState) {
                IconButton(modifier = Modifier
                    .size(commonIconOuterSize), onClick = { onImageClick.invoke() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_image),
                        tint = Color.Black,
                        contentDescription = null
                    )
                }
                IconButton(modifier = Modifier
                    .size(commonIconOuterSize), onClick = { onCameraClick.invoke() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        tint = Color.Black,
                        contentDescription = null
                    )
                }
            } else {
                IconButton(modifier = Modifier
                    .size(commonIconOuterSize), onClick = {
                    isOptionEnableState = true
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_right),
                        tint = Color.Black,
                        contentDescription = null,
                    )
                }
            }
        }

        ChatBottomTextField(modifier = Modifier
            .defaultMinSize(minHeight = 36.dp)
            .constrainAs(chatTextField) {
                start.linkTo(leftLayout.end, 4.dp)
                end.linkTo(rightLayout.start, 4.dp)
                bottom.linkTo(parent.bottom, 6.dp)
                top.linkTo(parent.top, 6.dp)
                width = Dimension.fillToConstraints
            },
            onTextChange = {
                textState = it
            },
            onClick = {
                isOptionEnableState = false
            }
        )

        Row(modifier = Modifier
            .padding(bottom = 3.dp)
            .constrainAs(rightLayout) {
                start.linkTo(chatTextField.end)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }) {

            if (isOptionEnableState) {
                Box(
                    modifier = Modifier
                        .size(commonIconOuterSize)
                        .audioTouchEvent(onAudioEventStatus)
                ) {
                    Icon(
                        modifier = Modifier.align(Alignment.Center),
                        painter = painterResource(id = R.drawable.ic_mic),
                        tint = Color.Black,
                        contentDescription = null
                    )
                }
            } else {
                IconButton(modifier = Modifier
                    .size(commonIconOuterSize),
                    enabled = textState.isNotEmpty(),
                    onClick = { onSendClick.invoke() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send),
                        tint = if (textState.isEmpty()) Color.Gray else Color.Black,
                        contentDescription = null
                    )
                }
            }
        }
    }
}


private const val NOT_LONG_TOUCHED_STATUS = -1

// 터치 컨트롤 ref : https://stackoverflow.com/questions/64571945/how-can-i-get-ontouchevent-in-jetpack-compose
@SuppressLint("UnnecessaryComposedModifier")
private fun Modifier.audioTouchEvent(
    onAudioEventStatus: (AudioViewTouchEvent) -> Unit
): Modifier = composed {

    var firstTouchedPositionY by remember { mutableStateOf(NOT_LONG_TOUCHED_STATUS) }

    this
        .pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    firstTouchedPositionY = it.y.roundToInt()
                    onAudioEventStatus.invoke(AudioViewTouchEvent.Start)
                }
            )
        }
        .pointerInput(Unit) {
            forEachGesture {
                awaitPointerEventScope {
                    awaitFirstDown()
                    do {
                        val event: PointerEvent = awaitPointerEvent()
                        // ACTION_MOVE loop
                        if (firstTouchedPositionY != NOT_LONG_TOUCHED_STATUS) {
                            event.changes.getOrNull(0)?.position?.y?.let {
                                onAudioEventStatus.invoke(
                                    AudioViewTouchEvent.Move(firstTouchedPositionY - it.roundToInt())
                                )
                            }
                        }
                    } while (event.changes.any { it.pressed })

                    // ACTION_TOUCH_UP
                    if (firstTouchedPositionY != NOT_LONG_TOUCHED_STATUS) {
                        onAudioEventStatus.invoke(AudioViewTouchEvent.End)
                    }
                    firstTouchedPositionY = NOT_LONG_TOUCHED_STATUS
                }
            }
        }
}

@Composable
fun ChatBottomTextField(
    modifier: Modifier = Modifier, onTextChange: (String) -> Unit, onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    if (interactionSource.collectIsPressedAsState().value) onClick.invoke()
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xfff4f3f6))
    ) {

        var textState by remember { mutableStateOf("") }

        SimpleTextField(
            modifier = Modifier
                .padding(10.dp, 8.dp)
                .align(Alignment.Center),
            value = textState,
            textStyle = TextStyle(
                color = Color(0xFF000000)
            ),
            maxLines = 4,
            hint = {
                Text(
                    "Message", style = TextStyle(
                        color = Color(0xFFB9B9B9)
                    )
                )
            },
            onTextChange = {
                textState = it
                onTextChange.invoke(it)
            },
            interactionSource = interactionSource
        )
    }

}

sealed class AudioViewTouchEvent {
    object Start : AudioViewTouchEvent()
    data class Move(val positionY: Int) : AudioViewTouchEvent()
    object End : AudioViewTouchEvent()
}

enum class AudioRecordEvent { START, SEND, CANCEL, TOO_SHORT_TO_FAIL }