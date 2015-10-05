package org.adamnew123456.source2html.test;
import org.adamnew123456.source2html.syntax.parsing.CheckpointStream;

import static org.junit.Assert.*;
import org.junit.Test;

public class CheckpointStreamTest {
    private static String START_STRING = "Hello, World";
    
    private void assertStreamEquals(String message, CheckpointStream stream, String text) {
        assertEquals(message, stream.toString(), text);
    }
    
    private CheckpointStream emptyStream() {
        return new CheckpointStream();
    }
    
    private CheckpointStream newStream() {
        CheckpointStream stream = new CheckpointStream();
        stream.extend(START_STRING);
        return stream;
    }
    
    @Test
    public void testEmptyStream() {
        CheckpointStream empty = emptyStream();
        assertStreamEquals("Empty stream is not empty", empty, "");
        
        assertTrue(empty.isEmpty());
    }
    
    @Test
    public void testAppend() {
        CheckpointStream empty = emptyStream();
        empty.append('c');
        
        assertStreamEquals("Appending does not work", empty, "c");
        assertFalse("Stream is empty despite appending", empty.isEmpty());
    }
    
    @Test
    public void testCheckpointAppend() {
        CheckpointStream empty = emptyStream();
        empty.checkpoint();
        empty.append('c');
        
        assertFalse("Append did not clear checkpoints", empty.isCheckpointed());
    }
    
    @Test
    public void testExtend() {
        CheckpointStream nonempty = newStream();
        assertStreamEquals("Extend does not work", nonempty, START_STRING); 
    }
    
    @Test
    public void testCheckpointExtend() {
        CheckpointStream empty = emptyStream();
        empty.checkpoint();
        empty.extend(START_STRING);
        
        assertFalse("Extending did not clear checkpoints", empty.isCheckpointed());
    }
    
    @Test
    public void testGetters() {
        CheckpointStream nonempty = newStream();
        
        assertEquals(nonempty.size(), START_STRING.length());
        
        // Peek is idempotent - you can run it twice without affecting the stream
        assertEquals("1st peek doesn't return 'H'",
                nonempty.peek(), new Character('H'));
        
        assertEquals("2nd peek returns different value than first peek",
                nonempty.peek(), new Character('H'));
        
        assertEquals("Peek altered size",
                nonempty.size(), START_STRING.length());
        
        // Get is not idempotent - it modifies the stream each time it runs
        assertEquals("1st get doesn't return 'H'",
                nonempty.get(), new Character('H'));
        
        assertEquals("2nd get doesn't return 'e'",
                nonempty.get(), new Character('e'));
        
        assertEquals("Get doesn't decrease size by 2",
                nonempty.size(), START_STRING.length() - 2);
    }
    
    @Test
    public void testCheckpointCommit() {
        CheckpointStream nonempty = newStream();
        
        nonempty.checkpoint();
        nonempty.get();
        nonempty.get();
        assertStreamEquals("Two gets didn't produce correct stream",
                nonempty, START_STRING.substring(2));
        
        nonempty.commit();
        assertStreamEquals("Commit changed the stream",
                nonempty, START_STRING.substring(2));
        
        assertFalse("Stream still checkpoined after commit", 
                nonempty.isCheckpointed());
    }
    
    @Test
    public void testCheckpointRestore() {
        CheckpointStream nonempty = newStream();
        
        nonempty.checkpoint();
        nonempty.get();
        nonempty.get();
        assertStreamEquals("Two gets didn't produce correct stream",
                nonempty, START_STRING.substring(2));
        
        nonempty.restore();
        assertStreamEquals("Restore didn't revert stream to original state",
                nonempty, START_STRING);
        
        assertFalse("Stream still checkpointed after restore",
                nonempty.isCheckpointed());
    }
    
    @Test
    public void testNestedCheckpoints() {
        CheckpointStream nonempty = newStream();
        
        nonempty.checkpoint();
        nonempty.get();
        nonempty.get();
        assertStreamEquals("Two gets didn't produce correct stream",
                nonempty, START_STRING.substring(2));
        
        nonempty.checkpoint();
        nonempty.get();
        nonempty.get();
        assertStreamEquals("Two more gets didn't produce correct stream",
                nonempty, START_STRING.substring(4));
        
        nonempty.restore();
        assertStreamEquals("Nested restore didn't restore stream",
                nonempty, START_STRING.substring(2));
        assertTrue("Nested restore removed all checkpoints",
                nonempty.isCheckpointed());
        
        nonempty.commit();
        assertStreamEquals("Commit changed stream",
                nonempty, START_STRING.substring(2));
        assertFalse("Commit didn't remove all checkpoints",
                nonempty.isCheckpointed());
    }
}
