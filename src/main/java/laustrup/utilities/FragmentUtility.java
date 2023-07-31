package laustrup.utilities;

import laustrup.models.graphic.fragment.Fragment;
import laustrup.models.graphic.fragment.FragmentCollection;

public class FragmentUtility {

    public static int[] elementGraphicFormat(FragmentCollection collection) {
        int[] data = new int[collection.amount()];
        int elementIndex = 0;

        for (Fragment element : collection.get_fragments()) {
            for (int i = 0; i < element.get_nodes().length; i++) {
                data[elementIndex] = element.get_node(i);
                elementIndex++;
            }
        }

        return data;
    }
}
