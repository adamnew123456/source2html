package org.adamnew123456.source2html.syntax.parsing;

public class NegativeGroupParser extends GroupLikeParser {
    public NegativeGroupParser(String chars) {
        super(chars);
    }

    @Override
    protected boolean matches(char c) {
        return !chars.contains(charToString(c));
    }
}