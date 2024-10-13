@file:Suppress("ObjectPropertyName", "UnusedReceiverParameter")

package ru.n08i40k.polytechnic.next.ui.icons.appicons.filled

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.EvenOdd
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Butt
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import ru.n08i40k.polytechnic.next.ui.icons.appicons.FilledGroup

val FilledGroup.Error: ImageVector
    get() {
        if (_error != null) {
            return _error!!
        }
        _error = Builder(
            name = "Error", defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24.0f, viewportHeight = 24.0f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000)), stroke = null, strokeLineWidth = 0.0f,
                strokeLineCap = Butt, strokeLineJoin = Miter, strokeLineMiter = 4.0f,
                pathFillType = EvenOdd
            ) {
                moveTo(1.25f, 8.0f)
                curveTo(1.25f, 4.2721f, 4.2721f, 1.25f, 8.0f, 1.25f)
                lineTo(16.0f, 1.25f)
                curveTo(19.7279f, 1.25f, 22.75f, 4.2721f, 22.75f, 8.0f)
                lineTo(22.75f, 16.0f)
                curveTo(22.75f, 19.7279f, 19.7279f, 22.75f, 16.0f, 22.75f)
                lineTo(8.0f, 22.75f)
                curveTo(4.2721f, 22.75f, 1.25f, 19.7279f, 1.25f, 16.0f)
                lineTo(1.25f, 8.0f)
                close()
                moveTo(8.4697f, 8.4697f)
                curveTo(8.7626f, 8.1768f, 9.2374f, 8.1768f, 9.5303f, 8.4697f)
                lineTo(12.0f, 10.9393f)
                lineTo(14.4697f, 8.4697f)
                curveTo(14.7626f, 8.1768f, 15.2374f, 8.1768f, 15.5303f, 8.4697f)
                curveTo(15.8232f, 8.7626f, 15.8232f, 9.2374f, 15.5303f, 9.5303f)
                lineTo(13.0606f, 12.0f)
                lineTo(15.5303f, 14.4697f)
                curveTo(15.8232f, 14.7626f, 15.8232f, 15.2374f, 15.5303f, 15.5303f)
                curveTo(15.2374f, 15.8232f, 14.7625f, 15.8232f, 14.4696f, 15.5303f)
                lineTo(12.0f, 13.0606f)
                lineTo(9.5303f, 15.5303f)
                curveTo(9.2374f, 15.8232f, 8.7626f, 15.8232f, 8.4697f, 15.5303f)
                curveTo(8.1768f, 15.2374f, 8.1768f, 14.7625f, 8.4697f, 14.4696f)
                lineTo(10.9393f, 12.0f)
                lineTo(8.4697f, 9.5303f)
                curveTo(8.1768f, 9.2374f, 8.1768f, 8.7626f, 8.4697f, 8.4697f)
                close()
            }
        }
            .build()
        return _error!!
    }

private var _error: ImageVector? = null
