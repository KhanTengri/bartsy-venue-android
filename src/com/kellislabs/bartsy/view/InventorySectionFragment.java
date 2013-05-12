/**
 * 
 */
package com.kellislabs.bartsy.view;

import com.kellislabs.bartsy.R;
import com.kellislabs.bartsy.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ToggleButton;

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
