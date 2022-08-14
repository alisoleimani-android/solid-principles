/**
 * This class is responsible for calculating
 * of the area of 2D shapes.
 */
class AreaCalculatorImpl : AreaCalculator {

    override fun sum(vararg shapes: Shape): Double {
        var sum = 0.00
        shapes.forEach { shape ->
            sum += shape.getArea()
        }
        return sum
    }
}