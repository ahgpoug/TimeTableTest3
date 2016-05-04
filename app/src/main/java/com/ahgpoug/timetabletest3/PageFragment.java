package com.ahgpoug.timetabletest3;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class PageFragment extends Fragment {

    private int pageNumber;
    public static View vArr[] = new View[7];

    public static PageFragment newInstance(int page) {
        PageFragment fragment = new PageFragment();
        Bundle args=new Bundle();
        args.putInt("num", page);
        fragment.setArguments(args);
        return fragment;
    }

    public PageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    static String getTitle(Context ctxt, String day) {
        return day;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_main, container, false);

        vArr[pageNumber] = result;

        if (GlobalVariables.count == 0) {
            DataBaseIO.Read();
            GlobalVariables.count++;
        }

        ArrayList<String> lst = new ArrayList<String>();
        ListView lstView = (ListView) result.findViewById(R.id.listView);
        ArrayAdapter<String> adapterN = new ArrayAdapter<String>(MyAdapter.getContext(), android.R.layout.simple_list_item_1, lst);
        lstView.setAdapter(adapterN);

        if (GlobalVariables.weekType.equals("Red"))
            for (int i = 0; i < GlobalVariables.mListRed.get(pageNumber).size(); i++) {
                DataInfo dtI = GlobalVariables.mListRed.get(pageNumber).get(i);
                if (dtI.getLessonType() == null)
                    dtI.setLessonType("");
                if (dtI.getLessonName() == null)
                    dtI.setLessonName("");
                if (dtI.getTeacherName() == null)
                    dtI.setTeacherName("");
                if (dtI.getRoomNumber() == null)
                    dtI.setRoomNumber("");
                if (dtI.getAddressText() == null)
                    dtI.setAddressText("");

                if (GlobalVariables.scheduleList.size() < GlobalVariables.mListRed.get(pageNumber).size())
                    for (int k = GlobalVariables.scheduleList.size(); k < GlobalVariables.mListRed.get(pageNumber).size(); k++)
                        GlobalVariables.scheduleList.add(new ScheduleInfo("", ""));
                String str = String.format("%d пара     %s - %s\n\n%s\n%s\n%s\n%s\n%s", i + 1, GlobalVariables.scheduleList.get(i).getStartT(), GlobalVariables.scheduleList.get(i).getEndT(), dtI.getLessonType(), dtI.getLessonName(), dtI.getRoomNumber(), dtI.getTeacherName(), dtI.getAddressText());
                lst.add(str);
                adapterN.notifyDataSetChanged();
            }
        else
            for (int i = 0; i < GlobalVariables.mListGreen.get(pageNumber).size(); i++) {
                DataInfo dtI = GlobalVariables.mListGreen.get(pageNumber).get(i);
                if (dtI.getLessonType() == null)
                    dtI.setLessonType("");
                if (dtI.getLessonName() == null)
                    dtI.setLessonName("");
                if (dtI.getTeacherName() == null)
                    dtI.setTeacherName("");
                if (dtI.getRoomNumber() == null)
                    dtI.setRoomNumber("");
                if (dtI.getAddressText() == null)
                    dtI.setAddressText("");

                if (GlobalVariables.scheduleList.size() < GlobalVariables.mListGreen.get(pageNumber).size())
                    for (int k = GlobalVariables.scheduleList.size(); k < GlobalVariables.mListGreen.get(pageNumber).size(); k++)
                        GlobalVariables.scheduleList.add(new ScheduleInfo("", ""));
                String str = String.format("%d пара     %s - %s\n\n%s\n%s\n%s\n%s\n%s", i + 1, GlobalVariables.scheduleList.get(i).getStartT(), GlobalVariables.scheduleList.get(i).getEndT(), dtI.getLessonType(), dtI.getLessonName(), dtI.getRoomNumber(), dtI.getTeacherName(), dtI.getAddressText());
                lst.add(str);
                adapterN.notifyDataSetChanged();
            }
        GlobalVariables.count++;
        return result;
    }
}
