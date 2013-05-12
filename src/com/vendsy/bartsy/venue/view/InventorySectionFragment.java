/**
 * 
 */
package com.vendsy.bartsy.venue.view;

import com.vendsy.bartsy.venue.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author peterkellis
 *
 */
public class InventorySectionFragment extends Fragment {

	View mRootView = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
 
		mRootView = inflater.inflate(R.layout.inventory_main, container, false);
		
        return mRootView;
	}
	
	
}
