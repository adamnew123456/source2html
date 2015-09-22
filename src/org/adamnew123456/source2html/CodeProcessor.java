package org.adamnew123456.source2html;
import org.adamnew123456.source2html.render.Renderable;
import org.adamnew123456.source2html.render.RenderPackageList;
import org.adamnew123456.source2html.render.RenderSourceFile;
import org.adamnew123456.source2html.render.RenderFrame;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This is responsible for generating all the Package and JavaFile objects,
 * and then rendering them all to the output directory.
 */
public class CodeProcessor {
    public void run(File inDir, File outDir, String linkPrefix) {
        List<JavaFile> javaSource = readSourceFiles(inDir);
        List<Package> packages = Package.getPackages();
        
        RenderPackageList pkgList = new RenderPackageList(packages, javaSource, linkPrefix);
        renderIndexPage(outDir, pkgList);
        
        // Populate the tree with package directories, so we have somewhere to
        // put the files
        for (Package pkg: packages) {
            File packageDir = new File(outDir, pkg.getName());
            packageDir.mkdir();
        }
        
        for (JavaFile file: javaSource) {
            RenderSourceFile renderFile = new RenderSourceFile(file);
            renderSourceFile(outDir, file, renderFile);
        }
    }
    
    /**
     * Renders the frame in the index page which displays the code.
     */
    private void renderIndexPage(File outDir, Renderable packages) {
        Renderable frame = new RenderFrame(packages);
                
        File indexFile = new File(outDir, "index.html");
        try {
            indexFile.createNewFile();
            
            FileWriter writer = new FileWriter(indexFile);
            writer.write("<html><head>"
                    + "<title>Code</title>"
                    + "<script>" + frame.toJavascript() + "</script>"
                    + "<style>" + frame.toCSS() + "</style>"
                    + "</head>"
                    + "<body>" + frame.toHTML() + "</body>"
                    + "</html>");
            writer.close();
        } catch (IOException err) {
            System.err.println("Could not create index " + indexFile.getPath());
        }
    }
    
    /**
     * Renders a single source file to its own page. Although these are complete
     * HTML files which can be displayed on their own, their primary purpose is
     * to be visible using the frame page.
     */
    private void renderSourceFile(File outDir, JavaFile file, RenderSourceFile source) {
        File packageDir = new File(outDir, file.getPackage().getName());
        File sourceFile = new File(packageDir, file.getName() + ".html");
        
        try {
            sourceFile.createNewFile();
            
            FileWriter writer = new FileWriter(sourceFile);
            writer.write("<html><head>"
                    + "<title>" + file.getName() + ".java </title>"
                    + "<script>" + source.toJavascript() + "</script>"
                    + "<style>" + source.toCSS() + "</style>"
                    + "</head>"
                    + "<body>" + source.toHTML() + "</body>"
                    + "</html>");
            writer.close();
        } catch (IOException err) {
            System.err.println("Could not create file " + file.getName() 
                    + " in package " + file.getPackage().getName());
        }
    }
    
    /**
     * Recursively gathers all the Java source code files in the given directory
     */
    private List<JavaFile> readSourceFiles(File inDir) {
        List<JavaFile> files = new LinkedList<>();
        File[] children = inDir.listFiles();
        for (File child: children) {
            if (child.isDirectory()) {
                files.addAll(readSourceFiles(child));
            } else if (child.isFile() && child.getName().endsWith(".java")) {
                try {
                    JavaFile sourceFile = new JavaFile(child);
                    files.add(sourceFile);
                } catch (IOException err) {
                    System.err.println("Could not access file " 
                            + child.getPath() + ": " + err.toString());
                }
            }
        }
        
        return files;
    }
}
