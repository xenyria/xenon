package net.xenyria.xenon.shape

import net.xenyria.xenon.shape.impl.BoxShape
import net.xenyria.xenon.shape.impl.BoxShapeProperties
import org.joml.Vector3d
import java.awt.Color
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class ShapeIOTest {

    @Test
    fun testBoxShapeSerialization() {
        val box = BoxShape(
            "testBox", Vector3d(1.0, 2.0, 3.0),
            BoxShapeProperties(
                dimensions = Vector3d(3.0, 4.0, 5.5),
                boxColor = Color(25, 50, 75, 200),
                outlineColor = Color(50, 25, 12, 5),
                visibleThroughWalls = true,
                onlyRenderOutline = false,
                centerTextVertically = true
            ), listOf("testLine1", "testLine2"), "someGroup"
        )

        val output = ByteArrayOutputStream()
        val outStream = DataOutputStream(output)
        box.serialize(outStream)

        val newBox = BoxShape()
        newBox.deserialize(DataInputStream(ByteArrayInputStream(output.toByteArray())))

        assertEquals(box, newBox)
    }

}