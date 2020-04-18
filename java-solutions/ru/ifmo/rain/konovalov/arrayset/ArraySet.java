package ru.ifmo.rain.konovalov.arrayset;

import java.util.*;
import java.util.NavigableSet;

import static java.lang.Integer.min;

public class ArraySet<T> extends AbstractSet<T> implements NavigableSet<T> {
    private final List<T> data;
    private final Comparator<? super T> compare;

    private ArraySet(List<T> data, Comparator<? super T> compare) {
        this.data = data;
        this.compare = compare;
    }

    public ArraySet(Collection<T> collection, Comparator<? super T> compare) {
        this.compare = compare;
        this.data = sortSet(new ArrayList<>(collection));
    }

    public ArraySet(Collection<T> collection) {
        this(collection, null);
    }

    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    private List<T> sortSet(ArrayList<T> list) {
        list.sort(compare);
        Comparator<? super T> comparator = compare != null ? compare : (T a, T b) -> a == b ? 0 : 1;

        int leftIndex = 0;
        for (int rightIndex = 1; rightIndex < list.size(); ++rightIndex)
            if (comparator.compare(list.get(leftIndex), list.get(rightIndex)) != 0)
                list.set(++leftIndex, list.get(rightIndex));

        return List.copyOf(list.subList(0, min(++leftIndex, list.size())));
    }

    private NavigableSet<T> subSet(int fromIndex, int toIndex) {
        if (fromIndex >= toIndex)
            return new ArraySet<>(Collections.emptyList(), compare);
        return new ArraySet<>(data.subList(fromIndex, toIndex), compare);
    }

    private int indexOfEq(T t, int find, int noFind) {
        int x = Collections.binarySearch(data, t, compare);
        if (x < 0)
            return -(x + 1) + noFind;
        return x + find;
    }

    private int indexOf(T t) {
        return indexOfEq(t, 0, 0);
    }

    private int indexOfEquals(T t, int delta) {
        return indexOfEq(t, delta, 0);
    }

    @Override
    public Comparator<? super T> comparator() {
        return compare;
    }

    @Override
    public SortedSet<T> subSet(T t, T e1) {
        return subSet(t, true, e1, false);
    }

    @Override
    public SortedSet<T> headSet(T t) {
        return headSet(t, false);
    }

    @Override
    public SortedSet<T> tailSet(T t) {
        return tailSet(t, true);
    }

    @Override
    public T first() {
        if (data.isEmpty())
            throw new NoSuchElementException();
        return data.get(0);
    }

    @Override
    public T last() {
        if (data.isEmpty())
            throw new NoSuchElementException();
        return data.get(data.size() - 1);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked cast")
    public boolean contains(Object o) {
        if (data.isEmpty())
            return false;
        T elem = data.get(indexOf((T) o) % data.size());
        if (compare == null)
            return elem.equals(o);
        return compare.compare(elem, (T) o) == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NavigableSet<T> descendingSet() {
        ArrayList<T> local = new ArrayList<>(data);
        Collections.reverse(local);
        return new ArraySet<>(List.copyOf(local), compare != null ? compare.reversed() : Collections.reverseOrder());
    }

    @Override
    public Iterator<T> descendingIterator() {
        return descendingSet().iterator();
    }

    @Override
    public T lower(T t) {
        int x = indexOf(t) - 1;
        if (x < 0)
            return null;
        return data.get(x);
    }

    @Override
    public T floor(T t) {
        int x = indexOfEq(t, 0, -1);
        if (x < 0)
            return null;
        return data.get(x);
    }

    @Override
    public T ceiling(T t) {
        int x = indexOf(t);
        if (x >= data.size())
            return null;
        return data.get(x);
    }

    @Override
    public T higher(T t) {
        int x = indexOfEquals(t, 1);
        if (x >= data.size())
            return null;
        return data.get(x);
    }

    @Override
    public T pollFirst() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T pollLast() {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked cast")
    public NavigableSet<T> subSet(T t, boolean b, T e1, boolean b1) {
        if (compare != null) {
            if (compare.compare(t, e1) > 0)
                throw new IllegalArgumentException();
        } else if (((Comparable<? super T>) t).compareTo(e1) > 0)
            throw new IllegalArgumentException();
        return subSet(indexOfEquals(t, b ? 0 : 1), indexOfEquals(e1, b1 ? 1 : 0));
    }

    @Override
    public NavigableSet<T> headSet(T t, boolean b) {
        return subSet(0, indexOfEquals(t, b ? 1 : 0));
    }

    @Override
    public NavigableSet<T> tailSet(T t, boolean b) {
        return subSet(indexOfEquals(t, b ? 0 : 1), data.size());
    }
}