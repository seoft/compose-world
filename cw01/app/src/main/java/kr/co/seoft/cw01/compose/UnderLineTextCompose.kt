package kr.co.seoft.cw01.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.co.seoft.cw01.ui.theme.Composeworld01Theme

@Preview(showBackground = true)
@Composable
fun UnderLineInputPreview() {
    Composeworld01Theme {
        UnderLineTextExample()
    }
}

@Composable
fun UnderLineTextExample() {

    val initText = "input"
    var currentText by remember { mutableStateOf(initText) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "currentText : $currentText")

        UnderLineTextSet(
            initText = initText,
            initSelectedIndex = null
        ) {
            currentText = it
        }
    }
}

@Composable
fun UnderLineTextSet(
    modifier: Modifier = Modifier,
    initText: String,
    initSelectedIndex: Int? = null,
    onCurrentText: (String) -> Unit
) {
    var currentText by remember { mutableStateOf(initText) }
    var selectedIndex: Int? by remember { mutableStateOf(initSelectedIndex) }
    var showKeyboard by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        currentText.forEachIndexed { index, c ->
            UnderLineText(c, index == selectedIndex) {
                selectedIndex = index
                showKeyboard = true
            }
        }
        FakeTextField(showKeyboard, keyboardActions = KeyboardActions(
            onDone = {
                showKeyboard = false
            }
        )) { inputChar ->
            selectedIndex?.let { index ->
                currentText = String(currentText.toCharArray().apply { this[index] = inputChar })
                onCurrentText.invoke(currentText)
                selectedIndex = if (currentText.length - 1 > index) {
                    index + 1
                } else {
                    showKeyboard = false
                    null
                }
            }
        }
    }
}

@Composable
fun UnderLineText(
    char: Char,
    showUnderLine: Boolean = false,
    onClick: () -> Unit
) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.clickable {
        onClick.invoke()
    }) {
        Text(char.toString(), fontSize = 40.sp)
        Text(
            text = "_",
            color = if (showUnderLine) Color.Unspecified else Color.Transparent,
            fontSize = 40.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}