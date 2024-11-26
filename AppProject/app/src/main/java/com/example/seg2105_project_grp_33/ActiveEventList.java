package com.example.seg2105_project_grp_33;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class ActiveEventList extends ArrayAdapter<Event> {

    private Activity context;

    List<Event> scheduledEvents;

    public ActiveEventList(Activity context, List<Event> events){
        super(context, R.layout.layout_active_event_list, events);
        this.context = context;
        this.scheduledEvents = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView == null){
            convertView = inflater.inflate(R.layout.layout_active_event_list, parent, false);
        }

        TextView eventType = convertView.findViewById(R.id.eventTypeTextField);
        TextView eventDate = convertView.findViewById(R.id.eventDateTextField);
        TextView eventLocation = convertView.findViewById(R.id.eventLocationTextField);
        TextView eventCapacity = convertView.findViewById(R.id.eventCapacityTextField);
        TextView eventResgistrants = convertView.findViewById(R.id.eventRegistrantsTextField);


        Event event = scheduledEvents.get(position);

        eventType.setText(event.toString());
        eventDate.setText("Date: "+event.getDate());
        eventLocation.setText("Location: " + event.getLocation());
        eventCapacity.setText("Participant Capacity: " + event.getCapacity());
        eventResgistrants.setText("Number of Participants: "+event.getNumParticipants());

        return convertView;

    }



}