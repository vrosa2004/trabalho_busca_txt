package org.example;

public class TextFile {
    private final String filename;
    private final String[] lines;

    public TextFile(String filename, String[] lines) {
        this.filename = filename;
        this.lines = lines;
    }

    public String getFilename() {
        return filename;
    }

    public String[] getLines() {
        return lines;
    }
}
