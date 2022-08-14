import kotlin.math.pow

data class Square(
    private val length: Double
) : Shape {
    override fun getArea(): Double = length.pow(2)
}