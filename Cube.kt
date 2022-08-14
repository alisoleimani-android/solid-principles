import kotlin.math.pow

class Cube(
    private val edge: Double
) : Shape, ThreeDimensionalShape {

    override fun getArea(): Double = 6 * edge.pow(2)

    override fun getVolume(): Double = edge.pow(3)

}
