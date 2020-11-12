package priorityqueues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class ArrayHeapMinPQ<T extends Comparable<T>> implements ExtrinsicMinPQ<T> {
    // IMPORTANT: Do not rename these fields or change their visibility.
    // We access these during grading to test your code.
    static final int START_INDEX = 1;
    List<PriorityNode<T>> items;
    int size;
    int initialCap;
    Map<T, Integer> dict;


    public ArrayHeapMinPQ() {
        items = new ArrayList<>(20);
        items.add(null);
        size = 0;
        initialCap = 20;
        dict = new HashMap<>();
    }

    // Here's a method stub that may be useful. Feel free to change or remove it, if you wish.
    // You'll probably want to add more helper methods like this one to make your code easier to read.

    /**
     * A helper method for swapping the items at two indices of the array heap.
     */
    private void swap(int a, int b) {
        // PriorityNode<T> temp = items.get(a);
        // items.set(a, items.get(b));
        // items.set(b, temp);
        // dict.put(items.get(a).getItem(), a);
        // dict.put(items.get(b).getItem(), b);

        PriorityNode<T> temp1 = items.get(a);
        PriorityNode<T> temp2 = items.get(b);
        items.set(a, temp2);
        items.set(b, temp1);
        dict.put(temp1.getItem(), b);
        dict.put(temp2.getItem(), a);
    }

    private void resize() {
        initialCap = 2 * initialCap;
        List<PriorityNode<T>> newItems = new ArrayList<>(initialCap);
        for (int i = 0; i < items.size(); i++) {
            newItems.add(items.get(i));
        }
        items = newItems;
    }



    @Override
    public void add(T item, double priority) {
        if (item == null || dict.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        if (size >= items.size()) {
            resize();
        }
        size++;
        items.add(new PriorityNode<>(item, priority));
        dict.put(item, size);
        percolateUp(size);
    }

    private void percolateUp(int i) {
        while (i > 1 && items.get(i/2).getPriority() > items.get(i).getPriority()) {
            swap(i, (i/2));
            i = i/2;
        }
    }

    @Override
    public boolean contains(T item) {
        return dict.containsKey(item);
    }

    @Override
    public T peekMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return items.get(START_INDEX).getItem();
    }

    @Override
    public T removeMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        T min = peekMin();
        swap(START_INDEX, size);
        items.remove(size);
        dict.remove(min);
        size--;
        percolateDown(START_INDEX);

        return min;
    }
    private void percolateDown(int i) {
        int left = 2 * i;
        int right = 2 * i + 1;
        if (left <= size && right > size) {
            if (items.get(i).getPriority() >= items.get(left).getPriority()) {
                swap(i, left);
                percolateDown(left);
            }
        } else if (right <= size) {
            if (items.get(i).getPriority() >= items.get(left).getPriority()
                || items.get(i).getPriority() >= items.get(right).getPriority()) {
                if (items.get(left).getPriority() < items.get(right).getPriority()) {
                    swap(i, left);
                    percolateDown(left);
                } else {
                    swap(i, right);
                    percolateDown(right);
                }
            }
        }


    }
    @Override
    public void changePriority(T item, double priority) {
        if (!dict.containsKey(item)) {
            throw new NoSuchElementException();
        }
        int index = dict.get(item);
        double old = items.get(index).getPriority();
        items.get(index).setPriority(priority);
        if (items.get(index).getPriority() > old) {
            percolateDown(index);
        } else {
            percolateUp(index);
        }
        // percolateDown(index);
        // percolateUp();
        //throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public int size() {
        return size;
    }
}
