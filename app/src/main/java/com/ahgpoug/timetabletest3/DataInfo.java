package com.ahgpoug.timetabletest3;

import java.io.Serializable;

public class DataInfo implements Serializable
{
    private String lessonName;
    private String lessonType;
    private String roomNumber;
    private String teacherName;
    private String addressText;

    public DataInfo(String lName, String lType, String roomN, String tName, String aText){
        this.lessonName = lName;
        this.lessonType = lType;
        this.roomNumber = roomN;
        this.teacherName = tName;
        this.addressText = aText;
    }

    public DataInfo(){

    }

    public String getLessonName(){
        return lessonName;
    }

    public String getLessonType(){
        return lessonType;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getTeacherName(){
        return teacherName;
    }

    public String getAddressText(){
        return addressText;
    }

    public void setLessonType(String lType){
        this.lessonType = lType;
    }

    public void setLessonName(String lName){
        this.lessonName = lName;
    }

    public void setTeacherName(String tName){
        this.teacherName = tName;
    }

    public void setAddressText(String aText){
        this.addressText = aText;
    }

    public void setRoomNumber(String roomN){
        this.roomNumber = roomN;
    }
}
