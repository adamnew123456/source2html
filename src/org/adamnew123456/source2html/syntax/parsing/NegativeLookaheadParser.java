package org.adamnew123456.source2html.syntax.parsing;

import java.util.Optional;

/**
 * A negative lookahead parser consumes input as long as two things are true:
 * 
 * - The lookahead parser doesn't match
 * - The matching parser does
 * 
 * For example, you could have a negative lookahead parser:
 * 
 *     new NegativeLookaheadParser(new GroupParser("\n"), new AnyCharParser())
 *     
 * This will read in any character, checking each time to make sure that the
 * GroupParser does not match. The negative lookahead is used purely as a check,
 * and is not allowed to affect the stream in a permanent way.
 */
public class NegativeLookaheadParser implements Parser {
    private Parser lookahead;
    private Parser parser;
    
    public NegativeLookaheadParser(Parser lookahead, Parser parser) {
        this.lookahead = lookahead;
        this.parser = parser;
    }
    
    @Override
    public Optional<String> tryParse(CheckpointStream stream) {
        StringBuffer buffer = new StringBuffer();
        
        boolean lookaheadFails = true;
        boolean parserSucceeds = true;
        do {
            stream.checkpoint();
            Optional<String> lookaheadResult = lookahead.tryParse(stream);
            stream.restore();
            
            if (lookaheadResult.isPresent()) {
                lookaheadFails = false;
            } else {
                Optional<String> parserResult = parser.tryParse(stream);
                
                if (parserResult.isPresent()) {
                    buffer.append(parserResult.get());
                } else {
                    parserSucceeds = false;
                }
            }
        } while (!stream.isEmpty() && lookaheadFails && parserSucceeds);
        
        return Optional.of(buffer.toString());
    }
}
