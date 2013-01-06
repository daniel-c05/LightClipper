package com.deadpixels.light.clipper;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.Toast;

import com.deadpixels.light.clipper.adapters.ClipsAdapter;
import com.deadpixels.light.clipper.utils.ClipHelper;

public class Home extends Activity {

	public static final String TAG = "Light Clipper";
	public static final String FILE_CLIPS = "clips.txt";
	public static final int MAX_CLIP_ITEMS = 10;

	private ListView clipList;

	private ArrayList<String> recentClips;
	private ClipsAdapter mAdapter;
	public boolean isOldAPI = true;
	private ArrayList<String> itemsChecked;
	boolean inActionMode = false;
	
	/**
	 * This is the Listener to be used when in ActionMode. This will only be set to the listView when the API of the device is higher than 11 (HoneyComb) as otherwise it would have a runtime error. 
	 */
	private MultiChoiceModeListener modeListener = new MultiChoiceModeListener() {
		
		 @SuppressLint("NewApi")
		@Override
		    public void onItemCheckedStateChanged(ActionMode mode, int position,
		                                          long id, boolean checked) {	
			 	String text =  recentClips.get(position);
		    	if (checked) {
		    		itemsChecked.add(text);
				}
		    	else {
		    		int index = itemsChecked.indexOf(text);
		    		itemsChecked.remove(index);
		    	}		    	
		    }

		    @SuppressLint("NewApi")
			@Override
		    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		        switch (item.getItemId()) {
		            case 0:
		                mode.finish();
		                return true;
		            case R.id.menu_merge:
		            	return false;		         
		            case R.id.menu_star:
		            	return false;		         
		            default:
		                return false;
		        }
		    }

		    @SuppressLint("NewApi")
			@Override
		    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		    	inActionMode = true;
		    	itemsChecked = new ArrayList<String>();
		        MenuInflater inflater = mode.getMenuInflater();
		        inflater.inflate(R.menu.cliplist_context_menu, menu);
		        return true;
		    }

		    @SuppressLint("NewApi")
			@Override
		    public void onDestroyActionMode(ActionMode mode) {
		    	inActionMode = false;
		    }

		    @SuppressLint("NewApi")
			@Override
		    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		        return false;
		    }
	};
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
		/*
		 * This will be called only when on APIs lower than 11. 
		 */
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.cliplist_context_menu, menu);
	}	

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    //AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.menu_merge:
	            //mergeAndAddToClip();
	            return true;
	        case R.id.menu_star:
	            
	            return false;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(TAG, "Calling onCreate");

		setContentView(R.layout.activity_home);

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			isOldAPI = false;
		}
		
		Intent intent = getIntent();

		if (intent.getExtras() != null) {
			String action = getIntent().getAction();
			String data = "";
			if (intent.hasExtra(Intent.EXTRA_TEXT)) {
				data = getIntent().getStringExtra(Intent.EXTRA_TEXT);
				ClipHelper.addItemToClipboard(this, action, data, isOldAPI);
				Toast.makeText(this, "Data copied to clipboard", Toast.LENGTH_SHORT).show();
				this.finish();
			}
		}

		recentClips = new ArrayList<String>();		
		recentClips = ClipHelper.getSavedClips(this);		

		initViews();			

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getLastItemFromClipboard(isOldAPI, false);
	}

	@Override
	protected void onStop() {
		Log.v(TAG, "Stopping");
		ClipHelper.saveClips(this, recentClips);		
		super.onStop();
	}

	@SuppressLint("NewApi")
	private void initViews () {

		clipList = (ListView) findViewById(R.id.home_clip_list);
		/* Not adding Context Menu for now, no use for it. 
		if (isOldAPI) {
			registerForContextMenu(clipList);
		}
		else {
			clipList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			clipList.setMultiChoiceModeListener(modeListener);			
		}
		*/
		mAdapter = new ClipsAdapter(this, recentClips, isOldAPI);				
		clipList.setAdapter(mAdapter);		
	}

	public void getLastItemFromClipboard (boolean oldAPI, boolean toast) {
		String lastClip = ClipHelper.getLastClip(this, oldAPI);
		if (lastClip != "" && !recentClips.contains(lastClip)) {
			recentClips.add(lastClip);
			mAdapter.addItem(lastClip);
			mAdapter.notifyDataSetChanged();
		}

		if (toast) {
			Toast.makeText(this,"Text: " + lastClip, Toast.LENGTH_LONG).show();
		}
	}

}