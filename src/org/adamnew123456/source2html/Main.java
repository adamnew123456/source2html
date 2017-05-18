package org.adamnew123456.source2html;

import java.io.File;

public class Main {
    static void usage() {
        System.err.println("Usage: <package> <output> [link-prefix]");
        System.exit(1);
    }
    
    public static void main(String[] args) {
        try {
            String input = args[0];
            String output = args[1];
            String prefix;

            if (args.length >= 3) {
                prefix = args[2];
            } else {
                prefix = "/";
            }
            
            File inDir = new File(input);
            if (!inDir.exists() || !inDir.isDirectory()) {
                System.err.println(input + " is not a directory");
                usage();
            }
            
            File outDir = new File(output);
            if (outDir.exists() && !outDir.isDirectory()) {
                System.err.println(output + " is not a directory");
                usage();
            } else if (!outDir.exists()) {
                outDir.mkdir();
            }
            
            CodeProcessor formatter = new CodeProcessor();
            formatter.run(inDir, outDir, prefix);
            
            System.exit(0);
        } catch (ArrayIndexOutOfBoundsException exn) {
            usage();
        }
    }
}
