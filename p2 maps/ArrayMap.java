package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {

    private static final int DEFAULT_INITIAL_CAPACITY = 8;
    private int size;
    /*
    Warning:
    DO NOT rename this `entries` field or change its type.
    We will be inspecting it in our Gradescope-only tests.

    An explanation of this field:
    - `entries` is the array where you're going to store all of your data (see the [] square bracket notation)
    - The other part of the type is the SimpleEntry<K, V> -- this is saying that `entries` will be an
    array that can store a SimpleEntry<K, V> object at each index.
       - SimpleEntry represents an object containing a key and a value.
        (To jump to its details, middle-click or control/command-click on SimpleEntry below)

    */
    SimpleEntry<K, V>[] entries;

    // You may add extra fields or helper methods though!

    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public ArrayMap(int initialCapacity) {
        this.entries = this.createArrayOfEntries(initialCapacity);
        this.size = 0;
        //throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code Entry<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     */
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
        arrays and generics interact.
        */
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    @Override
    public V get(Object key) {
        //
        int index = getIndex(key);
        if (index == -1) {
            return null;
        } else {
            return this.entries[index].getValue();
        }

    }

    //private method getIndex
    private int getIndex(Object key) {
        for (int i = 0; i < this.size; i++) {
            if ((key == null && this.entries[i].getValue() != null) ||
                (key != null && this.entries[i].getKey().equals(key))) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public V put(K key, V value) {
        int index = getIndex(key);
        if (index != -1) {
            V val = this.entries[index].getValue();
            this.entries[index].setValue(value);
            return val;
        } else {
            resize();
            this.entries[size] = new SimpleEntry<>(key, value);
            this.size++;
            return null;
        }
    }

    //private method resize
    private void resize() {
        if (this.size == this.entries.length) {
            SimpleEntry<K, V>[] newEnt = createArrayOfEntries(this.entries.length * 2);
            for (int i = 0; i < this.entries.length; i++) {
                newEnt[i] = this.entries[i];
            }
            this.entries = newEnt;
        }
    }

    @Override
    public V remove(Object key) {
        int index = getIndex(key);
        if (index == -1) {     // if the key dn exist
            return null;
        } else {
            V val = this.entries[index].getValue();
            this.entries[index] = this.entries[size - 1];
            this.entries[size - 1] = null;
            this.size--;
            return val;
        }

    }

    @Override
    public void clear() {
        this.entries = createArrayOfEntries(DEFAULT_INITIAL_CAPACITY);
        this.size = 0;

    }

    @Override
    public boolean containsKey(Object key) {
        int index = getIndex(key);
        return !(index == -1);

    }

    @Override
    public int size() {
        return size;

    }


    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ArrayMapIterator<>(this.entries, this.size);
    }


    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        private int current;
        private int size;
        // You may add more fields and constructor parameters

        public ArrayMapIterator(SimpleEntry<K, V>[] entries, int size) {
            this.entries = entries;
            this.current = -1;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return size - current > 1;
        }

        @Override
        public Map.Entry<K, V> next() {
            if (size - current == 1) {
                throw new NoSuchElementException();
            } else {
                current++;
                return this.entries[current];
            }

        }
    }
}
