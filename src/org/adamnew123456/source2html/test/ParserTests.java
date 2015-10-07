package org.adamnew123456.source2html.test;
import org.adamnew123456.source2html.syntax.parsing.*;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Optional;

public class ParserTests {
    /**
     * Generates a CheckpointStream from some characters.
     */
    private CheckpointStream toStream(String in) {
        CheckpointStream stream = new CheckpointStream();
        stream.extend(in);
        return stream;
    }
    
    @Test
    public void testSingleCharSuccess() {
        Parser singleChar = new AnyCharParser();
        CheckpointStream stream = toStream("xyz");
        Optional<String> result = singleChar.tryParse(stream);
        
        assertEquals("Incorrect parse result for AnyCharParser",
                result, Optional.of("x"));
        assertEquals("Incorrect parse leftover for AnyCharParser",
                stream.toString(), "yz");
    }
    
    @Test
    public void testSingleCharFailureEmpty() {
        Parser singleChar = new AnyCharParser();
        CheckpointStream stream = toStream("");
        Optional<String> result = singleChar.tryParse(stream);
        
        assertEquals("Incorrect parse result for AnyCharParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for AnyCharParser",
                stream.toString(), "");
    }
    
    @Test
    public void testGroupParserSuccess() {
        Parser group = new GroupParser("abc");
        CheckpointStream stream = toStream("abc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for GroupParser",
                result, Optional.of("a"));
        assertEquals("Incorrect parse leftover for GroupParser",
                stream.toString(), "bc");
    }
    
    @Test
    public void testGroupParserFailureEmpty() {
        Parser group = new GroupParser("abc");
        CheckpointStream stream = toStream("");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for GroupParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for GroupParser",
                stream.toString(), "");
    }
    
    @Test
    public void testGroupParserFailureInvalidChar() {
        Parser group = new GroupParser("abc");
        CheckpointStream stream = toStream("xabc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for GroupParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for GroupParser",
                stream.toString(), "xabc");
    }
    
    @Test
    public void testNegativeGroupParserSuccess() {
        Parser group = new NegativeGroupParser("abc");
        CheckpointStream stream = toStream("xabc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for NegativeGroupParser",
                result, Optional.of("x"));
        assertEquals("Incorrect parse leftover for NegativeGroupParser",
                stream.toString(), "abc");
    }
    
    @Test
    public void testNegativeGroupParserFailureEmpty() {
        Parser group = new NegativeGroupParser("abc");
        CheckpointStream stream = toStream("");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for NegativeGroupParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for NegativeGroupParser",
                stream.toString(), "");
    }
    
    @Test
    public void testNegativeGroupParserFailureInvalidChar() {
        Parser group = new NegativeGroupParser("abc");
        CheckpointStream stream = toStream("abc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for NegativeGroupParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for NegativeGroupParser",
                stream.toString(), "abc");
    }
    
    @Test
    public void testSequenceParserTrivialSuccess() {
        Parser group = new SequenceParser();
        CheckpointStream stream = toStream("abc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for SequenceParser",
                result, Optional.of(""));
        assertEquals("Incorrect parse leftover for SequenceParser",
                stream.toString(), "abc");
    }
    
    @Test
    public void testSequenceParserNonTrivialSuccess() {
        Parser group = new SequenceParser(new AnyCharParser(), new AnyCharParser());
        CheckpointStream stream = toStream("abc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for SequenceParser",
                result, Optional.of("ab"));
        assertEquals("Incorrect parse leftover for SequenceParser",
                stream.toString(), "c");
    }
    
    @Test
    public void testSequenceParserFailure() {
        Parser group = new SequenceParser(new AnyCharParser(), new GroupParser("abc"));
        CheckpointStream stream = toStream("kx");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for SequenceParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for SequenceParser",
                stream.toString(), "kx");
    }
    
    @Test
    public void testEitherParserSuccess() {
        Parser group = new EitherParser(new GroupParser("xyz"), new AnyCharParser());
        CheckpointStream stream = toStream("abc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for EitherParser",
                result, Optional.of("a"));
        assertEquals("Incorrect parse leftover for EitherParser",
                stream.toString(), "bc");
    }
    
    @Test
    public void testEitherParserFailure() {
        Parser group = new EitherParser(new GroupParser("ab"), new GroupParser("cd"));
        CheckpointStream stream = toStream("kx");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for EitherParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for EitherParser",
                stream.toString(), "kx");
    }
    
    @Test
    public void testZeroOrOneSuccessZero() {
        Parser group = new ZeroOrOneParser(new GroupParser("ab"));
        CheckpointStream stream = toStream("cde");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for ZeroOrOneParser",
                result, Optional.of(""));
        assertEquals("Incorrect parse leftover for ZeroOrOneParser",
                stream.toString(), "cde");
    }
    
    @Test
    public void testZeroOrOneSuccessOne() {
        Parser group = new ZeroOrOneParser(new GroupParser("ab"));
        CheckpointStream stream = toStream("abc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for ZeroOrOneParser",
                
                result, Optional.of("a"));
        assertEquals("Incorrect parse leftover for ZeroOrOneParser",
                stream.toString(), "bc");
    }
    
    @Test
    public void testZeroOrMoreSuccessZero() {
        Parser group = new ZeroOrMoreParser(new GroupParser("ab"));
        CheckpointStream stream = toStream("cde");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for ZeroOrMorePaser",
                result, Optional.of(""));
        assertEquals("Incorrect parse leftover for ZeroOrMorePaser",
                stream.toString(), "cde");
    }
    
    @Test
    public void testZeroOrMoreSuccessMore() {
        Parser group = new ZeroOrMoreParser(new GroupParser("ab"));
        CheckpointStream stream = toStream("ababc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for ZeroOrMorePaser",
                result, Optional.of("abab"));
        assertEquals("Incorrect parse leftover for ZeroOrMorePaser",
                stream.toString(), "c");
    }
    
    @Test
    public void testOneOrMoreSuccessOne() {
        Parser group = new OneOrMoreParser(new GroupParser("ab"));
        CheckpointStream stream = toStream("a");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for OneOrMoreParser",
                result, Optional.of("a"));
        assertEquals("Incorrect parse leftover for OneOrMoreParser",
                stream.toString(), "");
    }
    
    @Test
    public void testOneOrMoreSuccessMore() {
        Parser group = new OneOrMoreParser(new GroupParser("ab"));
        CheckpointStream stream = toStream("ababc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for OneOrMoreParser",
                result, Optional.of("abab"));
        assertEquals("Incorrect parse leftover for OneOrMoreParser",
                stream.toString(), "c");
    }
    
    @Test
    public void testOneOrMoreFailureZero() {
        Parser group = new OneOrMoreParser(new GroupParser("ab"));
        CheckpointStream stream = toStream("cde");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for OneOrMoreParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for OneOrMoreParser",
                stream.toString(), "cde");
    }
    
    @Test
    public void testNegativeLookaheadSuccess() {
        // This negative-lookahead parser consumes single characters until it 
        // hits whitespace
        Parser neg = new NegativeLookaheadParser(
                new SequenceParser(new GroupParser("."), new GroupParser(" \n\t")), 
                new AnyCharParser());
        
        CheckpointStream stream = toStream("abcde.fghij k. lmno");
        Optional<String> result = neg.tryParse(stream);
        
        assertEquals("Incorrect parse result for NegativeLookaheadParser",
                result, Optional.of("abcde.fghij k"));
        assertEquals("Incorrect parse leftover for NegativeLookaheadParser",
                stream.toString(), ". lmno");
    }
}
