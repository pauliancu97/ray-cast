import com.soywiz.kds.*
import com.soywiz.kmem.*
import com.soywiz.korge.*
import com.soywiz.korge.scene.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.paint.*
import com.soywiz.korim.vector.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*
import kotlin.math.*

typealias Grid = IntArray2

private enum class Side {
    Vertical,
    Horizontal
}

data class Player(
    val position: Point,
    val direction: Point,
    val camera: Point
)

private fun drawScene(
    player: Player,
    grid: Grid,
    width: Int,
    height: Int,
    colors: List<RGBA>,
    shapeBuilder: ShapeBuilder
) {
    for (x in 0 until width) {
        val cameraMultiplier = 2.0 * x / width - 1.0
        val ray = player.direction + player.camera * cameraMultiplier
        val deltaDistance = Point(
            x = if (ray.x != 0.0) 1.0 / kotlin.math.abs(ray.x) else Double.MAX_VALUE,
            y = if (ray.y != 0.0) 1.0 / kotlin.math.abs(ray.y) else Double.MAX_VALUE
        )
        val step = PointInt(
            x = ray.x.sign.toInt(),
            y = ray.y.sign.toInt()
        )
        val marginX = if (step.x >= 0) {
            player.position.x.toIntCeil() - player.position.x
        } else {
            player.position.x - player.position.x.toIntFloor()
        }
        val marginY = if (step.y >= 0) {
            player.position.y.toIntCeil() - player.position.y
        } else {
            player.position.y - player.position.y.toIntFloor()
        }
        val sideStep = Point(
            x = deltaDistance.x * marginX,
            y = deltaDistance.y * marginY
        )
        val mapPoint = PointInt(x = player.position.x.toIntCeil(), y = player.position.y.toIntCeil())
        var side = Side.Vertical
        var isHit = false
        if (x == 25) {
            print(x)
        }
        while (!isHit) {
            if (sideStep.x < sideStep.y) {
                sideStep.x += deltaDistance.x
                mapPoint.x += step.x
                side = Side.Vertical
            } else {
                sideStep.y += deltaDistance.y
                mapPoint.y += step.y
                side = Side.Horizontal
            }
            isHit = if (mapPoint.x in 0 until grid.width && mapPoint.y in 0 until grid.height) {
                grid[mapPoint.y, mapPoint.x] != 0
            } else {
                true
            }
        }
        val distanceToWall = when (side) {
            Side.Vertical -> sideStep.x - deltaDistance.x
            Side.Horizontal -> sideStep.y - deltaDistance.y
        }
        val wallHeight = height.toDouble() / distanceToWall
        val tempColor = colors[grid[mapPoint.y, mapPoint.x] - 1]
        val color = when (side) {
            Side.Vertical -> tempColor
            Side.Horizontal -> tempColor
                .withR(tempColor.r / 2)
                .withG(tempColor.g / 2)
                .withB(tempColor.b / 2)
        }
        with(shapeBuilder) {
            fill(color) {
                rect(
                    x = x,
                    y = max(height / 2 - wallHeight.toInt() / 2, 0),
                    width = 1,
                    height = wallHeight.toInt()
                )
            }
        }
    }
}


private const val WIDTH = 640
private const val HEIGHT = 480

private val player = Player(
    position = Point(x = 22, y = 12),
    direction = Point(x = -1, y = 0),
    camera = Point(x = 0.0, y = 0.66)
)

private val grid = IntArray2(
    listOf(
        listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 0, 0, 0, 0, 3, 0, 3, 0, 3, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 2, 2, 0, 2, 2, 0, 0, 0, 0, 3, 0, 3, 0, 3, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 4, 0, 4, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 4, 0, 0, 0, 0, 5, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 4, 0, 4, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 4, 0, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        listOf(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)
    )
)

private val colors = listOf(
    Colors.RED,
    Colors.GREEN,
    Colors.BLUE,
    Colors.WHITE,
    Colors.YELLOW
)

suspend fun main() = Korge(width = WIDTH, height = HEIGHT, bgcolor = Colors.BLACK) {
    graphics {
        this@Korge.position(0, 0)
        drawScene(
            player,
            grid,
            WIDTH,
            HEIGHT,
            colors,
            this
        )
    }
}
