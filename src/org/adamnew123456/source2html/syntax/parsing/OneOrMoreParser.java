package org.adamnew123456.source2html.syntax.parsing;

import java.util.Deque;
import java.util.Optional;

/**
 * This parser ensures that the given parser parses at least once, and maybe
 * more times than that.
 */
public class OneOrMoreParser implements Parser {
    private Parser parser;
    
    public OneOrMoreParser(Parser parser) {
        this.parser = parser;
    }

    @Override
    public Optional<String> tryParse(Deque<Character> stream) {
        StringBuffer buffer = new StringBuffer();
        Optional<String> result = parser.tryParse(stream);
        if (!result.isPresent()) {
            return Optional.empty();
        } else {
            buffer.append(result.get());
        }
        
        do {
            result = parser.tryParse(stream);
            
            if (result.isPresent()) {
                buffer.append(result.get());
            }
        } while (result.isPresent());
        
        return Optional.of(buffer.toString());
    }
}
