package org.lealone.examples.h2webconsole.service;

public class NodeInfo {
    int id;
    int level;
    int type;
    String icon;
    String text;
    String link;

    public NodeInfo(int id, int level, int type, String icon, String text, String link) {
        super();
        this.id = id;
        this.level = level;
        this.type = type;
        this.icon = icon;
        this.text = text;
        this.link = link;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
