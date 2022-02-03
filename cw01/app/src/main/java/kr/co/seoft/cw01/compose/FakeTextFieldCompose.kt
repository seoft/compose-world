package kr.co.seoft.cw01.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.seoft.cw01.ui.theme.Composeworld01Theme

@Preview(showBackground = true)
@Composable
fun FakeTextFieldPreview() {
    Composeworld01Theme {
        FakeTextFieldExample()
    }
}

@Composable
fun FakeTextFieldExample() {
    var isShow by remember { mutableStateOf(false) }
    var currentChar by remember { mutableStateOf(' ') }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("currentChar is : $currentChar")
        Spacer(modifier = Modifier.height(10.dp))
        Button(onClick = { isShow = !isShow }) {
            Text(text = if (isShow) "Hide Keyboard" else "Show keyboard")
        }
        FakeTextField(isShow) {
            currentChar = it
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FakeTextField(
    isShowKeyboard: Boolean,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onChar: (Char) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    TextField(
        value = "",
        onValueChange = { onChar.invoke(it.first()) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            cursorColor = Color.Transparent,
            disabledTextColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .width(0.dp)
            .height(0.dp)
            .focusRequester(focusRequester)
    )
    if (isShowKeyboard) {
        focusRequester.requestFocus()
        keyboardController?.show()
    } else {
        keyboardController?.hide()
    }
}