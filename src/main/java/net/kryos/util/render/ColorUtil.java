package net.kryos.util.render;

public class ColorUtil {
	public static int lerpColor(int colorA, int colorB, double t) {
	    t = Math.max(0, Math.min(1, t));

	    int a1 = (colorA >> 24) & 0xFF;
	    int r1 = (colorA >> 16) & 0xFF;
	    int g1 = (colorA >> 8)  & 0xFF;
	    int b1 =  colorA        & 0xFF;

	    int a2 = (colorB >> 24) & 0xFF;
	    int r2 = (colorB >> 16) & 0xFF;
	    int g2 = (colorB >> 8)  & 0xFF;
	    int b2 =  colorB        & 0xFF;

	    int a = (int)(a1 + (a2 - a1) * t);
	    int r = (int)(r1 + (r2 - r1) * t);
	    int g = (int)(g1 + (g2 - g1) * t);
	    int b = (int)(b1 + (b2 - b1) * t);

	    return (a << 24) | (r << 16) | (g << 8) | b;
	}
	
	public static float[] unpackColor(int color) {
	    float a = ((color >> 24) & 0xFF) / 255f;
	    float r = ((color >> 16) & 0xFF) / 255f;
	    float g = ((color >> 8)  & 0xFF) / 255f;
	    float b = ( color        & 0xFF) / 255f;
	    return new float[]{r, g, b, a};
	}
}