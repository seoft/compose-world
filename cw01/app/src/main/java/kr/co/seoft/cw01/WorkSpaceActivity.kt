package kr.co.seoft.cw01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import kr.co.seoft.cw01.compose.AudioRecordEvent
import kr.co.seoft.cw01.compose.ChatBottomLayoutContainer
import kr.co.seoft.cw01.compose.HorizontalWaveTester
import kr.co.seoft.cw01.ui.theme.Composeworld01Theme
import kr.co.seoft.cw01.utils.e

class WorkSpaceActivity : ComponentActivity() {

    private val tester = HorizontalWaveTester()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Composeworld01Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
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
                                        tester.startFlow(lifecycleScope)
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
            }
        }

    }
}