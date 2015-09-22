package org.adamnew123456.source2html;

import java.util.ArrayList;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

/**
 * A Package is an object which refers to a particular Java package. Note that
 * Package objects are re-used, which is why there is no public constructor.
 */
public class Package {
    /*
     * Re-using Packages gives us a couple of nice properties:
     * 
     * - We can easily account for all Packages, which becomes important when
     *   building the packages' link trees.
     * - Packages can be compared via reference, since no two non-equal packages
     *   are different objects
     * - Packages will always be reused, avoiding the need to have many little
     *   Package objects floating around everywhere.
     */
    private static Map<String, Package> packageCache = new HashMap<>();
    private final String name;
    
    private Package(String name) {
        this.name = name;
    }
    
    /*
     * Returns the name of this package.
     */
    public String getName() {
        return name;
    }
    
    /*
     * Factory method: generates a Package from a package name, reusing a
     * pre-existing Package if possible.
     */
    public static Package fromPackageName(String name) {
        if (!packageCache.containsKey(name)) {
            Package pkg = new Package(name);
            packageCache.put(name, pkg);
        }
        
        return packageCache.get(name);
    }
    
    /*
     * Returns all the packages registered so far.
     */
    public static List<Package> getPackages() {
        List<Package> packages = new ArrayList<>(packageCache.size());
        for (Package value: packageCache.values()) {
            packages.add(value);
        }
        
        return packages;
    }
}
