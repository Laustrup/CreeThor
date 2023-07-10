package laustrup;

import laustrup.models.Window;

public class Program {

    public static void main(String[] args) {
        Window window = Window.get_instance();
        window.run();
    }
}
