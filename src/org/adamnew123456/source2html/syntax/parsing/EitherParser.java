package org.adamnew123456.source2html.syntax.parsing;

import java.util.Deque;
import java.util.Optional;

/**
 * A parser which tries to parse from multiple input parsers, succeeding if one
 * of them works, or failing if all fail.
 */
public class EitherParser implements Parser {
    Parser[] parsers;
    
    public EitherParser(Parser... parsers) {
        this.parsers = parsers;
    }

    @Override
    public Optional<String> tryParse(Deque<Character> stream) {
        for (Parser parser: parsers) {
            Optional<String> result = parser.tryParse(stream);
            
            if (result.isPresent()) return result;
        }
        
        return Optional.empty();
    }

}
