package net.xenyria.xenon.core

/**
 * Defines the rotation mode for an object in the editor.
 */
enum class RotationMode(val supportedAxes: Set<Axis>) {
    /**
     * Rotation around all three axes (X, Y, Z) using Euler angles.
     * (This is mostly intended to be used for display entities)
     */
    EULER(setOf(Axis.X, Axis.Y, Axis.Z)),

    /**
     * Rotation around the Y axis (yaw) and X axis (pitch) only, using a yaw-pitch rotation system.
     * (This is mostly intended to be used for regular entities / NPCs)
     */
    YAW_PITCH(setOf(Axis.X, Axis.Y));
}