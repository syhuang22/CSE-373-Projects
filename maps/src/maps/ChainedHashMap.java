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
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 3;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 3;

    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    AbstractIterableMap<K, V>[] chains;

    double loadFactor;

    int n; //#key-value pairs
    int c; //hash table capacity

    int arrayCap;



    // You're encouraged to add extra fields (and helper methods) though!

    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        chains = createArrayOfChains(initialChainCount); //the initial number of chains for your hash table
        loadFactor = resizingLoadFactorThreshold;
        c = initialChainCount;
        n = 0;

        arrayCap = chainInitialCapacity;
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
        int index = 0;
        if (key == null) {
            index = Math.abs(0 % c);
        } else {
            index = Math.abs(key.hashCode() % c);
        }
        if (n == 0 || chains[index] == null) {
            return null;
        }
        if (chains[index].containsKey(key)) {
            return chains[index].get(key);
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        V oldV = null;
        if (!containsKey(key) && ((double) n + 1)/c >= loadFactor) {
            resize();
        }
        int index = 0;
        if (key == null) {
            index = Math.abs(0 % c);
        } else {
            index = Math.abs(key.hashCode() % c);
        }
        if (chains[index] == null) {
            chains[index] = createChain(arrayCap);
        }
        if (containsKey(key)) {
            oldV = chains[index].put(key, value);
        } else {
            chains[index].put(key, value);
            n = n + 1;
        }
        return oldV;
    }

    private void resize() {
        c = 2 * c;
        int newIndex = 0;
        AbstractIterableMap<K, V>[] newChains = createArrayOfChains(c);

        for (int i = 0; i < c/2; i++) {
            if (chains[i] != null) {
                Iterator<Map.Entry<K, V>> itr = chains[i].iterator();
                while (itr.hasNext()) {
                    Map.Entry<K, V> newEntry = itr.next();
                    if (newEntry.getKey() == null) {
                        newIndex = Math.abs(0 % c);
                    } else {
                        newIndex = Math.abs(newEntry.getKey().hashCode() % c);
                    }
                    if (newChains[newIndex] != null) {
                        newChains[newIndex].put(newEntry.getKey(), newEntry.getValue());
                    } else {
                        AbstractIterableMap<K, V> newMap = createChain(arrayCap);
                        newMap.put(newEntry.getKey(), newEntry.getValue());
                        newChains[newIndex] = newMap;
                    }
                }
            }
        }
        chains = newChains;
    }

    @Override
    public V remove(Object key) {
        V oldV = null;
        int index = 0;
        if (key == null) {
            index = Math.abs(0 % c);
        } else {
            index = Math.abs(key.hashCode() % c);
        }
        if (containsKey(key)) {
            n = n - 1;
            oldV = chains[index].remove(key);
            if (chains[index].isEmpty()) {
                chains[index] = null;
            }
        }
        return oldV;
    }

    @Override
    public void clear() {
        chains = createArrayOfChains(c);
        n = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (n == 0) {
            return false;
        }
        int index = 0;
        if (key == null) {
            index = Math.abs(0 % c);
        } else {
            index = Math.abs(key.hashCode() % c);
        }
        return (chains[index] != null && chains[index].containsKey(key));
    }

    @Override
    public int size() {
        return n;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains);
    }

    // Doing so will give you a better string representation for assertion errors the debugger.
    // @Override
    // public String toString() {
    //     return super.toString();
    // }

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private AbstractIterableMap<K, V>[] chains;

        private int tableIndex;
        private Iterator<Map.Entry<K, V>> itr;

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains) {
            this.chains = chains;
            tableIndex = 0;
            itr = null;

        }

        @Override
        public boolean hasNext() {
            if (itr == null) {
                while (tableIndex < chains.length - 1 && chains[tableIndex] == null) {
                    tableIndex = tableIndex + 1;
                }
                if (chains[tableIndex] == null) {
                    return false;
                }
                itr = chains[tableIndex].iterator();
            }
            return itr.hasNext();
        }

        @Override
        public Map.Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Map.Entry<K, V> entry = itr.next();
            if (!itr.hasNext() && tableIndex < chains.length - 1) {
                tableIndex = tableIndex + 1;
                while (tableIndex < chains.length - 1 && chains[tableIndex] == null) {
                    tableIndex = tableIndex + 1;
                }
                if (chains[tableIndex] != null) {
                    itr = chains[tableIndex].iterator();
                }
            }
            return entry;
        }
    }
}
