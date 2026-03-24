package net.xenyria.xenon.forklift.editor.state.rotate

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.core.RotationMode
import net.xenyria.xenon.core.getVectorComponent
import net.xenyria.xenon.forklift.config.ForkliftConfig
import net.xenyria.xenon.forklift.render.roundToNearestMultiple
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3f

fun RotationMode.create(params: RotationModeParams): IRotationMode {
    return when (this) {
        RotationMode.EULER -> EulerRotationMode(params)
        RotationMode.YAW_PITCH -> YawPitchRotationMode(params)
    }
}

data class RotationModeParams(
    val initialRotation: Vector3d,
    val config: ForkliftConfig,
    val axis: Axis,
    val updateRotation: (Vector3d) -> Unit
)

abstract class IRotationMode(params: RotationModeParams) {

    protected val config: ForkliftConfig = params.config
    protected val axis: Axis = params.axis
    protected val updateRotation: (Vector3d) -> Unit = params.updateRotation
    protected val previousObjectRotation = Vector3d(params.initialRotation)

    abstract val mode: RotationMode
    abstract fun rotate(displacement: Double, snapToNearest: Boolean)

    fun getSnapValue(): Double {
        return config.rotationGridSnap
    }

    fun getPreviousRotation(axis: Axis): Double {
        return getVectorComponent(axis, previousObjectRotation)
    }

    fun getNewRotation(axis: Axis, snapToNearest: Boolean): Double {
        return getVectorComponent(axis, getEffectiveNewLocalRotation(snapToNearest)).toDouble()
    }

    fun getEffectiveRotation(snapToNearest: Boolean): Vector3d {
        return Vector3d(getEffectiveNewLocalRotation(snapToNearest)).sub(previousObjectRotation)
    }

    abstract fun getEffectiveNewLocalRotation(snapToNearest: Boolean): Vector3f
}

class YawPitchRotationMode(params: RotationModeParams) : IRotationMode(params) {
    override val mode: RotationMode = RotationMode.YAW_PITCH
    private var _newLocalRotation = Vector3f(params.initialRotation)

    override fun rotate(displacement: Double, snapToNearest: Boolean) {
        if (axis == Axis.X) {
            _newLocalRotation.x += displacement.toFloat()
        } else if (axis == Axis.Y) {
            _newLocalRotation.y -= displacement.toFloat()
        }
        val rotation = Vector3d(_newLocalRotation)
        if (snapToNearest)
            rotation.set(roundToNearestMultiple(Vector3d(rotation), getSnapValue(), axis))
        updateRotation(rotation)

    }

    override fun getEffectiveNewLocalRotation(snapToNearest: Boolean): Vector3f {
        var rotation = Vector3d(_newLocalRotation)
        if (snapToNearest)
            rotation = Vector3d(roundToNearestMultiple(rotation, getSnapValue(), axis))
        return Vector3f(rotation.x.toFloat(), -rotation.y.toFloat(), rotation.z.toFloat())
    }

}

class EulerRotationMode(params: RotationModeParams) : IRotationMode(params) {

    private var _quaternion: Quaternionf = Quaternionf()
    private var _newLocalRotation = Vector3f(params.initialRotation)

    override fun getEffectiveNewLocalRotation(snapToNearest: Boolean): Vector3f {
        var rotation = Vector3d(_newLocalRotation)
        if (snapToNearest)
            rotation = Vector3d(roundToNearestMultiple(rotation, getSnapValue(), axis))
        return Vector3f(rotation.x.toFloat(), rotation.y.toFloat(), rotation.z.toFloat())
    }

    override fun rotate(displacement: Double, snapToNearest: Boolean) {
        var displacement = displacement
        if (axis == Axis.X) displacement *= -1.0F

        when (axis) {
            Axis.X -> {
                _quaternion.rotateLocalX(Math.toRadians(displacement).toFloat())
                _newLocalRotation.add(displacement.toFloat(), 0.0F, 0.0F)
            }

            Axis.Y -> {
                _quaternion.rotateLocalY(Math.toRadians(displacement).toFloat())
                _newLocalRotation.add(0.0F, displacement.toFloat(), 0.0F)
            }

            Axis.Z -> {
                _quaternion.rotateLocalZ(Math.toRadians(displacement).toFloat())
                _newLocalRotation.add(0.0F, 0.0F, displacement.toFloat())
            }
        }

        val buffer = Vector3f()
        _quaternion.getEulerAnglesYXZ(buffer)

        var degrees = Vector3d(
            Math.toDegrees(buffer.x.toDouble()),
            Math.toDegrees(buffer.y.toDouble()),
            Math.toDegrees(buffer.z.toDouble())
        )
        if (snapToNearest) degrees = Vector3d(roundToNearestMultiple(degrees, getSnapValue()))
        updateRotation(degrees)
    }

    override val mode: RotationMode = RotationMode.EULER

    init {
        _quaternion.rotateY(Math.toRadians(params.initialRotation.y).toFloat())
        _quaternion.rotateX(Math.toRadians(params.initialRotation.x).toFloat())
        _quaternion.rotateZ(Math.toRadians(params.initialRotation.z).toFloat())
    }
}