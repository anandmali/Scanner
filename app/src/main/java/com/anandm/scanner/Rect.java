package com.anandm.scanner;

final class Rect {
    private final int mLeft;
    private final int mTop;
    private final int mRight;
    private final int mBottom;

    public Rect(final int left, final int top, final int right, final int bottom) {
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
    }

    public int getLeft() {
        return mLeft;
    }

    public int getTop() {
        return mTop;
    }

    public int getRight() {
        return mRight;
    }

    public int getBottom() {
        return mBottom;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * (31 * mLeft + mTop) + mRight) + mBottom;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Rect) {
            final Rect other = (Rect) obj;
            return mLeft == other.mLeft && mTop == other.mTop && mRight == other.mRight &&
                    mBottom == other.mBottom;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "[(" + mLeft + "; " + mTop + ") - (" + mRight + "; " + mBottom + ")]";
    }
}
