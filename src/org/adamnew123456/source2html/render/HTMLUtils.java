package org.adamnew123456.source2html.render;

/*
 * Utilities which relate to generating HTML, including:
 * 
 * - Doing HTML entity escapes
 */
class HTMLUtils {
    /*
     * Escapes &, <, >, ' and " into their respective HTML entities.
     */
    public static String escapeHTML(String text) {
        StringBuilder builder = new StringBuilder();
        text.chars().forEach(character -> {
            switch ((char)character) {
            case '&':
                builder.append("&amp;");
                break;
            case '<':
                builder.append("&lt;");
                break;
            case '>':
                builder.append("&gt;");
                break;
            case '\'':
                builder.append("&apos;");
                break;
            case '"':
                builder.append("&quot;");
                break;
            default:
                builder.append((char)character);
            }
        });
        
        return builder.toString();
    }
}
