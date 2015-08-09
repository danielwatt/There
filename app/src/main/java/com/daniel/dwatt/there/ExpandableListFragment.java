package com.daniel.dwatt.there;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;

import java.util.HashMap;
import java.util.ArrayList;

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


    private HashMap<GroupObject, ChildObject> childItems;
    private ArrayList<GroupObject> groupItems;

    //Interface
    private ExpandableListViewListener activityCommander;

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

        //Temporary. Eventually read from MySQL DB. Just for testing purposes
        groupItems = new ArrayList<GroupObject>();
        groupItems.add(new GroupObject("Title 1", "City 1", true));
        groupItems.add(new GroupObject("Title 2", "City 2", false));
        groupItems.add(new GroupObject("Title 3", "City 3", true));
        groupItems.add(new GroupObject("Title 4", "City 4", true));
        groupItems.add(new GroupObject("Title 5", "City 5", true));
        groupItems.add(new GroupObject("Title 6", "City 6", true));

        childItems = new HashMap<GroupObject, ChildObject>();
        childItems.put(groupItems.get(0), new ChildObject("Address 1", "Ringtone 1", true, false));
        childItems.put(groupItems.get(1), new ChildObject("Address 2", "Ringtone 2", false, true));
        childItems.put(groupItems.get(2), new ChildObject("Address 3", "Ringtone 3", false, false));
        childItems.put(groupItems.get(3), new ChildObject("Address 4", "Ringtone 4", false, false));
        childItems.put(groupItems.get(4), new ChildObject("Address 5", "Ringtone 5", false, false));
        childItems.put(groupItems.get(5), new ChildObject("Address 6", "Ringtone 6", false, false));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expandable_list_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CustomListAdapter elAdapter = new CustomListAdapter(groupItems, childItems, getActivity());
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
                return true;
            }
        });

        addAlarmButton = (ImageButton) view.findViewById(R.id.addAlarmButton);

        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityCommander.LaunchPopUpEditor();
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
        public void LaunchPopUpEditor();
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


}
