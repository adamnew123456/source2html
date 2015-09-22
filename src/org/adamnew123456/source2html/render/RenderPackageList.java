package org.adamnew123456.source2html.render;
import org.adamnew123456.source2html.JavaFile;
import org.adamnew123456.source2html.Package;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.stringtemplate.v4.ST;
import org.cougaar.NaturalOrderComparator;

/**
 * This class is responsible for rendering the list of packages.
 * 
 * The package list is a sidebar which appears on every page, and which shows
 * all packages, each with a list of the files within:
 * 
 * +------------------+
 * |  + lec7.ex1      |
 * |  + lec7.ex2      |
 * |  + lec7.ex3      |
 * |  - lec7.ex4      |
 * |    Main.java     |
 * |    Util.java     |
 * |  +lec7.ex5       |
 * +------------------+
 * 
 * Note that the rendering context must supply a definition for loadSource(url)
 * in its toJavascript method.
 */
public class RenderPackageList implements Renderable {
    /**
     * Maps each package to the list of files which belong to that package.
     */
    Map<Package, List<JavaFile>> packageFiles;
    
    // Links to source code always start with this
    private String linkPrefix;
    
    /*
     * These generate the relevant HTML for declaring the package list, and the
     * file list for each package.
     * 
     * Yes, writing HTML and JS inline are bad. Java, lacking multi-line 
     * strings, adds insult to injury by making hunks of inline text more 
     * ugly than necessary.
     */
    private static ST packageTemplate = new ST(
        "<div class=\"packageEntry\">"
        + "<input type=\"button\" value=\"+\" id=\"package-{pkgName}\" "
        +   "onclick=\"toggleExpand('files-{pkgName}')\"></input>"
        +  "<span class=\"packageName\"> {pkgName} </span>"
        +  "<div class=\"packageFiles\" style=\"display: none\" id=\"files-{pkgName}\">"
        +   "{pkgFiles} "
        +  "</div>"
        + "</div>", 
        '{', '}');
    
    private static ST fileTemplate = new ST(
        "<a class=\"fileName\" onclick=\"loadCode('{prefix}/{pkgName}/{fileName}.html')\">"
        + "{fileName}.java"
        + "</a><br/>",
        '{', '}'
        );
    
    public RenderPackageList(List<Package> packages, List<JavaFile> files, 
            String prefix) {
        linkPrefix = prefix;
        packageFiles = new HashMap<>();
        for (Package pkg: packages) {
            packageFiles.put(pkg, new LinkedList<JavaFile>());
        }
        
        for (JavaFile file: files) {
            Package pkg = file.getPackage();
            packageFiles.get(pkg).add(file);
        }
    }
    
    /**
     * Creates HTML which represents the list of packages. It is meant to be
     * embedded into a page which contains the main source file somehow, such as
     * inside of a <div>.
     */
    @Override
    public String toHTML() {
        StringBuilder builder = new StringBuilder();
        
        // I want the packages to appear in a sane order inside the listing
        List<Package> sortedPkgs = new LinkedList<>();
        sortedPkgs.addAll(packageFiles.keySet());
        
        // NaturalOrderComparator uses the pre-generics convention, so ignore
        // the warnings on this code
        final Comparator cmp = new NaturalOrderComparator();
        sortedPkgs.sort((pkg1, pkg2) -> {
            return cmp.compare(pkg1.getName(), pkg2.getName());
        });
     
        // Generate the link groups for each package
        for (Package pkg: sortedPkgs) {
            StringBuilder fileParts = new StringBuilder();
            
            for (JavaFile file: packageFiles.get(pkg)) {
                ST fileSub = new ST(fileTemplate);
                fileSub.add("fileName", file.getName());
                fileSub.add("pkgName", pkg.getName());
                fileSub.add("prefix", linkPrefix);
                
                fileParts.append(fileSub.render());
            }
            
            ST packageSub = new ST(packageTemplate);
            packageSub.add("pkgName", pkg.getName());
            packageSub.add("pkgFiles", fileParts.toString());
            builder.append(packageSub.render());
        }
        
        return builder.toString();
    }
    
    /**
     * Creates CSS which provides default styles for the classes used in toHTML.
     */
    @Override
    public String toCSS() {
        return ".packageEntry { padding: 5px ; color: black }\n" +
               ".packageName  { font-weight: bold ; color: blue }\n";
    }
    
    /**
     * Creates Javascript which is used to expand/contract package listings.
     */
    @Override
    public String toJavascript() {
        return "function toggleExpand(elemID) {"
                +   "var element = document.getElementById(elemID);"
                +   "if (element.style.display == \"none\") {"
                +     "element.style.display = \"block\";"
                +   "} else {"
                +     "element.style.display = \"none\";"
                +   "}"
                + "}";
    }
}
