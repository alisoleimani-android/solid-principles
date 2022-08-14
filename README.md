# SOLID Principles

## Single Responsibility

Each class should have only one purpose and not be filled with excessive functionality.

Let's look at this AreaCalculator class below:

```kotlin
class AreaCalculator {

    fun sum(vararg shapes: Any): Double {
        var sum = 0.00
        shapes.forEach { shape ->
            when (shape) {
                is Square -> {
                    sum += shape.length.pow(2)
                }

                is Circle -> {
                    sum += PI * shape.radius.pow(2)
                }
            }
        }
        return sum
    }

    fun getSumAsJson(sum: String): String {
        return """
                {
                    sum: $sum
                }
            """.trimIndent()
    }

}
```

According to the name of this class, It's responsible for calculating the area of shapes passed to the **sum** function.

Defining the **print** function at the bottom of the class assigns another responsibility to this class which is conversion functionality. So it violates **Single Responsibility** principle.

To fix this violation, we need to define another class and call it **Printer**, which is responsible for printing values, and remove the **getSumAsJson** function from AreaCalculator class.

##### Solution :

```kotlin
class AreaCalculator {

    fun sum(vararg shapes: Any): Double {
        var sum = 0.00
        shapes.forEach { shape ->
            when (shape) {
                is Square -> {
                    sum += shape.length.pow(2)
                }

                is Circle -> {
                    sum += PI * shape.radius.pow(2)
                }
            }
        }
        return sum
    }

}
```

```kotlin
class Printer {

    fun getSumAsJson(sum: String): String {
        return """
                {
                    sum: $sum
                }
            """.trimIndent()
    }
}
```

## Open Closed

Imagine we want to add another class called **Rectangle** as a new shape to the project. We need to consider its area calculation within our AreaCalculation class like below:

```kotlin
data class Rectangle(
    val width: Double,
    val height: Double
)
```

```kotlin
class AreaCalculator {
    fun sum(vararg shapes: Any): Double {
        var sum = 0.00
        shapes.forEach { shape ->
            when (shape) {
                is Square -> {
                    sum += shape.length.pow(2)
                }

                is Circle -> {
                    sum += PI * shape.radius.pow(2)
                }

                is Rectangle -> { //---> Here we added the Rectangle
                    sum += shape.width * shape.height
                }
            }
        }
        return sum
    }
}
```

As you can see, due to the addition of Rectangle, we're modifying the body of the **sum** function, which is a bad practice. **You must not change the implementation of a function within a class** in case of adding a new feature to the project. Instead, it would be best if you defined an interface named **Shape**, and every new shape added to the project must implement it like below :

##### Solution :

1. Define a new interface for shapes

```kotlin
interface Shape {
    fun getArea(): Double
}
```

2. Shapes implement this interface

```kotlin
data class Square(
    private val length: Double
) : Shape {
    override fun getArea(): Double = length.pow(2)
}
```

```kotlin
data class Rectangle(
    private val width: Double,
    private val height: Double
) : Shape {
    override fun getArea(): Double = width * height
}
```

```kotlin
data class Circle(
    private val radius: Double
) : Shape {
    override fun getArea(): Double = PI * radius.pow(2)
}
```

3. Look at the beauty of using these shapes inside the AreaCalculator class

```kotlin
class AreaCalculator {

    // 1. Changed the argument from Any to Shape
    fun sum(vararg shapes: Shape): Double {
        var sum = 0.00

        shapes.forEach { shape ->
            // 2.Getting area of the shape
            sum += shape.getArea()
        }

        return sum
    }
}
```

So without modifying the body of the **sum** function within AreaCalculation class, you can pass any shape to this function for calculation.

This principle tells you :

**Classes should be open for extension and closed for modification. In other words, you should not have to rewrite an existing class to implement new features.**

## Liskov Substitution

Let's create a new shape called **NoShape** for the project. WHAT?!?

What kind of shape is that? You're right.

There's no shape around the world that people call it NoShape. But to understand this principle, I'm going to define it.

Let's do it together :

```kotlin
class NoShape : Shape {
    override fun getArea(): Double {
        throw IllegalStateException("Undefined shape has no area")
    }
}
```

Since the NoShape is the child class of Shape, whenever you create a new instance of this class with the type Shape and pass it to the sum function of AreaCalculator, you'll face an exception.

```kotlin
val areaCalculator = AreaCalculator()
val noShape: Shape = NoShape()
val sum = areaCalculator.sum(noShape) // It will throw an exception
```

So based on this example, the child class is not substitutable for its parent "Shape".

This principle tells you :

**Every child or derived class should be substitutable(replaceable) for their base or parent class.**

## Interface Segregation

Okay, let's move forward and learn another beautiful principle: **Interface Segregation**.
Now we want to create another shape within our project, a Cube class.
As you know, cubes have another function which we call volume. Let's add this volume function to the Shape interface and implement it within our shape classes, like below :

