package kr.co.seoft.cw01.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kr.co.seoft.cw01.ui.theme.Composeworld01Theme
import kr.co.seoft.cw01.utils.e

@Preview(showBackground = true)
@Composable
fun RouterPreview() {
    Composeworld01Theme {
        RouterScreen {
            it.e()
        }
    }
}

@Composable
fun RouterScreen(onSelect: (Int) -> (Unit)) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(10){
            Button(onClick = { onSelect.invoke(it) },modifier = Modifier.fillMaxWidth()) {
                Text(text = "$it")
            }
            Spacer(modifier = Modifier.size(10.dp))
        }
    }
}