package com.daniel.dwatt.there;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Switch;
import android.view.View.OnTouchListener;


public class CustomListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater;
    private ArrayList<ChildObject> childItems;
    private ArrayList<GroupObject> groupItems;
    private Context context;

    public CustomListAdapter(ArrayList<GroupObject> groupItems, ArrayList<ChildObject> childItems, Context context){
        this.groupItems = groupItems;
        this.childItems = childItems;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = inflater.inflate(R.layout.customlistview_group, parent, false);
        }

        TextView titleText = (TextView) convertView.findViewById(R.id.titleText);
        TextView cityText = (TextView) convertView.findViewById(R.id.cityText);
        Switch alarmSwitch = (Switch) convertView.findViewById(R.id.alarmSwitch);

        titleText.setText(groupItems.get(groupPosition).getTitleText());
        cityText.setText(groupItems.get(groupPosition).getCityText());
        alarmSwitch.setChecked(groupItems.get(groupPosition).isAlarmOn());

        if (isExpanded)
        {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.secondary_bg_color));

        }
        else
        {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.main_bg_color));
        }

        convertView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setBackgroundColor(context.getResources().getColor(R.color.highlight_color));
                return false;
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = inflater.inflate(R.layout.customlistview_child, parent, false);
        }
        /*Initializing objects in child*/
        TextView addressText = (TextView) convertView.findViewById(R.id.addressText);
        TextView ringToneText = (TextView) convertView.findViewById(R.id.ringToneText);
        CheckBox vibratechbx = (CheckBox) convertView.findViewById(R.id.vibratechbx);
        CheckBox repeatchbx = (CheckBox) convertView.findViewById(R.id.repeatchbx);

        addressText.setText(childItems.get(childPosition).getAddressText());
        ringToneText.setText(childItems.get(childPosition).getRingToneText());
        vibratechbx.setChecked(childItems.get(childPosition).isVibrateOn());
        repeatchbx.setChecked(childItems.get(childPosition).isRepeatOn());

        convertView.setBackgroundColor(context.getResources().getColor(R.color.secondary_bg_color));

        convertView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setBackgroundColor(context.getResources().getColor(R.color.highlight_color));
                return false;
            }
        });

        return convertView;
    }

    @Override
    public int getGroupCount() {

        return groupItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        return this.childItems.size();
    }

    @Override
    public boolean hasStableIds() {

        return false;
    }
    
    @Override
    public ChildObject getChild(int groupPosition, int childPosition) {

        return groupItems.get(groupPosition).getChildObject();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return groupItems.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {

        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return childPosition;
    }

}
