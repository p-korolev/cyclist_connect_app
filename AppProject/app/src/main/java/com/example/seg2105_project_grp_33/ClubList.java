package com.example.seg2105_project_grp_33;

import static com.example.seg2105_project_grp_33.R.layout.layout_club_list;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ClubList extends ArrayAdapter<Club> {

    private Activity context;

    List<Club> clubs;

    public ClubList(Activity context, List<Club> clubs1) {
        super(context, layout_club_list, clubs1);
        this.context = context;
        this.clubs = clubs1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.layout_club_list, parent,false);
        }
        TextView clubName = convertView.findViewById(R.id.clubNameTextField);

        Club club = clubs.get(position);

        clubName.setText(club.getName());

        return convertView;
    }
}
