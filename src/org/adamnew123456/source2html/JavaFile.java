package org.adamnew123456.source2html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.List;
import java.util.LinkedList;
import java.util.regex.*;

/**
 * A JavaFile is a Java source code file; this stores both the source code itself,
 * as well as the package that the file occurs in.
 */
public class JavaFile {
    private String name;
    private List<String> sourceLines;
    private Package thePackage;
    
    /*
     * This should work in most cases. It won't work if:
     * 
     * - The package declaration is split over multiple lines.
     * - There is a package declaration in a block comment, which
     *   lacks the typical leading column of stars.
     * 
     * Those shouldn't happen in non-weird code.
     */
    private static final Pattern packageRegex = 
        Pattern.compile("^\\s*package\\s*([a-zA-Z0-9_]+([.][a-zA-Z0-9_]+)*)\\s*;", 
                Pattern.MULTILINE);
    
    public JavaFile(Reader reader, String name) throws IOException {
        this.name = name;
        sourceLines = new LinkedList<>();
        
        BufferedReader sourceReader = new BufferedReader(reader);
        sourceReader.lines().forEach(line -> {
            sourceLines.add(line);
            
            Matcher matcher = packageRegex.matcher(line);
            if (matcher.find() && thePackage == null) {
                String packageName = matcher.group(1);
                thePackage = Package.fromPackageName(packageName);
            }
        });
        
        if (thePackage == null) {
            thePackage = Package.fromPackageName("default");
        }
        
        sourceReader.close();
        reader.close();
    }
    
    /**
     * This is the most useful constructor, designed for use with files.
     */
    public JavaFile(File input) throws IOException {
        this(new FileReader(input), input.getName());
    }
    
    /**
     * Returns the source code to the code in this file.
     */
    public String getSource() {
        return String.join("\n", sourceLines);
    }
    
    /**
     * Returns the number of lines of code in this file.
     */
    public int getLineCount() {
        return sourceLines.size();
    }
    
    /**
     * Returns the package this souce code was found in.
     */
    public Package getPackage() {
        return thePackage;
    }
    
    /**
     * Returns the name of the file that stores this source code, sans
     * the .java extension.
     */
    public String getName() {
        return name.replace(".java", "");
    }
}
