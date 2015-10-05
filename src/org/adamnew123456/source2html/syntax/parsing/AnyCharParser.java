package org.adamnew123456.source2html.syntax.parsing;

import java.util.Optional;

/**
 * The single parser parses a single character (of any kind) or fails.
 */
public class AnyCharParser implements Parser {
    @Override
    public Optional<String> tryParse(CheckpointStream stream) {
        if (stream.size() == 0) return Optional.empty();
        else                    return Optional.of(String.valueOf(stream.get()));
    }

}
