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
 * There is a distinction between so-called strong checkpoints and weak checkpoints.
 * Strong checkpoints guarantee that the state of the stream is restored to a 
 * particular condition whenever they are used - that is, you should use them when
 * you want to guarantee that a stream is not modified.
 * 
 * Strong checkpoints are returned by the strongCheckpoint() method, and you should
 * always check if they need to be restored (via needsRestore()) before you call
 * restore() on the stream.
 * 
 * Weak checkpoints are used when you want the stream to change if a parse succeeds,
 * or if you want the stream not to change when it fails. Weak checkpoints are used
 * internally, and you cannot get any access to them from the outside.
 * 
 * There are four extra methods:
 * 
 *  - checkpoint() creates a weak checkpoint.
 *  - strongCheckpoint() creates a strong checkpoint
 *  - restore() causes stream to be restored back to the time when checkpoint()
 *    was last called. It also cleans up the most recent checkpoint.
 *  - commit() cleans up the most recent checkpoint without applying it.
 *    
 * Note that, since checkpoints are designed for reading, any operations 
 * which add to the stream will invalidate all checkpoints  
 */
public class CheckpointStream implements Iterable<Character> {
    private class Checkpoint implements StrongCheckpoint {
        private Deque<Character> checkpoint; 
        private boolean isStrong;
        private boolean wasRestored = false;
        
        public Checkpoint(boolean strong) {
            checkpoint = new ArrayDeque<Character>();
            isStrong = strong;
        }

        public boolean isStrong() { return isStrong; }

        public void record(char c) {
            checkpoint.addFirst(c);
        }

        public void restore(Deque<Character> stream) {
            while (!checkpoint.isEmpty()) {
                Character elt = checkpoint.removeFirst();
                stream.addFirst(elt);
            }
            
            wasRestored = true;
        }
        
        /*
         * Useful for strong checkpoints when the checkpoint below the strong one
         * was restored (and thus the strong checkpoint need not be restored)
         * but the stream doesn't need the strong checkpoint to do anything.
         */
        public void forceRestore() {
            wasRestored = true;
        }
        
        public boolean needsRestore() {
            return isStrong() && !wasRestored;
        }
    }
    
    private Deque<Character> backingStore;
    private LinkedList<Checkpoint> checkpoints;
    
    public CheckpointStream() {
        backingStore = new ArrayDeque<Character>();
        checkpoints = new LinkedList<Checkpoint>();
    }
    
    /**
     * Gets the current checkpoint.
     */
    private Optional<Checkpoint> currentCheckpoint() {
        if (checkpoints.isEmpty()) return Optional.empty();
        else                       return Optional.of(checkpoints.getFirst());
    }
    
    /**
     * Weak checkpoints save only the characters which
     * are removed when they are the topmost checkpoint.
     */
    public void checkpoint() {
        checkpoints.addFirst(new Checkpoint(false));
    }
    
    /**
     * Strong checkpoints save *all* characters which are removed until they are
     * restored or committed.
     */
    public StrongCheckpoint strongCheckpoint() {
        checkpoints.addFirst(new Checkpoint(true));
        return checkpoints.getFirst();
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
        
        Checkpoint checkpoint = currentCheckpoint().get();
        checkpoints.removeFirst();
        checkpoint.restore(backingStore);
        
        // Also, if there are strong checkpoints on top of this, then discard 
        // them so that they don't double-up their effects
        while (!checkpoints.isEmpty() && checkpoints.getFirst().isStrong()) {
            checkpoints.removeFirst().forceRestore();
        }
    }
    
    /**
     * Returns true if a checkpoint is currently in use.
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
        
        int checkpointIndex = 0;
        for (Checkpoint checkpoint: checkpoints) {
            if (checkpointIndex == 0 || checkpoint.isStrong()) {
                checkpoint.record(result);
            }
            checkpointIndex++;
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
