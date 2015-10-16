package com.daniel.dwatt.there;

import java.sql.SQLException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.Switch;

import java.util.HashMap;

import com.daniel.dwatt.there.AnimatedExpandableListView.AnimatedExpandableListAdapter;


public class CustomListAdapter extends AnimatedExpandableListAdapter {

    private LayoutInflater inflater;
    private HashMap<ListGroupObject, ListChildObject> childItems;
    private ArrayList<ListGroupObject> groupItems;
    private Context context;
    private Intent serviceIntent;
    private ExpandableListFragment elfragment;

    public CustomListAdapter(ArrayList<ListGroupObject> groupItems, HashMap<ListGroupObject, ListChildObject> childItems, Context context, ExpandableListFragment elfragment) {
        this.groupItems = groupItems;
        this.childItems = childItems;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.elfragment = elfragment;
        startAlarmService();
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }

    static class ViewHolderItem {
        TextView titleText;
        TextView cityText;
        Switch alarmSwitch;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolderItem;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customlistview_group, parent, false);
            viewHolderItem = new ViewHolderItem();
            viewHolderItem.titleText = (TextView) convertView.findViewById(R.id.titleText);
            viewHolderItem.cityText = (TextView) convertView.findViewById(R.id.cityText);
            viewHolderItem.alarmSwitch = (Switch) convertView.findViewById(R.id.alarmSwitch);

            convertView.setTag(viewHolderItem);
        }else
        {
            viewHolderItem = (ViewHolderItem) convertView.getTag();
        }


        viewHolderItem.titleText.setText(groupItems.get(groupPosition).getTitleText());
        viewHolderItem.cityText.setText(groupItems.get(groupPosition).getCityText());
        viewHolderItem.alarmSwitch.setChecked(groupItems.get(groupPosition).isAlarmOn());

        viewHolderItem.titleText.setMaxLines(1);
        viewHolderItem.alarmSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = !groupItems.get(groupPosition).isAlarmOn();
                groupItems.get(groupPosition).setAlarmOn(isChecked);

                try {
                    AlarmDataSource dataSource;
                    dataSource = new AlarmDataSource(context);
                    dataSource.Open();
                    dataSource.setAlarmOn(groupItems.get(groupPosition).getAlarm(), isChecked);
                    //reset inFence after toggling AlarmOn
                    dataSource.setInFenceActive(groupItems.get(groupPosition).getAlarm(), false);
                    dataSource.Close();
                    notifyDataSetChanged();
                    startAlarmService();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        });

        return convertView;
    }

    @Override
    public View getRealChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ListChildObject childObj = getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customlistview_child, parent, false);
        }
        /*Initializing objects in child*/
        final TextView addressText = (TextView) convertView.findViewById(R.id.addressText);
        final TextView ringToneText = (TextView) convertView.findViewById(R.id.ringToneText);
        final CheckBox vibratechbx = (CheckBox) convertView.findViewById(R.id.vibratechbx);
        final CheckBox repeatchbx = (CheckBox) convertView.findViewById(R.id.repeatchbx);

        addressText.setText(childObj.getAddressText());

        ringToneText.setText(childObj.getRingToneText());
        vibratechbx.setChecked(childObj.isVibrateOn());
        repeatchbx.setChecked(childObj.isRepeatOn());

        RelativeLayout addressLayout = (RelativeLayout) convertView.findViewById(R.id.addressLayout);
        addressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(context, EditorActivity.class);
                editIntent.putExtra("com.daniel.dwatt.there.alarmedit", groupItems.get(groupPosition).getAlarm());
                context.startActivity(editIntent);
            }
        });

        RelativeLayout ringToneLayout = (RelativeLayout) convertView.findViewById(R.id.ringToneLayout);
        ringToneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elfragment.callRingtonePicker(groupPosition);
            }
        });

        RelativeLayout repeatLayout = (RelativeLayout) convertView.findViewById(R.id.repeatLayout);
        repeatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatchbx.toggle();
                boolean isChecked = repeatchbx.isChecked();
                childItems.get(groupItems.get(groupPosition)).setRepeatOn(isChecked);

                try {
                    AlarmDataSource dataSource;
                    dataSource = new AlarmDataSource(context);
                    dataSource.Open();
                    dataSource.setRepeatOn(groupItems.get(groupPosition).getAlarm(), isChecked);
                    dataSource.Close();
                    notifyDataSetChanged();
                    startAlarmService();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        RelativeLayout vibrateLayout = (RelativeLayout) convertView.findViewById(R.id.vibrateLayout);
        vibrateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibratechbx.toggle();
                boolean isChecked = vibratechbx.isChecked();
                childItems.get(groupItems.get(groupPosition)).setVibrateOn(isChecked);

                try {
                    AlarmDataSource dataSource;
                    dataSource = new AlarmDataSource(context);
                    dataSource.Open();
                    dataSource.setVibrateOn(groupItems.get(groupPosition).getAlarm(), isChecked);
                    dataSource.Close();
                    notifyDataSetChanged();
                    startAlarmService();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        });

        ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setMessage("Do you wish to delete '" + groupItems.get(groupPosition).getTitleText() + "'");
                dialog.setPositiveButton(context.getResources().getString(R.string.Delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        try {
                            AlarmDataSource dataSource;
                            dataSource = new AlarmDataSource(context);
                            dataSource.Open();
                            dataSource.deleteAlarm(groupItems.get(groupPosition).getAlarm());
                            dataSource.Close();
                            groupItems.remove(groupPosition);
                            notifyDataSetChanged();
                            startAlarmService();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dialog.setNegativeButton(context.getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
                dialog.show();


            }
        });


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
    public ListChildObject getChild(int groupPosition, int childPosition) {

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

    public void updateAlarmRingTone(int groupPosition, Uri uriRingtone) {

        String uriRingtoneString = "None";
        String ringtoneTitle = uriRingtoneString;

        if (uriRingtone != null) {
            Ringtone ringtone = RingtoneManager.getRingtone(context, uriRingtone);
            ringtoneTitle = ringtone.getTitle(context);
            uriRingtoneString = uriRingtone.toString();
            ringtone.stop();
        }

        childItems.get(groupItems.get(groupPosition)).setRingToneText(ringtoneTitle);

        try {
            AlarmDataSource dataSource;
            dataSource = new AlarmDataSource(context);
            dataSource.Open();
            dataSource.setRingtoneLocation(groupItems.get(groupPosition).getAlarm(), uriRingtoneString);
            dataSource.Close();
            notifyDataSetChanged();
            startAlarmService();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void startAlarmService() {
        context.startService(new Intent(context, AlarmService.class));
    }

}
