package com.example.singheart.entity;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String Col;
    private String CourseName;
    private String Position;
    private String Row;
    private String Week;
    private int span;
    public List<Integer> Week_NUM;

    private void tidyWeek() {
        Week_NUM = new ArrayList<Integer>();
        for (int i = 0; i < Week.length(); i++) {
            String s = Week.substring(i, i + 1);
            if (s.equals("1")) {
                Week_NUM.add(new Integer(i));
            }
        }
    }

    public int getCol() {
        return Integer.parseInt(this.Col);
    }

    public String getCourseName() {
        return this.CourseName;
    }

    public String getPosition() {
        return this.Position;
    }

    public int getRow() {
        return Integer.parseInt(this.Row);
    }

    public String getWeek() {
        return this.Week;
    }

    public void setCol(String Col) {
        this.Col = Col;
    }

    public void setCourseName(String CourseName) {
        this.CourseName = CourseName;
    }

    public void setPosition(String Position) {
        this.Position = Position;
    }

    public void setRow(String Row) {
        this.Row = Row;
    }

    public void setWeek(String Week) {
        this.Week = Week;
    }

    public void tidyDate() {
        tidyWeek();
    }

    public void setSpan(int span) {
        this.span = span;
    }

    public int getSpan() {
        return span;
    }
}