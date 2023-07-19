package laustrup.utilities;

public class TimeUtility {

    private static TimeUtility _instance;

    public static TimeUtility get_instance() {
        if (TimeUtility._instance == null)
            _instance = new TimeUtility();

        return _instance;
    }

    private TimeUtility() {}

    public static final float _startOfApplication = System.nanoTime();

    public static float get_time() {
        return (float)((System.nanoTime() - _startOfApplication) * 1E-9);
    }
}
