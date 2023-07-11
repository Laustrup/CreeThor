package laustrup.models.listeners;

import laustrup.utilities.console.Printer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Listens to the actions of the keys.
 * Makes callbacks from the actions, that will have an impact of values and prints the impacts.
 * Is a singleton class.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KeyListener {

    /** The single instance of KeyListener. */
    private static KeyListener _instance;

    /** An array of all the possible keys available for actions. */
    private boolean keyPressed[] = new boolean[350];

    /**
     * Determine if a specific key has been pressed.
     * Can throw a ArrayIndexOutOfBoundsException, if the key is not recognised.
     * @param key The index of the key, that will be determined.
     * @return True, if the indexed key has been pressed, otherwise not.
     */
    public static boolean isKeyPressed(int key) {
        if (key >= get_instance().keyPressed.length)
            throw new ArrayIndexOutOfBoundsException("The input of key that is pressed is not recognised as a key...");

        return get_instance().keyPressed[key];
    }

    /**
     * if the singleton instance is null, it will create it.
     * @return The singleton instance.
     */
    public static KeyListener get_instance() {
        if (KeyListener._instance == null)
            KeyListener._instance = new KeyListener();

        return KeyListener._instance;
    }

    /**
     * A callback function, that will be activated, when there is a change with one of the keys.
     * @param frame The frame that will be affected.
     * @param key The key that is changing effect.
     * @param scancode Can be used to scan the key.
     * @param action The action of the key.
     * @param mods Can modify the action.
     */
    public static void keyCallback(long frame, int key, int scancode, int action, int mods) {
        get_instance().keyPressed[key] = action == GLFW_PRESS;
        Printer.get_instance().print(
                glfwGetKeyName(key,scancode) + " is " +
                (action == GLFW_PRESS
                        ? "pressed"
                        : (action == GLFW_RELEASE
                            ? "released"
                            : "unknown action")
                )
        );
    }
}