1. Add the getVolume function to our Shape interface :

```kotlin
interface Shape {
    fun getArea(): Double

    // Calculates and returns volume of 3D shapes
    fun getVolume(): Double
}
```

2. Implement this interface within our Cube class :

```kotlin
class Cube(
    private val edge: Double
) : Shape {

    override fun getArea(): Double = 6 * edge.pow(2)

    override fun getVolume(): Double = edge.pow(3)

}
```

3. So far, so good; now, let's also look at the Circle class because it also needs to implement the getVolume function.

```kotlin
data class Circle(
    private val radius: Double
) : Shape {

    override fun getArea(): Double = PI * radius.pow(2)

    // Circle is a 2D shape, then we need to return 0
    override fun getVolume(): Double {
        return 0.toDouble()
    }
}
```

As you know, a circle is a two-dimensional shape. So defining volume for it, is pointless. And being forced to have the getVolume function made it so ugly. Sometimes you may have to implement meaningless functions in your classes because you have used an interface. So it feels like you're doing something wrong! right?

Here, the Interface Segregation Principle comes into the picture!
This principle tells you :
**Interfaces should not force classes to implement what they can’t do. Large interfaces should be divided into small ones.**
So to fix this violation :

1. Create ThreeDimensionalShape interface and add the getVolume to it :

```kotlin
interface ThreeDimensionalShape {

    // Calculates and returns volume of 3D shapes
    fun getVolume(): Double
}
```

2. Remove the getVolume function from the Shape interface :

```kotlin
interface Shape {
    fun getArea(): Double
}
```

3. Implement the ThreeDimensionalShape inside the Cube class :

```kotlin
class Cube(private val edge: Double) : Shape, ThreeDimensionalShape {

    override fun getArea(): Double = 6 * edge.pow(2)

    override fun getVolume(): Double = edge.pow(3)

}
```

## Dependency Inversion

We want to change the getSumAsJson function within the Printer class, which receives the sum as a String in its arguments.
We want to pass the shapes to this function instead of a String, and inside the implementation of this function, use the AreaCalculator class to calculate the sum of the area of shapes, then convert it to JSON and return it.
So let's do it :

1. Change the Printer class :

```kotlin
// 1. Pass the AreaCalculator instance within the constructor
class Printer(private val areaCalculator: AreaCalculator) {

    // 2. Pass shapes instead of sum
    fun getSumAsJson(vararg shapes: Shape): String {

        // 3. Calculate sum of the shape by AreaCalculator
        val result = areaCalculator.sum(*shapes)

        return """
                {
                    sum: $result
                }
            """.trimIndent()
    }
}
```

2. Use it like below :

```kotlin
fun main(args: Array<String>) {
    val areaCalculator = AreaCalculator()

    // Passing the areaCalculator to the constructor
    // of Printer class for calculations
    val printer = Printer(areaCalculator)

    val rectangle = Rectangle(
        width = 10.toDouble(),
        height = 20.toDouble()
    )
    val square = Square(length = 10.toDouble())
    val circle = Circle(radius = 12.toDouble())

    val sum = printer.getSumAsJson(square, circle, rectangle)

    println(sum)
}
```

As you can see, the constructor of the Printer class accepts an instance from the AreaCalculator class. Imagine that you may have created countless instances from the Printer class in a massive project. Suppose one day you decide to create a new class called NewAreaCalculator and you want to use it within the Printer class. In that case, you will have to make numerous changes to your project to achieve this goal (because you need to pass an instance of this new class to the constructor of the Printer class everywhere). Sounds terrible?

Okay, here the Dependency Inversion comes into the picture. This principle tells you :
**Components should depend on abstractions, not on concretions.**
So let's fix this violation:

1. We need to define an interface called **AreaCalculator** :

```kotlin
interface AreaCalculator {
    fun sum(vararg shapes: Shape): Double
}
```

2. Implement the above interface within **AreaCalculatorImpl** :

```kotlin
class AreaCalculatorImpl : AreaCalculator {

    override fun sum(vararg shapes: Shape): Double {
        var sum = 0.00
        shapes.forEach { shape ->
            sum += shape.getArea()
        }
        return sum
    }
}
```

3. Within the Printer class, use the AreaCalculator interface instead of AreaCalculatorImpl (actual implementation) :

```kotlin
// Using AreaCalculator interface instead of the real implementation
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
```

Whenever you need to define a new class or implementation for AreaCalculator, you need to implement this interface and use it wherever you want without any trouble.

**Note :** What happens to your code by this principle is that it reduces the dependencies between classes and makes them decoupled. It also increases the testability of your classes.

## Conclusion

Stick to these principles because they help you to implement testable, maintainable, and reusable code.
