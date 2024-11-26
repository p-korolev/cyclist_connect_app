package com.example.seg2105_project_grp_33;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UserList extends ArrayAdapter<User> {
    private final Activity context;
    List<User> userList;

    public UserList(Activity context, List<User> users) {
        super(context, R.layout.layout_user_list, users);
        this.context = context;
        this.userList = users;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.layout_user_list, parent, false);
        }

        TextView textViewUsername = convertView.findViewById(R.id.textViewUsername);
        TextView textViewRole = convertView.findViewById(R.id.textViewRole);

        User user = userList.get(position);

        textViewUsername.setText(user.getUsername());
        if(user instanceof ClubOwner) {
            textViewRole.setText("Club Owner");
        } else if (user instanceof Participant) {
            textViewRole.setText("Participant");
        }
        return convertView;
    }
}