package org.adamnew123456.source2html.syntax;

/**
 * The types of syntax that the lexer knows how to represent.
 */
public enum TokenType {
    COMMENT,
    CHARACTER,
    STRING,
    KEYWORD,

    // RAW is essentially where we dump anything that isn't any of the above
    RAW,
}
