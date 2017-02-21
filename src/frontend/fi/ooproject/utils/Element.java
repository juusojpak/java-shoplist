package fi.ooproject.utils;

/**
 * Class representing an element in linked list.
 *
 * @author Juuso Pakarinen
 * @version 2016.1011
 * @since 1.8
 */
public class Element<T> {

    /**
     * Content stored in element.
     */
    private T content;

    /**
     * Reference to element one step behind in linked list.
     */
    private Element<T> next;

    /**
     * Returns content stored in element.
     *
     * @return content stored in element.
     */
    public T getContent() {
        return content;
    }

    /**
     * Returns the element one step behind in linked list.
     *
     * @return the element one step behind in linked list.
     */
    public Element<T> getNext() {
        return next;
    }

    /**
     * Sets given content to element.
     *
     * @param content given content to be stored.
     */
    public void setContent(T content) {
        this.content = content;
    }

    /**
     * Sets given element as {@link Element#next}.
     *
     * @param next given element.
     */
    public void setNext(Element<T> next) {
        this.next = next;
    }

    /**
     * Constructor.
     *
     * @param content content stored in element.
     * @param next reference to element one step behind in linked list.
     */
    public Element(T content, Element<T> next) {
        this.content = content;
        this.next = next;
    }
}
