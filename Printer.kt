class Printer(private val areaCalculator: AreaCalculator) {

    fun getSumAsJson(vararg shapes: Shape): String {

        val result = areaCalculator.sum(*shapes)

        return """
                {
                    sum: $result
                }
            """.trimIndent()
    }
}