package org.adamnew123456.source2html.syntax;
import org.adamnew123456.source2html.syntax.parsing.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Optional;

/**
 * This is responsible for converting Java source code into a list of tokens.
 */
public class JavaLexer implements Iterator<Token>, Iterable<Token> {
    static List<String> keywords = Arrays.asList(new String[] {
        "abstract", "continue", "for", "new", "switch",
        "assert", "default", "goto", "package", "synchronized",
        "boolean", "do", "if", "private", "this",
        "break", "double", "implements", "protected", "throw",
        "byte", "else", "import", "public", "throws",
        "case", "enum", "instanceof", "return", "transient",
        "catch", "extends", "int", "short", "try",
        "char", "final", "interface", "static", "void",
        "class", "finally", "long", "strictfp", "volatile",
        "const", "float", "native", "super", "while",
    });

    private static Parser ANY = new AnyCharParser();
    private static Parser SLASH = new GroupParser("/");
    private static Parser BKSLASH = new GroupParser("\\");
    private static Parser STAR = new GroupParser("*");
    private static Parser NEWLINE = new GroupParser("\n");
    private static Parser LOWERCASE = new GroupParser("abcdefghijklmnopqrstuvwxyz");
    private static Parser QUOTE = new GroupParser("'");
    private static Parser DQUOTE = new GroupParser("\"");
    private static Parser DIGIT = new GroupParser("0123456789");
    private static Parser STR_VALID_CHAR = new NegativeGroupParser("\\\n");
    
    // All backslash escapes that appear in strings and characters
    private static Parser CHAR_ESCAPES =
            new SequenceParser(BKSLASH, new GroupParser("btnfr\"'\\"));
    
    // The \uXXX escape is special, since it doesn't fit the normal pattern
    private static Parser UNICODE_ESCAPES =
            new SequenceParser(BKSLASH, new GroupParser("u"),
                    DIGIT, DIGIT, DIGIT, DIGIT);
    
    // This actually captures any lower-case alphabetical identifier - we sort
    // out the matches later
    private static Parser possibleKeyword =
            new OneOrMoreParser(LOWERCASE);
    
    private static Parser singleLineComment = 
            new SequenceParser(SLASH, SLASH,
                    new NegativeLookaheadParser(NEWLINE, ANY));
    
    private static Parser multiLineComment =
            new SequenceParser(SLASH, STAR,
                    new NegativeLookaheadParser(new SequenceParser(STAR, SLASH), ANY),
                    STAR, SLASH);
    
    private static Parser charParser =
            new SequenceParser(QUOTE, 
                    new EitherParser(CHAR_ESCAPES, UNICODE_ESCAPES, STR_VALID_CHAR), 
                    QUOTE);
    
    private static Parser stringParser =
            new SequenceParser(
                    DQUOTE,
                    new NegativeLookaheadParser(DQUOTE,
                            new EitherParser(CHAR_ESCAPES, UNICODE_ESCAPES, STR_VALID_CHAR)),
                    DQUOTE);
    
    private final CheckpointStream stream;
    private List<Token> tokenBuffer;
    
    public JavaLexer(String code) {
        stream = new CheckpointStream();
        stream.extend(code);
        tokenBuffer = new LinkedList<Token>();
    }

    @Override
    public Iterator<Token> iterator() { 
        return this; 
    }

    @Override
    public boolean hasNext() { 
        return !tokenBuffer.isEmpty() || !stream.isEmpty(); 
    } 
    
    @Override
    public Token next() {
        if (tokenBuffer.isEmpty()) {
            Token token = getNextToken();
            
            List<Token> splitOverLines = token.splitLines();
            tokenBuffer.addAll(splitOverLines);
        }
        
        if (tokenBuffer.isEmpty()) {
            throw new NoSuchElementException(
                    String.format("Stream left: %d. Buffer size: %d.", 
                            stream.size(), tokenBuffer.size()));
        } else {
            Token token = tokenBuffer.get(0);
            tokenBuffer.remove(0);
            return token;
        }
    }
    
    /**
     * Reads the next token from the input stream.
     */
    private Token getNextToken() {
        Optional<String> result;
        
        result = possibleKeyword.tryParse(stream);
        if (result.isPresent()) {
            if (keywords.contains(result.get())) {
                return new Token(result.get(), TokenType.KEYWORD);
            } else {
                return new Token(result.get(), TokenType.RAW);
            }
        }
        
        result = singleLineComment.tryParse(stream);
        if (result.isPresent()) {
            return new Token(result.get(), TokenType.COMMENT);
        }
        
        result = multiLineComment.tryParse(stream);
        if (result.isPresent()) {
            return new Token(result.get(), TokenType.COMMENT);
        }
        
        result = charParser.tryParse(stream);
        if (result.isPresent()) {
            return new Token(result.get(), TokenType.CHARACTER);
        }
        
        result = stringParser.tryParse(stream);
        if (result.isPresent()) {
            return new Token(result.get(), TokenType.STRING);
        }
        
        result = ANY.tryParse(stream);
        return new Token(result.get(), TokenType.RAW);
    }
}
