package org.lealone.examples.h2webconsole.service;

public class TableInfo {
    int id;
    String name;
    String columns;

    public TableInfo(int id, String name, String columns) {
        super();
        this.id = id;
        this.name = name;
        this.columns = columns;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }
}
