package com.nameisjayant.components.ratingbar

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.lerp
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * A star with [points] tips, scaled to fill the bounds while keeping its proportions.
 * [roundness] (0..1) rounds every corner by that share of half an edge.
 */
internal class PolygonStarShape(
    private val points: Int,
    private val innerRadiusRatio: Float,
    private val roundness: Float,
) : Shape {

    init {
        require(points >= 3) { "A star needs at least 3 points, was $points" }
        require(innerRadiusRatio > 0f && innerRadiusRatio <= 1f) { "innerRadiusRatio must be in (0, 1]" }
    }

    // Vertices on a unit circle, first tip pointing up.
    private val unit: List<Offset> = List(points * 2) { k ->
        val radius = if (k % 2 == 0) 1f else innerRadiusRatio
        val angle = -PI / 2 + k * PI / points
        Offset((radius * cos(angle)).toFloat(), (radius * sin(angle)).toFloat())
    }
    private val minX = unit.minOf { it.x }
    private val minY = unit.minOf { it.y }
    private val unitWidth = unit.maxOf { it.x } - minX
    private val unitHeight = unit.maxOf { it.y } - minY

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val scale = min(size.width / unitWidth, size.height / unitHeight)
        val dx = (size.width - unitWidth * scale) / 2f - minX * scale
        val dy = (size.height - unitHeight * scale) / 2f - minY * scale
        val vertices = unit.map { Offset(it.x * scale + dx, it.y * scale + dy) }
        val t = roundness.coerceIn(0f, 1f) / 2f
        val path = Path()
        vertices.forEachIndexed { i, vertex ->
            val before = lerp(vertex, vertices[(i - 1 + vertices.size) % vertices.size], t)
            val after = lerp(vertex, vertices[(i + 1) % vertices.size], t)
            if (i == 0) path.moveTo(before.x, before.y) else path.lineTo(before.x, before.y)
            path.quadraticTo(vertex.x, vertex.y, after.x, after.y)
        }
        path.close()
        return Outline.Generic(path)
    }

    override fun equals(other: Any?): Boolean =
        this === other || (
            other is PolygonStarShape &&
                points == other.points &&
                innerRadiusRatio == other.innerRadiusRatio &&
                roundness == other.roundness
            )

    override fun hashCode(): Int = (points * 31 + innerRadiusRatio.hashCode()) * 31 + roundness.hashCode()
}

/** A symmetric heart, scaled to fit the bounds. */
internal object HeartShape : Shape {

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val side = min(size.width, size.height)
        val left = (size.width - side) / 2f
        val top = (size.height - side) / 2f
        // Drawn on a 0..1 square; the shape spans y 0.04..0.96.
        fun x(v: Float) = left + v * side
        fun y(v: Float) = top + v * side
        val path = Path().apply {
            moveTo(x(0.5f), y(0.96f))
            cubicTo(x(0.2f), y(0.76f), x(0f), y(0.56f), x(0f), y(0.33f))
            cubicTo(x(0f), y(0.15f), x(0.14f), y(0.04f), x(0.29f), y(0.04f))
            cubicTo(x(0.39f), y(0.04f), x(0.46f), y(0.09f), x(0.5f), y(0.17f))
            cubicTo(x(0.54f), y(0.09f), x(0.61f), y(0.04f), x(0.71f), y(0.04f))
            cubicTo(x(0.86f), y(0.04f), x(1f), y(0.15f), x(1f), y(0.33f))
            cubicTo(x(1f), y(0.56f), x(0.8f), y(0.76f), x(0.5f), y(0.96f))
            close()
        }
        return Outline.Generic(path)
    }

    override fun toString(): String = "HeartShape"
}
