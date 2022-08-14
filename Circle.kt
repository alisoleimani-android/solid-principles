import kotlin.math.PI
import kotlin.math.pow

data class Circle(private val radius: Double) : Shape {

    override fun getArea(): Double = PI * radius.pow(2)
}