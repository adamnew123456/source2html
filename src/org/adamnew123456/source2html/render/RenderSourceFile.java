package org.adamnew123456.source2html.render;
import org.adamnew123456.source2html.JavaFile;

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
        + ".code { color: lightgray }\n"
        + ".lineNumber { color: green; margin-right: 5px  }\n");
    
    public RenderSourceFile(JavaFile code) {
        sourceFile = code;
    }
    
    @Override
    public String toHTML() {
        // Figure out how much to offset the line number by, to keep them all
        // aligned as a single column
        int alignAmount = (int)Math.ceil(Math.log10(sourceFile.getLineCount()));
        String formatString = "%" + alignAmount + "d";
        
        // Render each line, along with their appropriate line numbers
        StringJoiner codeLines = new StringJoiner("\n");
        int lineNumber = 1;
        for (String line: sourceFile.getSource().split("\n")) {
            String lineNumberStr = String.format(formatString, lineNumber);
            
            ST formatLine = new ST(lineTemplate);
            formatLine.add("lineNumber", lineNumberStr);
            formatLine.add("codeLine", HTMLUtils.escapeHTML(line));
            codeLines.add(formatLine.render());
            
            lineNumber++;
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
