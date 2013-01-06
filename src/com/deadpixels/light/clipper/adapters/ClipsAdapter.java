package com.deadpixels.light.clipper.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.deadpixels.light.clipper.R;
import com.deadpixels.light.clipper.utils.ClipHelper;

public class ClipsAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private ArrayList<String> mClips;	
	private Context mContext;
	private boolean isOldApi;
	
	public ClipsAdapter (Context context, ArrayList<String> clips, boolean oldAPI) {
		mContext = context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		isOldApi = oldAPI;
		if (clips != null) {
			mClips = clips;
		}
		else {
			mClips = new ArrayList<String>();
		}
		
	}

	@Override
	public int getCount() {
		if (mClips == null) {
			return 0;
		}
		return mClips.size();
	}

	@Override
	public Object getItem(int pos) {		
		return mClips.get(pos);
	}
	
	public void removeItem (String item) {
		mClips.remove(mClips.indexOf(item));
	}
	
	public void addItem (String item) {
		if (!mClips.contains(item)) {
			mClips.add(item);
			}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View root = convertView;
		ViewHolder mHolder;
		
		final int finalPosition = position;
		
		if (root == null) {
			root = mInflater.inflate(R.layout.clip_list_item, null);
			mHolder = new ViewHolder();
			mHolder.content = (TextView) root.findViewById(R.id.clip_list_text);
			mHolder.button = (ImageView) root.findViewById(R.id.clip_list_attach);
			root.setTag(mHolder);
		}
		
		else {
			mHolder = (ViewHolder) root.getTag();
		}
		
		mHolder.content.setText(mClips.get(position));
		Linkify.addLinks(mHolder.content, Linkify.WEB_URLS);
		
		mHolder.button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				String value = mClips.get(finalPosition);
				ClipHelper.addItemToClipboard(mContext, "label", value, isOldApi);
				Toast.makeText(mContext, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
				mClips.remove(value);
				mClips.add(value);	
				notifyDataSetChanged();
			}
		});
		
		return root;
	}
	
	private class ViewHolder {
		TextView content;
		ImageView button;
	}
	
	
	
}
