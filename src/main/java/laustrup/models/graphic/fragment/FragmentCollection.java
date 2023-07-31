package laustrup.models.graphic.fragment;

import laustrup.utilities.collections.lists.Liszt;

import lombok.Getter;
import lombok.ToString;

import java.util.function.Supplier;

/**
 * A collection of Fragment objects.
 * Also contains some method to be used.
 */
@ToString
public class FragmentCollection {

    /** The Collection of fragments of a Liszt Utility. */
    @Getter
    private Liszt<Fragment> _fragments;

    /**
     * Initiates the fragments of the input into this Collection.
     * @param fragments The fragments the be initiated into this Collection.
     */
    public FragmentCollection(Liszt<Fragment> fragments) {
        _fragments = fragments;
    }

    /**
     * Initiates the fragments of the input into this Collection.
     * @param fragments The fragments the be initiated into this Collection.
     */
    public FragmentCollection(Fragment[] fragments) {
        this(new Liszt<>(fragments));
    }

    /**
     * Counts the amount of nodes in all fragments.
     * @return The calculated amount.
     */
    public int amount() {
        return new Supplier<Integer>() {
            @Override
            public Integer get() {
                int length = 0;

                for (Fragment fragment : _fragments)
                    length += fragment.get_nodes().length;

                return length;
            }
        }.get();
    }
}
