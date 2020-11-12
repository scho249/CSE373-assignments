package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 1;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 8;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 8;
    private int size;
    private int chainCapacity;
    private int chainCount;
    private double lambda;
    /*
    Warning:
    DO NOT rename this `chains` field or change its type.
    We will be inspecting it in our Gradescope-only tests.

    An explanation of this field:
    - `chains` is the main array where you're going to store all of your data (see the [] square bracket notation)
    - The other part of the type is the AbstractIterableMap<K, V> -- this is saying that `chains` will be an
    array that can store an AbstractIterableMap<K, V> object at each index.
       - AbstractIterableMap represents an abstract/generalized Map. The ArrayMap you wrote in the earlier part
       of this project qualifies as one, as it extends the AbstractIterableMap class.  This means you can
       and should be creating ArrayMap objects to go inside your `chains` array as necessary. See the instructions on
       the website for diagrams and more details.
        (To jump to its details, middle-click or control/command-click on AbstractIterableMap below)
     */
    AbstractIterableMap<K, V>[] chains;

    // You're encouraged to add extra fields (and helper methods) though!

    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        this.chains = createArrayOfChains(initialChainCount);
        this.chainCount = initialChainCount;
        this.chainCapacity = chainInitialCapacity;
        this.lambda = resizingLoadFactorThreshold;
        this.size = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     *
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     *
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {
        int index = getIndex(key);
        if (this.chains[index] != null && this.chains[index].containsKey(key)) {
            return this.chains[index].get(key);
        }
        return null;
    }

    //private helper method getIndex
    private int getIndex(Object key) {
        if (key == null) {
            return 0;
        } else {
            int hashCode = key.hashCode();
            if (hashCode < 0) {
                hashCode = hashCode*-1;
            }
            if (hashCode > this.chains.length - 1) {
                hashCode %= this.chains.length;
            }
            return hashCode;
        }
    }

    @Override
    public V put(K key, V value) {
        int index = getIndex(key);
        if (this.chains[index] == null) {
            this.chains[index] = createChain(chainCapacity);
        }
        V val = this.chains[index].put(key, value);
        if (val == null) {
            size++;
        }
        if ((double) size / (double) this.chains.length > lambda) {
            resize();
        }
        return val;
    }

    private void resize() {
        AbstractIterableMap<K, V>[] newChains = createArrayOfChains(2 * chains.length);
        for (Entry<K, V> entries : this) {
            int newIndex = Math.abs(entries.getKey().hashCode()) % newChains.length;
            if (newChains[newIndex] == null) {
                newChains[newIndex] = createChain(chainCapacity);
            }
            newChains[newIndex].put(entries.getKey(), entries.getValue());

        }
        this.chains = newChains;
    }

    @Override
    public V remove(Object key) {
        int index = getIndex(key);
        if (this.chains[index] == null || !this.chains[index].containsKey(key)) {
            return null;
        } else {
            V val = chains[index].remove(key);
            if (this.chains[index].size() == 0) {
                this.chains[index] = null;
            }
            this.size--;
            return val;
        }

    }
    @Override
    public void clear() {
        this.chains = createArrayOfChains(DEFAULT_INITIAL_CHAIN_COUNT);
        this.size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        int index = getIndex(key);
        if (this.chains[index] != null) {
            return this.chains[index].containsKey(key);
        } else {
            return false;
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }

    // Doing so will give you a better string representation for assertion errors the debugger.
    @Override
    public String toString() {
        return super.toString();
    }

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private AbstractIterableMap<K, V>[] chains;
        // You may add more fields and constructor parameters
        private int current;
        private Iterator<Entry<K, V>> iterator;
        private int arrCurr;
        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) {
            this.chains = chains;
            this.current = 0;
            this.iterator = null;
            this.arrCurr = 0;
        }

        @Override
        public boolean hasNext() {

            while (current < this.chains.length) {
                if (this.chains[current] == null) {
                    current++;
                    arrCurr = 0;
                } else if (this.chains[current] != null && arrCurr == 0) {
                    iterator = this.chains[current].iterator();
                    arrCurr++;
                    return iterator.hasNext();
                } else {
                    if (iterator.hasNext()) {
                        return iterator.hasNext();
                    } else {
                        current++;
                        arrCurr = 0;
                    }
                }
            }
            return false;
        }




        @Override
        public Map.Entry<K, V> next() {
            if (hasNext()) {
                return iterator.next();
            }
            throw new NoSuchElementException();

        }
    }
}
