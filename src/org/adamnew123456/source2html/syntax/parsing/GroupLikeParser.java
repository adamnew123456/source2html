package org.adamnew123456.source2html.syntax.parsing;

import java.util.Optional;

/**
 * A parser which is based upon some list of characters.
 */
public abstract class GroupLikeParser implements Parser {
    protected String chars;
    
    public GroupLikeParser(String chars) {
        this.chars = chars;
    }
    
    /**
     * Whether or not the given character matches the group.
     * 
     * This is abstract so that something like InverseGroup, which only reads
     * a character *not* in the group, can easily exist.
     */
    abstract protected boolean matches(char c);
    
    /**
     * Converts a character to a string.
     */
    protected String charToString(char c) {
        return String.valueOf(c);
    }
    
    @Override
    public Optional<String> tryParse(CheckpointStream stream) {
        if (stream.size() == 0) return Optional.empty();
        
        if (matches(stream.peek())) {
            return Optional.of(charToString(stream.get()));
        } else {
            return Optional.empty();
        }
    }

}
