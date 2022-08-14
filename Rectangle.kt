data class Rectangle(
    private val width: Double,
    private val height: Double
) : Shape {
    override fun getArea(): Double = width * height
}