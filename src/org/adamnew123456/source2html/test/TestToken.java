package org.adamnew123456.source2html.test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

import org.adamnew123456.source2html.syntax.Token;
import org.adamnew123456.source2html.syntax.TokenType;
import org.junit.Test;

public class TestToken {
    private static <T> boolean listEq(List<T> a, List<T> b) {
        if (a.size() != b.size()) {
            System.out.println("Unequal sizes: " + a.size() + " and " + b.size());
            return false;
        }
        
        Stream<Pair<T, T>> pairs =
                IntStream.range(0, a.size())
                .boxed()
                .map(i -> new Pair<T,T>(a.get(i), b.get(i)));
        
        return pairs.allMatch(elems -> {
            if (!elems.first.equals(elems.second)) {
                System.out.println(String.format("%s != %s", elems.first, elems.second));
                return false;
            } else {
                return true;
            }
        });
    }
    
    @Test
    public void testSplitSingleLineToken() {
        Token singleLine = new Token("foo", TokenType.RAW);
        assertTrue("The single line token did not split correctly",
                listEq(singleLine.splitLines(),
                        Arrays.asList(new Token[] { new Token("foo", TokenType.RAW)})));
    }
    
    @Test
    public void testSplitMultiLineToken() {
        Token multiLine = new Token("foo\nbar\nbaz", TokenType.RAW);
        assertTrue("The multi-line token did not split correctly",
                listEq(multiLine.splitLines(),
                        Arrays.asList(new Token[] { new Token("foo", TokenType.RAW),
                                                    new Token("bar", TokenType.RAW),
                                                    new Token("baz", TokenType.RAW)
                        })));
    }
}
