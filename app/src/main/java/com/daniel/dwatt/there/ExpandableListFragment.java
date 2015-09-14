package com.daniel.dwatt.there;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;


public class ExpandableListFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private ImageButton addAlarmButton;
    private static final String ARG_PARAM2 = "param2";
    private AnimatedExpandableListView elv;
    private int lastExpandedPosition = -1;
    private AlarmDataSource dataSource;
    private List<Alarm> listAlarm;
    private Context context;
    private CustomListAdapter elAdapter;


    private HashMap<ListGroupObject, ListChildObject> childItems;
    private ArrayList<ListGroupObject> groupItems;

    //Interface
    private ExpandableListViewListener activityCommander;

    public void callRingtonePicker(int position){
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        this.startActivityForResult(intent, position);
    }

    @Override
    public void onActivityResult(int groupPosition, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && groupPosition >= 0)
        {
            super.onActivityResult(groupPosition, resultCode, data);
            Uri newRingtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            elAdapter.updateAlarmRingTone(groupPosition,newRingtone);

        }
    }

    public static ExpandableListFragment newInstance(String param1, String param2) {
        ExpandableListFragment fragment = new ExpandableListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public ExpandableListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateAlarmList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expandable_list_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        elAdapter = new CustomListAdapter(groupItems, childItems, getActivity(),this);
        elv = (AnimatedExpandableListView) view.findViewById(R.id.elv);
        elv.setIndicatorBounds(elv.getRight() - 40, elv.getWidth());
        elv.setAdapter(elAdapter);
        elv.setGroupIndicator(null);


        elv.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (elv.isGroupExpanded(groupPosition)) {
                    elv.collapseGroupWithAnimation(groupPosition);
                } else {
                    elv.expandGroupWithAnimation(groupPosition);
                }
                elv.refreshDrawableState();
                return true;
            }

        });

        elv.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    elv.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
                elv.setItemChecked(groupPosition, true);

            }
        });
        elv.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                elv.setItemChecked(groupPosition, false);

            }
        });

        elv.setOnChildClickListener(new OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                elv.collapseGroupWithAnimation(groupPosition);
                elv.refreshDrawableState();
                return true;
            }
        });

        addAlarmButton = (ImageButton) view.findViewById(R.id.addAlarmButton);

        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityCommander.launchEditor();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);
        try {
            activityCommander = (ExpandableListViewListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activityCommander = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ExpandableListViewListener {
        public void launchEditor();
    }

    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    public static void expand(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    private void updateAlarmList(){

        try{
            dataSource = new AlarmDataSource(getActivity());
            dataSource.Open();
            listAlarm = dataSource.GetAllAlarms();
            dataSource.Close();
        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        groupItems = new ArrayList<ListGroupObject>();
        childItems = new HashMap<ListGroupObject, ListChildObject>();
        int listSize = 0;
        if(listAlarm != null) {

           listSize = listAlarm.size();
        }
            for (int i = 0; i<listSize; i++)
        {
            groupItems.add(new ListGroupObject(listAlarm.get(i).getAlarmObject().getLocationObject().getShortname(),
                    listAlarm.get(i).getAlarmObject().getLocationObject().getLocality(),
                    listAlarm.get(i).getAlarmObject().isActive(),listAlarm.get(i)));

            childItems.put(groupItems.get(i), new ListChildObject(listAlarm.get(i).getAlarmObject().getLocationObject().getAddress(),
                    listAlarm.get(i).getAlarmObject().getRingtoneTitle(),
                    listAlarm.get(i).getAlarmObject().isRepeat(),
                    listAlarm.get(i).getAlarmObject().isVibrate()));
        }



    }


}
