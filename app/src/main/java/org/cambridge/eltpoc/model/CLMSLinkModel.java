package org.cambridge.eltpoc.model;

/**
 * Created by etorres on 6/26/15.
 */
public class CLMSLinkModel extends CLMSModel {
    private String webLink;
    private String className;
    private String contentName;

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }
}
