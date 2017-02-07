package fi.ooproject.utils;

/**
 * Interface for list implementations.
 *
 * @author Juuso Pakarinen
 * @version 2016.1011
 * @since 1.8
 */
public interface MyList {

    /**
     * Appends the specified object to the end of this list.
     *
     * @param e specified object.
     */
    void add(Object e);

    /**
     * Removes all objects from this list.
     */
    void clear();

    /**
     * Returns the object at the specified position in this list.
     *
     * @param index specified position.
     * @return object at the specified position in this list.
     */
    Object get(int index);

    /**
     * Returns whether this list contains no objects.
     *
     * @return whether this list contains no objects.
     */
    boolean isEmpty();

    /**
     * Removes the object from the list.
     *
     * Removes the object at the specified position in this list.
     * Returns the removed object.
     *
     * @param index specified position.
     * @return removed object.
     */
    Object remove(int index);

    /**
     * Removes the first occurrence of the specified object from this list.
     *
     * Returns whether object were found and removed.
     *
     * @param o specified object.
     * @return whether object were found and removed.
     */
    boolean remove(Object o);

    /**
     * Returns the number of objects in this list.
     *
     * @return the number of objects in this list.
     */
    int size();
}
