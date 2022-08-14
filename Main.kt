fun main(args: Array<String>) {
    val areaCalculator = AreaCalculatorImpl()

    // Passing the areaCalculator to the constructor
    // of Printer class for calculations
    val printer = Printer(areaCalculator)

    val rectangle = Rectangle(
        width = 10.toDouble(),
        height = 20.toDouble()
    )
    val square = Square(length = 10.toDouble())
    val circle = Circle(radius = 12.toDouble())
    val cube = Cube(edge = 6.toDouble())

    val sum = printer.getSumAsJson(square, circle, rectangle, cube)

    println(sum)
}