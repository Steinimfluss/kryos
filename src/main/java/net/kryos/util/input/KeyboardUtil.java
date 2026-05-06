package net.kryos.util.input;

public class KeyboardUtil {
	public static String getName(int key) {
		if(key == -1) return "";
		
	    String name = org.lwjgl.glfw.GLFW.glfwGetKeyName(key, 0);
	    if (name != null && !name.isEmpty()) {
	        return name.toUpperCase();
	    }

	    return switch (key) {
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT -> "LShift";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT -> "RShift";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL -> "LCtrl";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCtrl";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT -> "LAlt";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_ALT -> "RAlt";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SUPER -> "LWin";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SUPER -> "RWin";

	        case org.lwjgl.glfw.GLFW.GLFW_KEY_UP -> "Up";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN -> "Down";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT -> "Left";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT -> "Right";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_HOME -> "Home";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_END -> "End";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_UP -> "PageUp";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_DOWN -> "PageDown";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_INSERT -> "Insert";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE -> "Delete";

	        case org.lwjgl.glfw.GLFW.GLFW_KEY_F1 -> "F1";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_F2 -> "F2";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_F3 -> "F3";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_F4 -> "F4";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_F5 -> "F5";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_F6 -> "F6";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_F7 -> "F7";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_F8 -> "F8";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_F9 -> "F9";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_F10 -> "F10";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_F11 -> "F11";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_F12 -> "F12";

	        case org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE -> "Backspace";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER -> "Enter";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_TAB -> "Tab";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE -> "Esc";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE -> "Space";

	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_0 -> "Num0";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_1 -> "Num1";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_2 -> "Num2";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_3 -> "Num3";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_4 -> "Num4";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_5 -> "Num5";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_6 -> "Num6";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_7 -> "Num7";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_8 -> "Num8";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_9 -> "Num9";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ADD -> "Num+";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_SUBTRACT -> "Num-";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_MULTIPLY -> "Num*";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DIVIDE -> "Num/";
	        case org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DECIMAL -> "Num.";

	        default -> "Key " + key;
	    };
	}
}