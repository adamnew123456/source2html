package org.adamnew123456.source2html.render;
import org.adamnew123456.source2html.JavaFile;
import org.adamnew123456.source2html.syntax.*;

import java.util.StringJoiner;

import org.stringtemplate.v4.ST;

/**
 * This class is responsible for rendering a page containing source code, 
 * along with line numbers and a pacakge list.
 */
public class RenderSourceFile implements Renderable {
    private JavaFile sourceFile;
    
    /*
     * These generates the HTML for the page as a whole, and each line of code.
     */
    private static ST fileTemplate = new ST(
        "<pre class=\"code\">{codeLines}</pre>",
        '{', '}');
    
    private static ST lineTemplate = new ST(
        "<span class=\"lineNumber\">{lineNumber}</span>"
        + "{codeLine}",
        '{', '}');
    
    private static ST cssTemplate = new ST(
        " body { background: black; color: black }\n"
        + ".code-raw { color: lightgray }\n"
        + ".code-comment { color: #00ff00 }\n"
        + ".code-character { color: #ff9000 }\n"
        + ".code-string { color: #ff5100 }\n"
        + ".code-keyword { color: #00ffff }\n"
        + ".lineNumber { color: green; margin-right: 5px  }\n");
    
    public RenderSourceFile(JavaFile code) {
        sourceFile = code;
    }
    
    @Override
    public String toHTML() {
        System.out.println("[RENDER] " + sourceFile.getPackage().getName() + "/" + sourceFile.getName());
        
        // Figure out how much to offset the line number by, to keep them all
        // aligned as a single column
        int alignAmount = (int)Math.ceil(Math.log10(sourceFile.getLineCount()));
        String formatString = "%" + alignAmount + "d";
        
        // Render each line, along with their appropriate line numbers
        StringBuilder codeLines = new StringBuilder("\n");
        JavaLexer lexer = new JavaLexer(sourceFile.getSource());
        
        for (int lineNumber = 1; lexer.hasNext(); lineNumber++) {
            String lineNumberStr = String.format(formatString, lineNumber);
            StringBuilder lineBuffer = new StringBuilder();

            boolean isEndOfLine = false;
            for (Token token: lexer) {
                if (token.getChunk().endsWith("\n")) {
                    isEndOfLine = true;
                }
                
                switch (token.getTokenType()) {
                case CHARACTER:
                    lineBuffer.append("<span class=\"code-character\">");
                    break;
                case COMMENT:
                    lineBuffer.append("<span class=\"code-comment\">");
                    break;
                case KEYWORD:
                    lineBuffer.append("<span class=\"code-keyword\">");
                    break;
                case STRING:
                    lineBuffer.append("<span class=\"code-string\">");
                    break;
                case RAW:
                    lineBuffer.append("<span class=\"code-raw\">");
                    break;
                }
                
                lineBuffer.append(HTMLUtils.escapeHTML(token.getChunk()));
                lineBuffer.append("</span>");
                
                if (isEndOfLine) {
                    break;
                }
            }
                
            ST formatLine = new ST(lineTemplate);
            formatLine.add("lineNumber", lineNumberStr);
            formatLine.add("codeLine", lineBuffer.toString());
            codeLines.append(formatLine.render());
        }
        
        ST formatPage = new ST(fileTemplate);
        formatPage.add("fileName", sourceFile.getName());
        formatPage.add("codeLines", codeLines.toString());
        return formatPage.render();
    }

    @Override
    public String toCSS() {
        ST formatCSS = new ST(cssTemplate);
        return formatCSS.render();
    }

    @Override
    public String toJavascript() {
        return "";
    }
}
