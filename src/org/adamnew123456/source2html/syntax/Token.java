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
        return String.format("Token[%s:%d] `%s`", type, chunk.length(), escapedChunk());
    }
 
    /**
     * Returns the content of the token, but with whitespace escaped.
     */
    private String escapedChunk() {
        return getChunk()
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\b", "\\b")
                .replace("\f", "\\f");
    }
    
    /**
     * Returns the raw contents of the token.
     */
    public String getChunk() {
        return chunk;
    }
    
    /**
     * Returns the type of this token.
     */
    public TokenType getTokenType() {
        return type;
    }
    
    /**
     * Splits this Token into multiple tokens which respect line boundaries,
     * while keeping newlines at the end of each line.
     */
    public List<Token> splitLines() {
        if (!chunk.contains("\n") || chunk.equals("\n")) {
            return Arrays.asList(new Token[] { this });
        }
        
        List<Token> out = new ArrayList<Token>();
        String[] lines = chunk.split("\n");
        int idx = 0;
        for (String line: lines) {
            if (idx < lines.length - 1) {
                out.add(new Token(line + "\n", type));
            } else {
                out.add(new Token(line, type));
            }
            
            idx++;
        }
        
        return out;
    }
}
