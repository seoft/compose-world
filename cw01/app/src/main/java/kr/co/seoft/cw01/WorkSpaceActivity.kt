package kr.co.seoft.cw01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import kr.co.seoft.cw01.ui.theme.Composeworld01Theme

class WorkSpaceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Composeworld01Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box(Modifier.fillMaxSize()) {

// * ChatBottomLayout 채팅 페이지 내 하단 가변 UI 레이아웃
//                        ChatBottomLayout(
//                            Modifier.align(Alignment.BottomCenter),
//                            onImageClick = {},
//                            onCameraClick = {},
//                            onSendClick = {},
//                        )
                    }
                }
            }
        }
    }
}