package net.xenyria.xenon.core

enum class RotationMode(val supportedAxes: Set<Axis>) {
    EULER(setOf(Axis.X, Axis.Y, Axis.Z)),
    YAW_PITCH(setOf(Axis.X, Axis.Y));
}