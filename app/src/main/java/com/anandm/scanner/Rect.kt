package com.anandm.scanner

internal class Rect(val left: Int, val top: Int, val right: Int, val bottom: Int) {
    override fun hashCode(): Int {
        return 31 * (31 * (31 * left + top) + right) + bottom
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj === this) {
            true
        } else if (obj is Rect) {
            val other = obj
            left == other.left && top == other.top && right == other.right && bottom == other.bottom
        } else {
            false
        }
    }

    override fun toString(): String {
        return "[(" + left + "; " + top + ") - (" + right + "; " + bottom + ")]"
    }
}