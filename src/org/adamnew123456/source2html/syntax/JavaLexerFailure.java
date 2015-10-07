package org.adamnew123456.source2html.syntax;

public class JavaLexerFailure extends RuntimeException {
    static final long serialVersionUID = 1L;
    
    public JavaLexerFailure(String chunk) {
        super(chunk);
    }
}
