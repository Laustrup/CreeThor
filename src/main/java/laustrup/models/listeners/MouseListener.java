package laustrup.models.listeners;

import laustrup.utilities.console.Printer;

import lombok.Getter;
import lombok.ToString;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Listens to the actions of the mouse.
 * Makes callbacks from the actions, that will have an impact of values and prints the impacts.
 * Is a singleton class.
 */
@ToString(of = {"_x", "_y", "_clicked"})
public class MouseListener {

    /** The single instance of MouseListener. */
    private static MouseListener _instance;

    /** Scroll value of mouse horizontally of the x-axis. */
    @Getter
    private double _scrollX;

    /** Scroll value of mouse vertically of the y-axis. */
    @Getter
    private double _scrollY;

    /** The current x coordinate value of the mouse. */
    @Getter
    private double _x;

    /** The current y coordinate value of the mouse. */
    @Getter
    private double _y;

    /** The previous x coordinate value of the mouse. */
    @Getter
    private double _dx;

    /** The previous y coordinate value of the mouse. */
    @Getter
    private double _dy;

    /**
     * Have three different button values, where for each one, if they are true, they have been clicked, otherwise not.
     */
    private boolean _clicked[] = new boolean[3];

    /**
     * Determines if one of the buttons have been clicked.
     * @param button The button index that should be determined.
     * @return True if the indexed button is pressed, otherwise not.
     */
    public static boolean is_clicked(int button) {
        return button < get_instance()._clicked.length && get_instance()._clicked[button];
    }

    /** True if the mouse is being dragged, otherwise false. */
    @Getter
    private boolean _dragging;

    /**
     * A constructor for the singleton.
     * Initializes all x and y values into 0.
     */
    private MouseListener() {
        _scrollX = 0.0;
        _scrollY = 0.0;
        _x = 0.0;
        _y = 0.0;
        _dx = 0.0;
        _dy = 0.0;
    }

    /**
     * if the singleton instance is null, it will create it.
     * @return The singleton instance.
     */
    private static MouseListener get_instance() {
        if (MouseListener._instance == null)
            MouseListener._instance = new MouseListener();

        return MouseListener._instance;
    }

    /**
     * A callback function, that will be activated, when there is a change in the position of the mouse.
     * If the mouse is dragging, then it will print it.
     * @param window The window that the cursor are on top of.
     * @param x The new x value of the cursor.
     * @param y The new y value of the cursor.
     */
    public static void positionCallback(long window, double x, double y) {
        setLastPositions();
        setNewPositions(x,y);
        get_instance()._dragging = calculateDragging();
        if (get_instance().is_dragging())
            Printer.get_instance().print("Mouse is dragging");
    }

    /**
     * Determine if the mouse is been dragged.
     * @return True, if the determination is true.
     */
    private static boolean calculateDragging() {
        for (boolean click : get_instance()._clicked)
            if (click)
                return true;

        return false;
    }

    /**
     * A callback function, that will be called, when there is a change with the buttons of the mouse.
     * Prints the action of the button.
     * @param window The window where the cursor is on top of.
     * @param button The button index that is having a action.
     * @param action The action of the button.
     * @param mods A modifier, that can modify the action of the button.
     */
    public static void buttonCallback(long window, int button, int action, int mods) {
        boolean isClicked = action == GLFW_PRESS && button < get_instance()._clicked.length,
                isReleased = action == GLFW_RELEASE;

        get_instance()._clicked[button] = isClicked;
        if (action == GLFW_RELEASE)
            get_instance()._dragging = false;

        Printer.get_instance().print(glfwGetMouseButton(window,button) + " is " + (isClicked ? "clicked" : (isReleased ? "released" : "unknown action")));
    }

    /**
     * A callback function, that will be called, when there is a change with the scroll of the mouse.
     * Prints the new scroll values.
     * @param window The window where the cursor is on top of.
     * @param xOffset The change of the x-axis.
     * @param yOffset The change of the y-axis.
     */
    public static void scrollCallback(long window, double xOffset, double yOffset) {
        get_instance()._scrollX = xOffset;
        get_instance()._scrollY = yOffset;
//        Printer.get_instance().print("""
//                Scroll values are:
//
//                x = $x
//                y = $y"""
//                .replace("$x",String.valueOf(get_instance()._scrollX))
//                .replace("$Y",String.valueOf(get_instance()._scrollY))
//        );
    }

    /**
     * When the window ends, values of the mouse will have an effect.
     * Prints the configuration has been changed.
     */
    public static void endwindow() {
        get_instance()._scrollX = 0;
        get_instance()._scrollY = 0;
        get_instance()._dx = get_instance()._x;
        get_instance()._dy = get_instance()._y;
        Printer.get_instance().print("Mouse has had its configuration of the window ending!");
    }

    /**
     * Will change to previous positions of x and y into dx and dy.
     */
    private static void setLastPositions() {
        get_instance()._dx = get_instance()._x;
        get_instance()._dy = get_instance()._y;
    }

    /**
     * Sets the current x and y into the input values.
     * Will print the new and previous values.
     * @param x The new x value.
     * @param y The new y value.
     */
    private static void setNewPositions(double x, double y) {
        get_instance()._x = x;
        get_instance()._y = y;
//        Printer.get_instance().print("""
//                New positions are:
//
//                x = $x
//                y = $y
//                dx = $dx
//                dy = $dy"""
//                .replace("$x",String.valueOf(get_instance()._x))
//                .replace("$y",String.valueOf(get_instance()._y))
//                .replace("$dx",String.valueOf(get_instance()._dx))
//                .replace("$dy",String.valueOf(get_instance()._dy))
//        );
    }
}
