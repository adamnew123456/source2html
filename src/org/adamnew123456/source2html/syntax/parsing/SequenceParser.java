package org.adamnew123456.source2html.syntax.parsing;

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
    public Optional<String> tryParse(CheckpointStream stream) {
        stream.checkpoint();
        
        StringBuffer result = new StringBuffer();
        for (Parser parser: parsers) {
            Optional<String> intermed_result = parser.tryParse(stream);
            
            if (intermed_result.isPresent()) {
                result.append(intermed_result.get());
            } else {
                stream.restore();
                return Optional.empty();
            }
        }
        
        stream.commit();
        return Optional.of(result.toString());
    }

}
