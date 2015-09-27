package org.adamnew123456.source2html.syntax.parsing;

import java.util.Deque;
import java.util.Optional;

/**
 * This parser allows the given parser to match any number of times
 * (this cannot fail).
 */
public class ZeroOrMoreParser implements Parser {
    private Parser parser;
    
    public ZeroOrMoreParser(Parser parser) {
        this.parser = parser;
    }

    @Override
    public Optional<String> tryParse(Deque<Character> stream) {
        StringBuffer buffer = new StringBuffer();
        Optional<String> result;
        
        do {
            result = parser.tryParse(stream);
            
            if (result.isPresent()) {
                buffer.append(result.get());
            }
        } while (result.isPresent());
        
        return Optional.of(buffer.toString());
    }
}
