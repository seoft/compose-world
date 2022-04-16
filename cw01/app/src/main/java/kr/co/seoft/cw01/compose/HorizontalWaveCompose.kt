package kr.co.seoft.cw01.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.co.seoft.cw01.ui.theme.Composeworld01Theme
import java.util.*

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun HorizontalWaveLayoutPreview() {
    val tester = HorizontalWaveTester()
    Composeworld01Theme {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalWaveLayout(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(Color.Black),
                ratios = tester.gaugeState.collectAsState().value,
                text = tester.timeState.collectAsState().value,
            )
        }
    }
    tester.startFlow(GlobalScope)
}

@Composable
fun HorizontalWaveLayout(modifier: Modifier, ratios: List<Float>, text: String) {
    Box(modifier = modifier.size(136.dp, 54.dp)) {
        Row(
            modifier = Modifier
                .padding(start = 21.dp)
                .align(Alignment.CenterStart)
        ) {
            ratios.take(18).forEach {
                Box(
                    modifier = Modifier
                        .padding(start = 1.dp)
                        .align(CenterVertically)
                        .size(2.dp, ((it * 10) + 4).dp) // height max 14, min 4
                        .clip(RoundedCornerShape(1.dp))
                        .background(Color.White)
                )
            }
        }
        Text(
            modifier = Modifier
                .padding(end = 20.dp)
                .align(Alignment.CenterEnd),
            text = text,
            fontSize = 15.sp,
            color = Color.White
        )
    }
}

class HorizontalWaveTester {

    private val horizontalWaveLayoutTestValue = listOf(
        0,
        10400,
        8268,
        9320,
        10023,
        9992,
        10598,
        13009,
        14555,
        12961,
        15469,
        14576,
        13766,
        18418,
        21801,
        20487,
        22025,
        22040,
        22007,
        21228,
        19857,
        20931,
        22118,
        23022,
        24090,
        22279,
        20450,
        19812,
        19847,
        19989,
        18909,
        18400,
        15199,
        11001,
        9250,
        7126,
        5137,
        3934,
        3596,
        3328,
        2260,
        2220,
        590,
        202,
        231,
        274,
        174,
        148,
        194,
        1856,
        3430,
        5466,
        4594,
        6673,
        9008,
        11234,
        11049,
        12145,
        18207,
        20438,
        20634,
        23820,
        24251,
        25279,
        26726,
        24612,
        22993,
        22965,
        21949,
        22767,
        21609,
        21914,
        21972,
        20692,
        22888,
        22100,
        21800,
        21321,
        21375,
        17815,
        12281,
        9078,
        6965,
        4164,
        2642,
        482,
        150,
        0,
        10400,
        8268,
        9320,
        10023,
        9992,
        10598,
        13009,
        14555,
        12961,
        15469,
        14576,
        13766,
        18418,
        21801,
        20487,
        22025,
        22040,
        22007,
        21228,
        19857,
        20931,
        22118,
        23022,
        24090,
        22279,
        20450,
        19812,
        19847,
        19989,
        18909,
        18400,
        15199,
        11001,
        9250,
        7126,
        5137,
        3934,
        3596,
        3328,
        2260,
        2220,
        590,
        202,
        231,
        274,
        174,
        148,
        194,
        1856,
        3430,
        5466,
        4594,
        6673,
        9008,
        11234,
        11049,
        12145,
        18207,
        20438,
        20634,
        23820,
        24251,
        25279,
        26726,
        24612,
        22993,
        22965,
        21949,
        22767,
        21609,
        21914,
        21972,
        20692,
        22888,
        22100,
        21800,
        21321,
        21375,
        17815,
        12281,
        9078,
        6965,
        4164,
        2642,
        482,
        150
    )

    private var count = 0
    private var time = 0
    private val ratiosQueue: Queue<Float> = LinkedList()

    private val _gaugeFlow = MutableStateFlow(ratiosQueue.toList())
    val gaugeState = _gaugeFlow.asStateFlow()

    private val _timeFlow = MutableStateFlow("0:00")
    val timeState = _timeFlow.asStateFlow()

    var gaugeJob: Job? = null
    var timeJob: Job? = null

    private fun initQueue() {
        ratiosQueue.clear()
        repeat(14) { ratiosQueue.add(0f) }
    }

    fun startFlow(scope: CoroutineScope) {
        initQueue()
        count = 0
        time = 0
        _gaugeFlow.tryEmit(emptyList())
        _timeFlow.tryEmit("0:00")
        gaugeJob = scope.launch {
            while (true) {
                delay(100)
                val currentCount = count++ % horizontalWaveLayoutTestValue.size
                val currentFloat =
                    horizontalWaveLayoutTestValue[currentCount] / Short.MAX_VALUE.toFloat()
                ratiosQueue.add(currentFloat)
                ratiosQueue.poll()
                _gaugeFlow.emit(ratiosQueue.toList())
            }
        }
        timeJob = scope.launch {
            while (true) {
                delay(1000)
                _timeFlow.emit(getTimeString(time))
                time++
            }
        }
    }

    private fun getTimeString(seconds: Int): String {
        return String.format("%02d:%02d", seconds % 3600 / 60, seconds % 60)
    }

    fun stopFlow() {
        gaugeJob?.cancel()
        gaugeJob = null
        timeJob?.cancel()
        timeJob = null
    }
}