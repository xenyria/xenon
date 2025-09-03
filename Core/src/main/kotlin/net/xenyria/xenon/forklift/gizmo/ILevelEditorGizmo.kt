/*
 * Copyright (c) 2025 Pixelground Labs - All Rights Reserved.
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 */
package net.xenyria.xenon.forklift.gizmo

import net.xenyria.xenon.core.Axis
import net.xenyria.xenon.core.IVec3D
import net.xenyria.xenon.forklift.TransformationMode
import java.util.*

interface ILevelEditorGizmo {

    val maxRenderDistance: Int

    val uuid: UUID

    val objectPosition: IVec3D

    val objectRotation: IVec3D

    val objectScale: IVec3D

    val allowedModes: Set<TransformationMode>

    val allowedRotationAxes: Set<Axis>
        get() {
            return setOf(Axis.X, Axis.Y, Axis.Z)
        }
}
