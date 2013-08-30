/**
 * 
 */
package com.vendsy.bartsy.venue.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.vendsy.bartsy.venue.dialog.ProfileDialogFragment;
import com.vendsy.bartsy.venue.BartsyApplication;
import com.vendsy.bartsy.venue.MainActivity;
import com.vendsy.bartsy.venue.R;
import com.vendsy.bartsy.venue.adapter.PeopleListAdapter;

/**
 * @author peterkellis
 *
 */
public class PeopleSectionFragment extends Fragment{

	View mRootView = null;
	LayoutInflater mInflater = null;
	ViewGroup mContainer = null;
	ListView mPeopleListView = null;
	public BartsyApplication mApp = null;
	private PeopleListAdapter mPeopleAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
 
		Log.v("Bartsy", "PeopleSectionFragment.onCreateView()");

		mInflater = inflater;
		mContainer = container; 
		mRootView = inflater.inflate(R.layout.users_main, container, false);
		mPeopleListView = (ListView) mRootView.findViewById(R.id.view_singles);
		
		// Make sure the fragment pointed to by the activity is accurate
		mApp = (BartsyApplication) getActivity().getApplication();
		((MainActivity) getActivity()).mPeopleFragment = this;		
		
		mPeopleAdapter = new PeopleListAdapter(getActivity(), R.layout.customer_details, mApp.mPeople);
		mPeopleListView.setAdapter(mPeopleAdapter);
		mPeopleListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg) {
				ProfileDialogFragment dialog = new ProfileDialogFragment();
				dialog.mUser = mApp.mPeople.get(position) ;
//				dialog.show(getActivity().getFragmentManager(),"ProfileDialogFragment");
			}
		});

		updatePeopleView();
        
        return mRootView;
	}
	
	/**
	 * Updates the people view from scratch
	 */
	
	synchronized public void updatePeopleView () {
		
		Log.v("Bartsy", "About to update people list view");

		if (mPeopleListView == null)
			return;

		Log.v("Bartsy", "mApp.mPeople list size = " + mApp.mPeople.size());
		
		mPeopleAdapter.notifyDataSetChanged();
	}
	
	@Override 
	public void onDestroy() {
		super.onDestroy();
		
		Log.v("Bartsy", "PeopleSectionFragment.onDestroy()");

		mRootView = null;
		mPeopleListView = null;
		mInflater = null;
		mContainer = null;

		// Because the fragment may be destroyed while the activity persists, remove pointer from activity
		((MainActivity) getActivity()).mPeopleFragment = null;
	}
	
}
