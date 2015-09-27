package org.adamnew123456.source2html.test;
import org.adamnew123456.source2html.syntax.parsing.*;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class ParserTests {
    /**
     * Generates a Deque of characters from a String
     */
    private Deque<Character> toStream(String in) {
        Deque<Character> stream = new ArrayDeque<>();
        for (char c: in.toCharArray()) {
            stream.add(c);
        }
        return stream;
    }
    
    /**
     * Generates a String from a Deque of characters
     */
    private String toString(Deque<Character> in) {
        StringBuffer buff = new StringBuffer();
        for (char c: in) {
            buff.append(c);
        }
        return buff.toString();
    }
    
    @Test
    public void testSingleCharSuccess() {
        Parser singleChar = new AnyCharParser();
        Deque<Character> stream = toStream("xyz");
        Optional<String> result = singleChar.tryParse(stream);
        
        assertEquals("Incorrect parse result for AnyCharParser",
                result, Optional.of("x"));
        assertEquals("Incorrect parse leftover for AnyCharParser",
                toString(stream), "yz");
    }
    
    @Test
    public void testSingleCharFailureEmpty() {
        Parser singleChar = new AnyCharParser();
        Deque<Character> stream = toStream("");
        Optional<String> result = singleChar.tryParse(stream);
        
        assertEquals("Incorrect parse result for AnyCharParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for AnyCharParser",
                toString(stream), "");
    }
    
    @Test
    public void testGroupParserSuccess() {
        Parser group = new GroupParser("abc");
        Deque<Character> stream = toStream("abc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for GroupParser",
                result, Optional.of("a"));
        assertEquals("Incorrect parse leftover for GroupParser",
                toString(stream), "bc");
    }
    
    @Test
    public void testGroupParserFailureEmpty() {
        Parser group = new GroupParser("abc");
        Deque<Character> stream = toStream("");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for GroupParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for GroupParser",
                toString(stream), "");
    }
    
    @Test
    public void testGroupParserFailureInvalidChar() {
        Parser group = new GroupParser("abc");
        Deque<Character> stream = toStream("xabc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for GroupParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for GroupParser",
                toString(stream), "xabc");
    }
    
    @Test
    public void testNegativeGroupParserSuccess() {
        Parser group = new NegativeGroupParser("abc");
        Deque<Character> stream = toStream("xabc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for NegativeGroupParser",
                result, Optional.of("x"));
        assertEquals("Incorrect parse leftover for NegativeGroupParser",
                toString(stream), "abc");
    }
    
    @Test
    public void testNegativeGroupParserFailureEmpty() {
        Parser group = new NegativeGroupParser("abc");
        Deque<Character> stream = toStream("");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for NegativeGroupParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for NegativeGroupParser",
                toString(stream), "");
    }
    
    @Test
    public void testNegativeGroupParserFailureInvalidChar() {
        Parser group = new NegativeGroupParser("abc");
        Deque<Character> stream = toStream("abc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for NegativeGroupParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for NegativeGroupParser",
                toString(stream), "abc");
    }
    
    @Test
    public void testSequenceParserTrivialSuccess() {
        Parser group = new SequenceParser();
        Deque<Character> stream = toStream("abc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for SequenceParser",
                result, Optional.of(""));
        assertEquals("Incorrect parse leftover for SequenceParser",
                toString(stream), "abc");
    }
    
    @Test
    public void testSequenceParserNonTrivialSuccess() {
        Parser group = new SequenceParser(new AnyCharParser(), new AnyCharParser());
        Deque<Character> stream = toStream("abc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for SequenceParser",
                result, Optional.of("ab"));
        assertEquals("Incorrect parse leftover for SequenceParser",
                toString(stream), "c");
    }
    
    @Test
    public void testSequenceParserFailure() {
        Parser group = new SequenceParser(new AnyCharParser(), new GroupParser("abc"));
        Deque<Character> stream = toStream("kx");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for SequenceParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for SequenceParser",
                toString(stream), "kx");
    }
    
    @Test
    public void testEitherParserSuccess() {
        Parser group = new EitherParser(new GroupParser("xyz"), new AnyCharParser());
        Deque<Character> stream = toStream("abc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for EitherParser",
                result, Optional.of("a"));
        assertEquals("Incorrect parse leftover for EitherParser",
                toString(stream), "bc");
    }
    
    @Test
    public void testEitherParserFailure() {
        Parser group = new EitherParser(new GroupParser("ab"), new GroupParser("cd"));
        Deque<Character> stream = toStream("kx");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for EitherParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for EitherParser",
                toString(stream), "kx");
    }
    
    @Test
    public void testZeroOrOneSuccessZero() {
        Parser group = new ZeroOrOneParser(new GroupParser("ab"));
        Deque<Character> stream = toStream("cde");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for ZeroOrOneParser",
                result, Optional.of(""));
        assertEquals("Incorrect parse leftover for ZeroOrOneParser",
                toString(stream), "cde");
    }
    
    @Test
    public void testZeroOrOneSuccessOne() {
        Parser group = new ZeroOrOneParser(new GroupParser("ab"));
        Deque<Character> stream = toStream("abc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for ZeroOrOneParser",
                
                result, Optional.of("a"));
        assertEquals("Incorrect parse leftover for ZeroOrOneParser",
                toString(stream), "bc");
    }
    
    @Test
    public void testZeroOrMoreSuccessZero() {
        Parser group = new ZeroOrMoreParser(new GroupParser("ab"));
        Deque<Character> stream = toStream("cde");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for ZeroOrMorePaser",
                result, Optional.of(""));
        assertEquals("Incorrect parse leftover for ZeroOrMorePaser",
                toString(stream), "cde");
    }
    
    @Test
    public void testZeroOrMoreSuccessMore() {
        Parser group = new ZeroOrMoreParser(new GroupParser("ab"));
        Deque<Character> stream = toStream("ababc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for ZeroOrMorePaser",
                result, Optional.of("abab"));
        assertEquals("Incorrect parse leftover for ZeroOrMorePaser",
                toString(stream), "c");
    }
    
    @Test
    public void testOneOrMoreSuccessOne() {
        Parser group = new OneOrMoreParser(new GroupParser("ab"));
        Deque<Character> stream = toStream("a");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for OneOrMoreParser",
                result, Optional.of("a"));
        assertEquals("Incorrect parse leftover for OneOrMoreParser",
                toString(stream), "");
    }
    
    @Test
    public void testOneOrMoreSuccessMore() {
        Parser group = new OneOrMoreParser(new GroupParser("ab"));
        Deque<Character> stream = toStream("ababc");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for OneOrMoreParser",
                result, Optional.of("abab"));
        assertEquals("Incorrect parse leftover for OneOrMoreParser",
                toString(stream), "c");
    }
    
    @Test
    public void testOneOrMoreFailureZero() {
        Parser group = new OneOrMoreParser(new GroupParser("ab"));
        Deque<Character> stream = toStream("cde");
        Optional<String> result = group.tryParse(stream);
        
        assertEquals("Incorrect parse result for OneOrMoreParser",
                result, Optional.empty());
        assertEquals("Incorrect parse leftover for OneOrMoreParser",
                toString(stream), "cde");
    }
}
