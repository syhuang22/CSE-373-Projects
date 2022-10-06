package disjointsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A quick-union-by-size data structure with path compression.
 * @see DisjointSets for more documentation.
 */
public class UnionBySizeCompressingDisjointSets<T> implements DisjointSets<T> {
    // Do NOT rename or delete this field. We will be inspecting it directly in our private tests.
    List<Integer> pointers;

    HashMap<T, Integer> ids;

    /*
    However, feel free to add more fields and private helper methods. You will probably need to
    add one or two more fields in order to successfully implement this class.
    */

    public UnionBySizeCompressingDisjointSets() {
        pointers = new ArrayList<>();
        ids = new HashMap<>();
    }

    @Override
    public void makeSet(T item) {
        ids.put(item, pointers.size());
        pointers.add(-1);
    }

    @Override
    public int findSet(T item) {
        Integer index = ids.get(item);
        if (index == null) {
            throw new IllegalArgumentException();
        }
        if (!ids.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        List<Integer> comp = new ArrayList<>();

        while (pointers.get(index) >= 0) {
            comp.add(index);
            index = pointers.get(index);
        }
        for (int targetIndex : comp) {
            pointers.set(targetIndex, index);
            pointers.set(index, pointers.get(index) - 1);
        }
        return index;
    }

    @Override
    public boolean union(T item1, T item2) {
        int index1 = findSet(item1);
        int index2 = findSet(item2);
        if (index1 == index2) {
            return false;
        }
        if (Math.abs(pointers.get(index1)) >= Math.abs(pointers.get(index2))) {
            int count = pointers.get(index2);
            pointers.set(index2, index1);
            pointers.set(index1, pointers.get(index1) + count);

        } else {
            int count = pointers.get(index1);
            pointers.set(index1, index2);
            pointers.set(index2, pointers.get(index2) + count);
        }
        return true;
    }
}
