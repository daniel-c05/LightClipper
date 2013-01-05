package com.deadpixels.light.clipper;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.deadpixels.light.clipper.adapters.ClipsAdapter;
import com.deadpixels.light.clipper.utils.ClipHelper;

public class Home extends Activity {

	public static final String TAG = "Light Clipper";
	public static final String FILE_CLIPS = "clips.txt";
	public static final int MAX_CLIP_ITEMS = 10;

	private Button buttonGetLastClip, buttonClearClips;
	private ListView clipList;

	private ArrayList<String> recentClips;
	private ClipsAdapter mAdapter;
	public boolean isOldAPI = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.v(TAG, "Calling onCreate");

		setContentView(R.layout.activity_home);

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			isOldAPI = false;
		}

		recentClips = new ArrayList<String>();		
		ClipHelper.getSavedClips(this);		

		initViews();				
		getLastItemFromClipboard(isOldAPI, false);

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

	}

	@Override
	protected void onStop() {
		Log.v(TAG, "Stopping");
		ClipHelper.saveClips(this, recentClips);		
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_home, menu);
		return true;
	}

	private void initViews () {

		clipList = (ListView) findViewById(R.id.home_clip_list);
		mAdapter = new ClipsAdapter(this, recentClips, isOldAPI);
		clipList.setAdapter(mAdapter);


		buttonGetLastClip = (Button) findViewById(R.id.button_get_last_clip);
		buttonGetLastClip.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getLastItemFromClipboard(isOldAPI, true);
			}
		});

		buttonClearClips = (Button) findViewById(R.id.button_clear_clips);
		buttonClearClips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clearClips();
			}
		});
	}

	public void clearClips() {
		for (String clip : recentClips) {
			mAdapter.removeItem(clip);
		}
		mAdapter.notifyDataSetChanged();
		ClipHelper.clearAll(this);
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