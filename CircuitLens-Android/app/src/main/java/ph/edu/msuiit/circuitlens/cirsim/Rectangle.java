package ph.edu.msuiit.circuitlens.cirsim;

/**
 *  Custom Rectangle Class compatible with negative coordinates
 */
public class Rectangle {
    public int left;
    public int top;
    public int right;
    public int bottom;

    public void set(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int width() {
        return Math.abs(right - left);
    }

    public int height() {
        return Math.abs(top - bottom);
    }

    public boolean intersect(Rectangle rect) {
        return intersect(rect.left, rect.top, rect.right, rect.bottom);
    }

    public boolean contains(int x, int y) {
        return this.left <= x && this.top <= y
                && this.right >= x && this.bottom >= y;
    }

    /*
     * Copyright (C) 2006 The Android Open Source Project
     *
     * Licensed under the Apache License, Version 2.0 (the "License");
     * you may not use this file except in compliance with the License.
     * You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     * Unless required by applicable law or agreed to in writing, software
     * distributed under the License is distributed on an "AS IS" BASIS,
     * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     * See the License for the specific language governing permissions and
     * limitations under the License.
     */
    public boolean intersect(int left, int top, int right, int bottom) {
        if (this.left < right && left < this.right && this.top < bottom && top < this.bottom) {
            if (this.left < left) this.left = left;
            if (this.top < top) this.top = top;
            if (this.right > right) this.right = right;
            if (this.bottom > bottom) this.bottom = bottom;
            return true;
        }
        return false;
    }

    public boolean contains(int left, int top, int right, int bottom) {
        // check for empty first
        return this.left < this.right && this.top < this.bottom
                // now check for containment
                && this.left <= left && this.top <= top
                && this.right >= right && this.bottom >= bottom;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("Rectangle("); sb.append(left); sb.append(", ");
        sb.append(top); sb.append(") - ("); sb.append(right);
        sb.append(", "); sb.append(bottom); sb.append(")");
        return sb.toString();
    }
}
