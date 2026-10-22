package com.nameisjayant.composecomponents.timeline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.timeline.ComposeTimeline
import com.nameisjayant.components.timeline.NumberIndicator
import com.nameisjayant.components.timeline.TimelineAnimation
import com.nameisjayant.components.timeline.TimelineContentSide
import com.nameisjayant.components.timeline.TimelineIndicatorAlignment
import com.nameisjayant.components.timeline.TimelineLineStyle
import com.nameisjayant.components.timeline.TimelineOrientation
import com.nameisjayant.components.timeline.TimelineStepState
import com.nameisjayant.components.timeline.timelineItems
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme

private val Steps = listOf("Placed", "Packed", "Shipped", "Delivered")

@Composable
private fun TimelineSamples() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(28.dp)) {
            ComposeTimeline(
                items = Steps,
                stepState = { index, _ -> if (index == 2) TimelineStepState.Error else TimelineStepState.forProgress(index, 2) },
                pendingLineStyle = TimelineLineStyle.Dashed,
                animation = TimelineAnimation.None,
                oppositeContent = { Text("10:0${it.index}", textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth()) },
            ) { step ->
                Text(step.item, style = MaterialTheme.typography.titleMedium)
            }
            ComposeTimeline(
                items = Steps,
                stepState = { index, _ -> TimelineStepState.forProgress(index, 1) },
                orientation = TimelineOrientation.Horizontal,
                contentSide = TimelineContentSide.Alternate,
                indicatorAlignment = TimelineIndicatorAlignment.Center,
                indicatorSize = 28.dp,
                itemSpacing = 4.dp,
                contentSpacing = 8.dp,
                pendingLineStyle = TimelineLineStyle.Dotted,
                animation = TimelineAnimation.None,
                indicator = { NumberIndicator("${it.index + 1}", it.state) },
            ) { step ->
                Text(step.item, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun LazyTimelineSample() {
    Surface(color = MaterialTheme.colorScheme.background) {
        LazyColumn(Modifier.height(320.dp), contentPadding = PaddingValues(16.dp)) {
            item { Text("History", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp)) }
            timelineItems(
                items = (1..12).toList(),
                key = { it },
                contentSide = TimelineContentSide.Alternate,
                itemSpacing = 16.dp,
                animation = TimelineAnimation.None,
            ) { step ->
                val alignEnd = step.contentSide == TimelineContentSide.Start
                Text(
                    text = "Event ${step.item}",
                    textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview
@Composable
private fun TimelineLightPreview() = ComposeComponentsTheme(darkTheme = false) { TimelineSamples() }

@Preview
@Composable
private fun TimelineDarkPreview() = ComposeComponentsTheme(darkTheme = true) { TimelineSamples() }

@Preview
@Composable
private fun LazyTimelinePreview() = ComposeComponentsTheme(darkTheme = false) { LazyTimelineSample() }
