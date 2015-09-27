package org.adamnew123456.source2html.syntax.parsing;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * A sequence parser makes sure that all of its input parsers execute 
 * successfully, in order.
 */
public class SequenceParser implements Parser {
    Parser[] parsers;
    
    public SequenceParser(Parser... parsers) {
        this.parsers = parsers;
    }
    
    @Override
    public Optional<String> tryParse(Deque<Character> stream) {
        Deque<Character> copied = new ArrayDeque<>();
        stream.forEach(elt -> copied.add(elt));
        
        StringBuffer result = new StringBuffer();
        for (Parser parser: parsers) {
            Optional<String> intermed_result = parser.tryParse(stream);
            
            if (intermed_result.isPresent()) {
                result.append(intermed_result.get());
            } else {
                // Since we don't care about anything that the input stream
                // still has, truncate the copied stream to just the part that
                // the input stream doesn't have
                int charsRead = copied.size() - stream.size();
                while (copied.size() > charsRead) {
                    copied.removeLast();
                }
                
                // Now, rewind the stream by putting on characters from the copy
                while (copied.size() > 0) {
                    stream.addFirst(copied.removeLast());
                }
                
                return Optional.empty();
            }
        }
        
        return Optional.of(result.toString());
    }

}
