package org.adamnew123456.source2html.syntax;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * A Token is some chunk of code, with a particular interpretation built up by 
 * the lexer
 */
public class Token {
    private String chunk;
    private TokenType type;
    
    public Token(String chunk, TokenType type) {
        this.chunk = chunk;
        this.type = type;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof Token) {
            return ((Token)other).chunk.equals(chunk) && ((Token)other).type == type;
        } else {
            return false;
        }
    }
    
    @Override
    public String toString() {
        return String.format("Token[%s] `%s`", type, chunk);
    }
    
    public String getChunk() {
        return chunk;
    }
    
    public TokenType getTokenType() {
        return type;
    }
    
    /**
     * Splits this Token into multiple tokens which respect line boundaries.
     */
    public List<Token> splitLines() {
        if (!chunk.contains("\n")) {
            return Arrays.asList(new Token[] { this });
        }
        
        List<Token> out = new ArrayList<Token>();
        String[] lines = chunk.split("\n");
        for (String line: lines) {
            out.add(new Token(line, type));
        }
        
        return out;
    }
}
