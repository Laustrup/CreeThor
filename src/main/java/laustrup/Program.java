package laustrup;

import laustrup.models.Window;
import lombok.Getter;

public class Program {

    @Getter
    private static String _path = "C:\\Users\\Laust\\IdeaProjects\\CreeThor";

    public static void main(String[] args) {
        Window window = Window.get_instance();
        window.run();
    }
}
