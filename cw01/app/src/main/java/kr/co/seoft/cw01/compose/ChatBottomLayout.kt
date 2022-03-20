package kr.co.seoft.cw01.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kr.co.seoft.cw01.R
import kr.co.seoft.cw01.ui.theme.Composeworld01Theme

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ChatBottomLayoutPreview() {
    Composeworld01Theme {
        Box(Modifier.fillMaxSize()) {
            ChatBottomLayout(
                Modifier.align(Alignment.BottomCenter),
                onImageClick = {},
                onCameraClick = {},
                onSendClick = {}
            )
        }
    }
}

@Composable
fun ChatBottomLayout(
    modifier: Modifier = Modifier,
    onImageClick: () -> Unit,
    onCameraClick: () -> Unit,
    onSendClick: () -> Unit
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
                IconButton(modifier = Modifier
                    .size(commonIconOuterSize), onClick = { }) {
                    Icon(
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

