package net.kryos.render;

import org.joml.Matrix3x2fc;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.gui.navigation.ScreenRectangle;

public record PreciseScreenRectangle(float x, float y, float width, float height) {

    public static final PreciseScreenRectangle EMPTY = new PreciseScreenRectangle(0f, 0f, 0f, 0f);

    public static PreciseScreenRectangle empty() {
        return EMPTY;
    }

    public float left() {
        return x;
    }

    public float right() {
        return x + width;
    }

    public float top() {
        return y;
    }

    public float bottom() {
        return y + height;
    }

    public boolean containsPoint(float px, float py) {
        return px >= left() && px < right() && py >= top() && py < bottom();
    }

    public boolean overlaps(PreciseScreenRectangle other) {
        return this.left() < other.right() &&
               this.right() > other.left() &&
               this.top() < other.bottom() &&
               this.bottom() > other.top();
    }

    @Nullable
    public PreciseScreenRectangle intersection(PreciseScreenRectangle other) {
        float left = Math.max(this.left(), other.left());
        float top = Math.max(this.top(), other.top());
        float right = Math.min(this.right(), other.right());
        float bottom = Math.min(this.bottom(), other.bottom());

        if (left < right && top < bottom) {
            return new PreciseScreenRectangle(left, top, right - left, bottom - top);
        }
        return null;
    }

    public boolean encompasses(PreciseScreenRectangle other) {
        return other.left() >= this.left() &&
               other.top() >= this.top() &&
               other.right() <= this.right() &&
               other.bottom() <= this.bottom();
    }

    public float centerX() {
        return (left() + right()) * 0.5f;
    }

    public float centerY() {
        return (top() + bottom()) * 0.5f;
    }

    public PreciseScreenRectangle transformAxisAligned(Matrix3x2fc matrix) {
        Vector2f topLeft = matrix.transformPosition(left(), top(), new Vector2f());
        Vector2f bottomRight = matrix.transformPosition(right(), bottom(), new Vector2f());

        return new PreciseScreenRectangle(
                topLeft.x,
                topLeft.y,
                bottomRight.x - topLeft.x,
                bottomRight.y - topLeft.y
        );
    }

    public PreciseScreenRectangle transformMaxBounds(Matrix3x2fc matrix) {
        Vector2f tl = matrix.transformPosition(left(), top(), new Vector2f());
        Vector2f tr = matrix.transformPosition(right(), top(), new Vector2f());
        Vector2f bl = matrix.transformPosition(left(), bottom(), new Vector2f());
        Vector2f br = matrix.transformPosition(right(), bottom(), new Vector2f());

        float minX = Math.min(Math.min(tl.x, tr.x), Math.min(bl.x, br.x));
        float maxX = Math.max(Math.max(tl.x, tr.x), Math.max(bl.x, br.x));
        float minY = Math.min(Math.min(tl.y, tr.y), Math.min(bl.y, br.y));
        float maxY = Math.max(Math.max(tl.y, tr.y), Math.max(bl.y, br.y));

        return new PreciseScreenRectangle(
                minX,
                minY,
                maxX - minX,
                maxY - minY
        );
    }
    
    public ScreenRectangle toScreenRectangle() {
        return new ScreenRectangle(
            Math.round(x),
            Math.round(y),
            Math.round(width),
            Math.round(height)
        );
    }
}