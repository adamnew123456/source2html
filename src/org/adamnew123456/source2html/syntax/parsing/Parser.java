package org.adamnew123456.source2html.syntax.parsing;

/**
 * Parsers are single units, which are capable of doing one of two things:
 * 
 * 1. Accepting the start of the input, returning a parsed out String.
 * 2. Rejecting the input, returning nothing and resetting the input stream.
 * 
 * The second part of the 'failure' clause is very important - the parser
 * *must* reset the contents of the stream should it fail.
 */
import java.util.Deque;
import java.util.Optional;

public interface Parser {
    Optional<String> tryParse(Deque<Character> stream);
}
