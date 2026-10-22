package com.nameisjayant.composecomponents.ratingbar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.components.ratingbar.ComposeRatingBar
import com.nameisjayant.components.ratingbar.ComposeRatingBarDefaults
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme

@Composable
private fun RatingSamples() {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            ComposeRatingBar(rating = 3.5f, onRatingChange = {}, colors = ComposeRatingBarDefaults.accentColors())
            ComposeRatingBar(rating = 4f, onRatingChange = {}, shape = ComposeRatingBarDefaults.SharpStarShape)
            ComposeRatingBar(
                rating = 2.4f,
                onRatingChange = null,
                shape = ComposeRatingBarDefaults.HeartShape,
                colors = ComposeRatingBarDefaults.outlinedColors(accent = MaterialTheme.colorScheme.error),
                borderWidth = ComposeRatingBarDefaults.OutlineWidth,
            )
            ComposeRatingBar(rating = 3f, onRatingChange = {}, enabled = false)
            ComposeRatingBar(
                rating = 7f,
                onRatingChange = {},
                maxRating = 10,
                itemSize = 22.dp,
                itemSpacing = 3.dp,
                shape = ComposeRatingBarDefaults.starShape(points = 6, roundness = 0.3f),
            )
        }
    }
}

@Preview
@Composable
private fun RatingLightPreview() = ComposeComponentsTheme(darkTheme = false) { RatingSamples() }

@Preview
@Composable
private fun RatingDarkPreview() = ComposeComponentsTheme(darkTheme = true) { RatingSamples() }
