package com.ahgpoug.timetabletest3;

public class ScheduleInfo {
    private String startT;
    private String endT;

    public ScheduleInfo(String _startT, String _endT){
        this.startT = _startT;
        this.endT = _endT;
    }

    public String getStartT(){
        return startT;
    }

    public String getEndT(){
        return endT;
    }
}
