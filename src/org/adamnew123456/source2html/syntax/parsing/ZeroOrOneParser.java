package org.adamnew123456.source2html.syntax.parsing;

import java.util.Optional;

/**
 * This parser takes another parser, and applies it either one time or zero
 * times (it cannot fail).
 */
public class ZeroOrOneParser implements Parser {
    private Parser parser;
    
    public ZeroOrOneParser(Parser parser) {
        this.parser = parser;
    }

    @Override
    public Optional<String> tryParse(CheckpointStream stream) {
        Optional<String> result = parser.tryParse(stream);
        
        if (result.isPresent()) {
            return result;
        } else {
            return Optional.of("");
        }
    }
}
