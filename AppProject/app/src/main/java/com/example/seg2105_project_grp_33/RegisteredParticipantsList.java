package com.example.seg2105_project_grp_33;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class RegisteredParticipantsList extends ArrayAdapter<String> {

    private Context context;

    List<String> Participants;

    public RegisteredParticipantsList(Context context, List<String> participants){
        super(context, R.layout.layout_registered_participants, participants);
        this.context = context;
        this.Participants = participants;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_registered_participants, parent, false);
        }
        TextView username = convertView.findViewById(R.id.participantUserNameTextField);
        String un = Participants.get(position);
        username.setText(un);
        return convertView;
    }
}
