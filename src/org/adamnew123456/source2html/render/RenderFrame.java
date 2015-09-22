package org.adamnew123456.source2html.render;

import org.stringtemplate.v4.ST;

/**
 * RenderFrame is responsible for rendering the area in which the code 
 * appears, as well as the sidebar used to navigate between code files.
 */
public class RenderFrame implements Renderable {
    private String SIDEBAR_WIDTH = "215px";
    private Renderable packageList;
    
    private static ST frameTemplate = new ST(
        "<div class=\"sidebar\">{pkgList}</div>"
        + "<iframe id=\"codeview\" class=\"codeview\" ></iframe>",
        '{', '}');
    
    private static ST cssTemplate = new ST(
        " body { background: black; color: white}\n"
        + ".sidebar { position: fixed; left: 0; top: 0; "
        + "           width: <sidebarWidth>; height: 100%; "
        + "           background: lightgray; "
        + "           overflow-y: scroll }\n"
        + ".codeview { width: 100%; height: 100% ; margin-left: <sidebarWidth> }\n"
        + "<pkgList>");
    
    private static ST jsTemplate = new ST(
        "function loadCode(url) { document.getElementById('codeview').src = url; }\n"
        + "<pkgList>");
    
    public RenderFrame(Renderable pkglist) {
        packageList = pkglist;
    }
    
    @Override
    public String toHTML() {
        ST frameInstance = new ST(frameTemplate);
        frameInstance.add("pkgList", packageList.toHTML());
        return frameInstance.render();
    }

    @Override
    public String toCSS() {
        ST cssInstance = new ST(cssTemplate);
        cssInstance.add("sidebarWidth", SIDEBAR_WIDTH);
        cssInstance.add("pkgList", packageList.toCSS());
        return cssInstance.render();
    }

    @Override
    public String toJavascript() {
        ST jsInstance = new ST(jsTemplate);
        jsInstance.add("pkgList", packageList.toJavascript());
        return jsInstance.render();
    }
}
