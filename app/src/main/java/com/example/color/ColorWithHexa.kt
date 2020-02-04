package com.example.color

class ColorWithHexa(
    val color : Color = Color.WHITE,
    val hexa : Int = 100
) {

    override fun toString(): String {
        return "$color ($hexa)"
    }

}