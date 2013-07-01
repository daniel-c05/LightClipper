/*******************************************************************************
 * Copyright 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.thoughts.apps.clipper.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thoughts.apps.clipper.R;
import com.thoughts.apps.clipper.database.DbOpenHelper;
import com.thoughts.apps.clipper.ui.ClipListItem;

public class ClipsArrayAdapter extends SimpleCursorAdapter {

    private LayoutInflater mInflater;

    public ClipsArrayAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.clip_list_item, null);
        ClipListItem item = (ClipListItem) view;
        item.bind(mCursor.getString(mCursor.getColumnIndex(DbOpenHelper.CLIP_TEXT)));
        return view;
    }

    @Override
    public void bindView(View view, Context arg1, Cursor arg2) {
        ClipListItem item;
        if (view == null) {
            view = mInflater.inflate(R.layout.clip_list_item, null);
        }
        item = (ClipListItem) view;
        item.bind(mCursor.getString(mCursor.getColumnIndex(DbOpenHelper.CLIP_TEXT)));
    }

    public String getString (int pos) {
        if (mCursor.getCount() < pos)
            return "";

        mCursor.moveToPosition(pos);
        return mCursor.getString(mCursor.getColumnIndex(DbOpenHelper.CLIP_TEXT));
    }
}
