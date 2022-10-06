package priorityqueues;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * @see ExtrinsicMinPQ
 */
public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    // IMPORTANT: Do not rename these fields or change their visibility.
    // We access these during grading to test your code.
    static final int START_INDEX = 0;
    List<PriorityNode<T>> items;
    int size;

    HashMap<T, Integer> data;

    public ArrayHeapMinPQ() {
        items = new ArrayList<>();
        data = new HashMap<>();
        size = 0;
    }

    // Here's a method stub that may be useful. Feel free to change or remove it, if you wish.
    // You'll probably want to add more helper methods like this one to make your code easier to read.
    /**
     * A helper method for swapping the items at two indices of the array heap.
     */
    private void swap(int a, int b) {
        PriorityNode<T> aCopy= items.get(a);
        PriorityNode<T> bCopy= items.get(b);

        items.set(a, bCopy);
        items.set(b, aCopy);

        data.replace(aCopy.getItem(), b);
        data.replace(bCopy.getItem(), a);
    }

    @Override
    public void add(T item, double priority) {
        if (contains(item)) {
            throw new IllegalArgumentException();
        }
        items.add(new PriorityNode<>(item, priority));
        size = size + 1;

        int currIndex = size - 1;
        int parentIndex = (currIndex - 1)/2;
        while (currIndex > 0 && items.get(currIndex).getPriority() < items.get(parentIndex).getPriority()) {
            swap(currIndex, parentIndex);
            currIndex = parentIndex;
            parentIndex = (currIndex - 1)/2;
        }
        data.put(item, currIndex);
    }

    @Override
    public boolean contains(T item) {
        if (size == 0) {
            return false;
        }
        return (data.containsKey(item));
    }

    @Override
    public T peekMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return items.get(0).getItem();
    }

    @Override
    public T removeMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        T removeT = items.get(0).getItem();
        items.set(0, items.get(size - 1));
        items.remove(size - 1);
        size = size - 1;
        percolateDown(0);
        data.remove(removeT);
        return removeT;
    }

    @Override
    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException();
        }
        int currIndex = data.get(item);
        items.get(currIndex).setPriority(priority);

        percolateUp(currIndex);
        percolateDown(currIndex);
    }

    private void percolateDown(int currIndex) {
        int minIndex = currIndex;
        int leftIndex = 2 * currIndex + 1;
        int rightIndex = 2 * currIndex + 2;
        if (leftIndex <= size - 1 && items.get(leftIndex).getPriority() < items.get(minIndex).getPriority()) {
            minIndex = leftIndex;
        }
        if (rightIndex <= size - 1 && items.get(rightIndex).getPriority() < items.get(minIndex).getPriority()) {
            minIndex = rightIndex;
        }
        if (minIndex != currIndex) {
            swap(currIndex, minIndex);
            percolateDown(minIndex);
        }
    }

    private void percolateUp(int currIndex) {
        int parentIndex = (currIndex - 1)/2;
        while (items.get(currIndex).getPriority() < items.get(parentIndex).getPriority()) {
            swap(currIndex, parentIndex);
            currIndex = parentIndex;
            parentIndex = (currIndex - 1)/2;
        }
    }

    @Override
    public int size() {
        return size;
    }
}
