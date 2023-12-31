package laustrup.models;

import laustrup.models.listeners.KeyListener;
import laustrup.models.listeners.MouseListener;
import laustrup.models.scenes.IScene;
import laustrup.models.scenes.LevelEditorScene;
import laustrup.models.scenes.LevelScene;
import laustrup.utilities.TimeUtility;
import laustrup.utilities.console.Printer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
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
    private long _window;

    /** The current scene of this window. */
    private static IScene _scene;

    private void set_scene(IScene scene) {
        _scene = scene;
        _scene.init();
    }

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
        freeMemoryAndTerminate();
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

        _window = glfwCreateWindow(_width, _height, _title, NULL, NULL);
        if (_window == NULL)
            throw new IllegalStateException("Failed to create the GLFW");
        else
            Printer.get_instance().print(
                    """
                        Window has been initiated with the values:
                        
                        width = $width
                        height = $height"""
                .replace("$width",String.valueOf(_width))
                .replace("$height",String.valueOf(_height))
            );

        configureListeners();
        configureOpenGL();
    }

    /** If the glfw isn't NULL, it will open the window and createCapabilities for GL. */
    public void open() {
        if (_window != NULL) {
            glfwShowWindow(_window);
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
        glfwMakeContextCurrent(_window);
        glfwSwapInterval(1); // v-sync
    }

    /**
     * Configures listeners of keys and mouse.
     * Will catch any auto collapse with a print.
     */
    private void configureListeners() {
        configureKeys();
        configureMouse();
    }

    /**
     * Configures keys from KeyListener.
     * Will catch any auto collapse with a print.
     */
    private void configureKeys() {
        try {
            glfwSetKeyCallback(_window, KeyListener::keyCallback);
        } catch (Exception e) {
            Printer.get_instance().print("Trouble configuring keys at window...",e);
        }
    }

    /**
     * Configures mouse from MouseListener.
     * Will catch any auto collapse with a print.
     */
    private void configureMouse() {
        try {
            glfwSetCursorPosCallback(_window, MouseListener::positionCallback);
            glfwSetMouseButtonCallback(_window, MouseListener::buttonCallback);
            glfwSetScrollCallback(_window, MouseListener::scrollCallback);
        }
        catch (Exception e) {
            Printer.get_instance().print("Trouble configuring mouse at window...",e);
        }
    }

    /**
     * Keeps looping while the window should be open.
     * Polls events.
     * Then the window actions are acted and buffers will be swapped.
     */
    private void loop() {
        float beginning = TimeUtility.get_time(),
              loopStart,
              dt = 0;
        set_scene(new LevelEditorScene());

        while (!glfwWindowShouldClose(_window)) {
            loopStart = TimeUtility.get_time();
            glfwPollEvents();

            glClearColor(1f,1f,1f,1f);
            glClear(GL_COLOR_BUFFER_BIT);

            _scene.update(dt);

            glfwSwapBuffers(_window);
            dt = TimeUtility.get_time() - loopStart;
        }

        Printer.get_instance().print("Loop of frame \"" + _title + "\" ended and lasted " + ((int) (TimeUtility.get_time() - beginning)) + " seconds.");
    }

    /** Will free the memory and terminate the whole window. */
    private void freeMemoryAndTerminate() {
        glfwFreeCallbacks(_window);
        glfwDestroyWindow(_window);
        glfwTerminate();
        glfwSetErrorCallback(null);
    }
}
