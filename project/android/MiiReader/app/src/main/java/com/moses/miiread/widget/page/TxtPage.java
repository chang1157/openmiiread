package com.moses.miiread.widget.page;

import java.util.ArrayList;
import java.util.List;

/**
 * 页面
 */

class TxtPage {
    public int position;
    public String title;
    public int titleLines; //当前 lines 中为 title 的行数。
    public List<String> lines = new ArrayList<>();

    TxtPage(int position) {
        this.position = position;
    }

    int getPosition() {
        return position;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    int getTitleLines() {
        return titleLines;
    }

    void setTitleLines(int titleLines) {
        this.titleLines = titleLines;
    }

    String getContent() {
        StringBuilder s = new StringBuilder();
        if (lines != null) {
            for (int i = 0; i < lines.size(); i++) {
                s.append(lines.get(i));
            }
        }
        return s.toString();
    }

    void addLine(String line) {
        lines.add(line);
    }

    void addLines(List<String> lines) {
        this.lines.addAll(lines);
    }

    String getLine(int i) {
        return lines.get(i);
    }

    List<String> getLines() {
        return lines;
    }

    int size() {
        return lines.size();
    }
}