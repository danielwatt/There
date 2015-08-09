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
import android.view.MotionEvent;
import java.util.HashMap;
import com.daniel.dwatt.there.AnimatedExpandableListView;
import com.daniel.dwatt.there.AnimatedExpandableListView.AnimatedExpandableListAdapter;



public class CustomListAdapter extends AnimatedExpandableListAdapter {

    private LayoutInflater inflater;
    private HashMap<GroupObject,ChildObject> childItems;
    private ArrayList<GroupObject> groupItems;
    private Context context;

    public CustomListAdapter(ArrayList<GroupObject> groupItems, HashMap<GroupObject,ChildObject> childItems, Context context){
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

        return convertView;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildObject childObj = getChild(groupPosition, childPosition);
        if (convertView == null){
            convertView = inflater.inflate(R.layout.customlistview_child, parent, false);
        }
        /*Initializing objects in child*/
        TextView addressText = (TextView) convertView.findViewById(R.id.addressText);
        TextView ringToneText = (TextView) convertView.findViewById(R.id.ringToneText);
        CheckBox vibratechbx = (CheckBox) convertView.findViewById(R.id.vibratechbx);
        CheckBox repeatchbx = (CheckBox) convertView.findViewById(R.id.repeatchbx);

        addressText.setText(childObj.getAddressText());
        ringToneText.setText(childObj.getRingToneText());
        vibratechbx.setChecked(childObj.isVibrateOn());
        repeatchbx.setChecked(childObj.isRepeatOn());

        return convertView;
    }

    @Override
    public int getGroupCount() {

        return groupItems.size();
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {

        return 1;
    }

    @Override
    public boolean hasStableIds() {

        return false;
    }
    
    @Override
    public ChildObject getChild(int groupPosition, int childPosition) {

        return this.childItems.get(this.groupItems.get(groupPosition));
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
