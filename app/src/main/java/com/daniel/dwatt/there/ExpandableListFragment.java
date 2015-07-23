package com.daniel.dwatt.there;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpandableListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpandableListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpandableListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private ArrayList<ChildObject> childItems;
    private ArrayList<GroupObject> groupItems;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpandableListView.
     */
    // TODO: Rename and change types and number of parameters
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
        childItems = new ArrayList<ChildObject>();
        childItems.add(new ChildObject("Address 1","Ringtone 1", true, false));
        childItems.add(new ChildObject("Address 2","Ringtone 2", false, true));
        childItems.add(new ChildObject("Address 3", "Ringtone 3", false, false));

        groupItems = new ArrayList<GroupObject>();
        groupItems.add(new GroupObject("Title 1","City 1", true, childItems.get(0)));
        groupItems.add(new GroupObject("Title 2","City 2", false, childItems.get(1)));
        groupItems.add(new GroupObject("Title 3", "City 3", true, childItems.get(2)));


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
        final ExpandableListView elv = (ExpandableListView) view.findViewById(R.id.elv);
        elv.setIndicatorBounds(elv.getRight() - 40, elv.getWidth());
        elv.setAdapter(elAdapter);
        elv.setGroupIndicator(null);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
