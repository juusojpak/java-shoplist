package fi.ooproject.utils;

/**
 * Own implementation of linked list.
 *
 * @author Juuso Pakarinen
 * @version 2016.1011
 * @since 1.8
 */
public class MyLinkedList<T> implements MyList {

    /**
     * Last appended {@link Element element}.
     *
     * The element that was appended last is first in link. All elements
     * contains reference to the element next to them in link.
     */
    private Element<T> first;

    /**
     * Number of elements in the list.
     */
    private int size;

    /**
     * Constructor.
     */
    public MyLinkedList() {
        first = null;
        size = 0;
    }

    /**
     * Adds a new element containing given object to the list.
     *
     * @param e given object.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void add(Object e) {

        T t = (T) e;

        if (first == null) {
            first = new Element<>(t, null);
        } else {
            first = new Element<>(t, first);
        }

        size++;
    }

    /**
     * Clears the list.
     */
    @Override
    public void clear() {
        first = null;
        size = 0;
    }

    /**
     * Returns content of an element found at queried index in the list.
     *
     * @param index queried index.
     * @return content of found element.
     * @throws IndexOutOfBoundsException if index is out of list's bounds.
     */
    @Override
    public Object get(int index) throws IndexOutOfBoundsException {

        if (index <= size) {

            if (index == 0) {
                return first.getContent();
            } else {

                Element<T> cursor = first;

                for (int i = 0; i <= index; i++) {

                    if (i == index) {
                        return cursor.getContent();
                    }

                    cursor = cursor.getNext();
                }
            }
        } else {
            throw new IndexOutOfBoundsException();
        }

        return null;
    }

    /**
     * Returns whether the list is empty.
     *
     * @return whether the list is empty.
     */
    @Override
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * Removes an element found at queried index in the list.
     *
     * Removes an element by placing the element in removed element's
     * {@link Element#next 'next'} as 'next' of the element one step ahead
     * from removed element in link.
     *
     * @param index queried index.
     * @return removed element as Object.
     * @throws IndexOutOfBoundsException if index is out of list's bounds.
     */
    @Override
    public Object remove(int index) throws IndexOutOfBoundsException {

        if (index <= size) {
            if (index == 0) {
                Element<T> removed = first;
                first = first.getNext();
                size--;
                return removed;
            } else {
                Element<T> cursor = first;

                for (int i = 0; i <= index; i++) {

                    if (i == index - 1) {
                        Element<T> removed = cursor.getNext();
                        cursor.setNext(removed.getNext());
                        size--;
                        return removed;
                    }

                    cursor = cursor.getNext();
                }
            }
        } else {
            throw new IndexOutOfBoundsException();
        }

        return null;
    }

    /**
     * Removes the element given as argument.
     *
     * @param o element to be removed.
     * @return whether given element was found and removed.
     */
    @Override
    public boolean remove(Object o) {

        boolean found = false;
        int index = 0;

        for (int i = 0; i < size; i++) {
            if (get(i).equals(o)) {
                found = true;
                index = i;
                break;
            }
        }

        if (found) {
            for (int i = 0; i <= index; i++) {
                if (index == 0) {
                    first = first.getNext();
                    size--;
                    return true;
                } else {
                    Element<T> cursor = first;

                    for (int j = 0; j <= index; j++) {

                        if (j == index - 1) {
                            Element<T> removed = cursor.getNext();
                            cursor.setNext(removed.getNext());
                            size--;
                            return true;
                        }

                        cursor = cursor.getNext();
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns the number of elements in the list.
     *
     * @return number of elements in the list.
     */
    @Override
    public int size() {
        return size;
    }
}
