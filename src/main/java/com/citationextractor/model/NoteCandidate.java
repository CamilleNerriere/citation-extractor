package com.citationextractor.model;

public class NoteCandidate {
    private final String text;
    private final int page;
    private final float x;
    private final float y;

    public NoteCandidate(final String text, final int page, final float x, final float y) {
        this.text = text;
        this.page = page;
        this.x = x;
        this.y = y;
    }

    public String getText() {
        return text;
    }

    public int getPage(){
        return page;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return "NoteCandidate{" +
                "text='" + text + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}

