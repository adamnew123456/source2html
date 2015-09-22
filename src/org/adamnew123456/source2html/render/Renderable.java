package org.adamnew123456.source2html.render;

/*
 * A thing which can be rendered, specifically into HTML, CSS and Javascript.
 * 
 * - HTML renderings can be created by using toHTML, which returns an HTML
 *   string.
 * - CSS renderings can be created by toCSS, which returns a CSS string.
 * - Javascript renderings can be created by toJavascript, which returns a
 *   Javascript string.
 */
public interface Renderable {
    String toHTML();
    String toCSS();
    String toJavascript();
}
