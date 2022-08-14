class NoShape : Shape {
    override fun getArea(): Double {
        throw IllegalStateException("Undefined shape has no area")
    }
}