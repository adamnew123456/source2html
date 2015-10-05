package org.adamnew123456.source2html.syntax.parsing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;

/**
 * A checkpoint stream is a Deque which has the ability to save parts of its
 * input, and restore them. This is important for parsing, since tere are times
 * when a stream has to be reverted, even after characters are read (for example,
 * in a Sequence parser where only a few parsers succeeded, but one of them failed).
 * 
 * There are three extra methods:
 * 
 *  - checkpoint() causes the stream to save its current state so that it can be
 *    restored later. checkpoint() works in such a way that only characters which
 *    are consumed after the checkpoint() are saved, which makes it more efficient
 *    than just copying the whole stream.
 *  - restore() causes stream to be restored back to the time when checkpoint()
 *    was last called. It also cleans up the most recent checkpoint.
 *  - commit() cleans up the most recent checkpoint without applying it.
 *    
 * Note that, since checkpoints are designed for reading, any operations 
 * which add to the stream will invalidate all checkpoints  
 */
public class CheckpointStream implements Iterable<Character> {
    private Deque<Character> backingStore;
    private LinkedList<Deque<Character>> checkpoints;
    
    public CheckpointStream() {
        backingStore = new ArrayDeque<Character>();
        checkpoints = new LinkedList<Deque<Character>>();
    }
    
    /**
     * Gets the current checkpoint.
     */
    private Optional<Deque<Character>> currentCheckpoint() {
        if (checkpoints.isEmpty()) return Optional.empty();
        else                       return Optional.of(checkpoints.getFirst());
    }
    
    /**
     * Saves all the characters removed from the head of the stream, until the next
     */
    public void checkpoint() {
        checkpoints.addFirst(new ArrayDeque<Character>());
    }
    
    /**
     * Deletes the current checkpoint.
     */
    public void commit() {
        if (checkpoints.isEmpty()) {
            throw new IllegalStateException("Cannot commit with no checkpoints");
        }
        
        checkpoints.removeFirst();
    }
    
    /**
     * Restores the content of the checkpoint onto the stream.
     */
    public void restore() {
        if (checkpoints.isEmpty()) {
            throw new IllegalStateException("Cannot restore with no checkpoints");
        }
        
        Deque<Character> checkpoint = currentCheckpoint().get();
        checkpoints.removeFirst();
        
        while (!checkpoint.isEmpty()) {
            Character elt = checkpoint.removeFirst();
            backingStore.addFirst(elt);
        }
    }
    
    /**
     * Retruns true if a checkpoint is currently in use.
     */
    public boolean isCheckpointed() {
        return !checkpoints.isEmpty();
    }
    
    /**
     * Returns true if the stream is empty.
     */
    public boolean isEmpty() {
        return backingStore.isEmpty();
    }

    /**
     * Adds an element to the back of the stream.
     */
    public void append(Character e) {
        checkpoints.clear();
        backingStore.addLast(e);
    }
    
    /**
     * Adds all the elements to the back of the stream.
     */
    public void extend(String text) {
        checkpoints.clear();
        for (char c: text.toCharArray()) {
            append(c);
        }
    }

    /**
     * Gets the first element of the stream without removing it.
     */
    public Character peek() {
        return backingStore.getFirst();
    }

    /**
     * Gets an iterator which walks the elements of the stream.
     */
    public Iterator<Character> iterator() {
        return backingStore.iterator();
    }
    
    /**
     * Gets the next element from the stream and removes it.
     */
    public Character get() {
        Character result = backingStore.remove();
        
        Optional<Deque<Character>> checkpoint = currentCheckpoint();
        if (checkpoint.isPresent()) {
            checkpoint.get().addFirst(result);
        }
        
        return result;
    }
    
    /**
     * Returns the number of elements left in the stream.
     */
    public int size() {
        return backingStore.size();
    }
    
    /**
     * Creates a string with all the elements of the stream as characters
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (char c: this) {
            builder.append(c);
        }
        return builder.toString();
    }
}
