package org.adamnew123456.source2html.syntax.parsing;

/**
 * A parser which accepts a single character from a group.
 */
public class GroupParser extends GroupLikeParser {
    public GroupParser(String chars) {
        super(chars);
    }

    @Override
    protected boolean matches(char c) {
        return chars.contains(charToString(c));
    }
}
