package com.jkapps.sqlitelistview;

public class Model {
    private int id;
    private String title;
    private String content;
    private byte[] image;

    public Model (int id, String title, String content, byte[] image) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public byte[] getImage() {
        return image;
    }
}
