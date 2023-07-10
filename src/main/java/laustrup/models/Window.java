package laustrup.models;

import laustrup.utilities.console.Printer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Contains values for the window of the program, such as width and height.
 * Can initiate and loop the window.
 * Has a singleton value of the same object.
 */
@ToString(of = {"_width","_height"})
public class Window {

    /** The singleton value of the Window instance. */
    private static Window _instance;

    /** The width size of the screen. */
    @Getter @Setter
    private int _width;

    /** The height size of the screen. */
    @Getter @Setter
    private int _height;

    /**
     * The title that will show in the top of the window.
     * Is at the moment final as CreeThor.
     */
    @Getter
    private final String _title = "CreeThor";

    /**
     * The created window of glfw.
     * Has a long value assigned as an id.
     * Needs to be initiated.
     */
    @Getter
    private long _glfw;

    /**
     * A constructor for the singleton.
     * Sets the width and height into default values of HD 1920 - 1080 resolution.
     */
    private Window() {
        _width = 1920;
        _height = 1080;
    }

    /**
     * if the singleton instance is null, it will create it.
     * @return The singleton instance.
     */
    public static Window get_instance() {
        if (Window._instance == null)
            Window._instance = new Window();

        return _instance;
    }

    /**
     * First initializes the window,
     * afterwards opens it and loops.
     */
    public void run() {
        init();
        open();
        loop();
    }

    /**
     * Sets up the GLFW with hints and openGL.
     * Can throw errors that stops the program, if it is not properly set up.
     */
    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();


        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW...");

        configureGLFW();

        _glfw = glfwCreateWindow(_width, _height, _title, NULL, NULL);
        if (_glfw == NULL)
            throw new IllegalStateException("Failed to create the GLFW");
        else
            Printer.get_instance().print(
                    """
                        Window has been created with the values:
                        
                        width = $width
                        height = $height"""
                .replace("$width",String.valueOf(_width))
                .replace("$height",String.valueOf(_height))
            );

        configureOpenGL();
    }

    /** If the glfw isn't NULL, it will open the window and createCapabilities for GL. */
    public void open() {
        if (_glfw != NULL) {
            glfwShowWindow(_glfw);
            GL.createCapabilities();
        }
    }

    /** Configures the glfw with hints for the window. */
    private void configureGLFW() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
    }

    /** Configures the openGL with context and swapInterval that is of v-sync. */
    private void configureOpenGL() {
        glfwMakeContextCurrent(_glfw);
        glfwSwapInterval(1); // v-sync
    }

    /**
     * Keeps looping while the window should be open.
     * Polls events.
     * Then the window actions are acted and buffers will be swapped.
     */
    private void loop() {
        while (!glfwWindowShouldClose(_glfw)) {
            glfwPollEvents();

            glClearColor(1.0f,0.0f,0.0f,1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            glfwSwapBuffers(_glfw);
        }
    }
}
