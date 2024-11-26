package com.example.seg2105_project_grp_33;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class EventList extends ArrayAdapter<EventType> {
    private Activity context;
    List<EventType> eventList;

    public EventList(Activity context, List<EventType> events) {
        super(context, R.layout.layout_event_list, events);
        this.context = context;
        this.eventList = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
//        if(convertView == null) {
//            convertView = inflater.inflate(R.layout.layout_user_list, parent, false);
//        }

        View listViewItem = inflater.inflate(R.layout.layout_event_list, null, true);

        TextView textViewType = (TextView) listViewItem.findViewById(R.id.textViewType);
        TextView textViewDescription = (TextView) listViewItem.findViewById(R.id.textViewDescription);
        TextView textViewPace = (TextView) listViewItem.findViewById(R.id.textViewPace);


        EventType event = eventList.get(position);
        textViewType.setText(event.getType());
        textViewDescription.setText(event.getDescription());
        textViewPace.setText(event.getPace());

        return listViewItem;
    }
}