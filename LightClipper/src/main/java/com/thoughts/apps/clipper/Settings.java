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
package com.thoughts.apps.clipper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.thoughts.apps.clipper.database.DbManager;

/**
 * We are using a Preference Activity as a PreferenceFragment required high API levels. 
 * Note we do not call {@link #setTheme(int)} here since by default most settings in android are black themed. 
 * @author SkullKandy
 *
 */
public class Settings extends PreferenceActivity {

	public static final String KEY_PREF_CUR_THEME = "pref_key_current_theme";
	public static final String KEY_PREF_ENABLE_LINKS = "pref_key_enable_links_for";
	public static final String KEY_PREF_CLEAR_CLIPS = "pref_key_clear_clips";
	public static final String KEY_PREF_SHOW_SOURCE = "pref_key_show_source";
	public static final String KEY_PREF_BUILD_VERSION = "pref_key_build_version";
	
	private static final String SOURCE_URL = "https://github.com/daniel-c05/LightClipper";
	private static final String VERSION_UNAVAILABLE = "N/A";
	
	private static boolean shouldRestartHome = false;

	private Preference deleteClipsPref;
	private Preference showSourcePref;
	private Preference buildVersionPref;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);   
		deleteClipsPref = findPreference(KEY_PREF_CLEAR_CLIPS);
		showSourcePref = findPreference(KEY_PREF_SHOW_SOURCE);
		buildVersionPref = findPreference(KEY_PREF_BUILD_VERSION);
		findPreference(KEY_PREF_CUR_THEME).setOnPreferenceChangeListener(prefChangeListener);	
		
		updateBuildVersionInfo();
	}

	private void updateBuildVersionInfo() {
		PackageManager pm = getPackageManager();
		String packageName = getPackageName();
		String versionName;
		try {
			PackageInfo info = pm.getPackageInfo(packageName, 0);
			versionName = info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			versionName = VERSION_UNAVAILABLE;
		}
		
		buildVersionPref.setSummary(packageName + " v" + versionName);
	}

	private static Preference.OnPreferenceChangeListener prefChangeListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {				
			String prefName = preference.getTitle().toString();
			Log.v(Home.TAG, "Preference " + prefName + " changed");
			if (preference instanceof CheckBoxPreference || preference instanceof MultiSelectListPreference) {		
				shouldRestartHome = true;
			}
			return true;
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
		if (preference == deleteClipsPref) {
			//Muestra un dialogo para evitar que el usuario borre la base de datos por error.
			showDeleteHistoryDialog();
		}
		else if (preference == showSourcePref) {
			showSourceCode();
		}
		return super.onPreferenceTreeClick(preferenceScreen, preference);	
	}

	private void showSourceCode() {
		Intent showSourceIntent = new Intent();
		showSourceIntent.setAction(Intent.ACTION_VIEW);
		showSourceIntent.addCategory(Intent.CATEGORY_DEFAULT);
		showSourceIntent.setData(Uri.parse(SOURCE_URL));
		startActivity(showSourceIntent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && shouldRestartHome) {
			Log.v(Home.TAG, "Restarting home");
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void showDeleteHistoryDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);    		
		builder.setMessage(R.string.pref_warning_clear_clips)
		.setPositiveButton(android.R.string.ok, new OnClickListener() {				
			@Override
			public void onClick(DialogInterface dialog, int which) {	
				DbManager.deleteClips(Settings.this);
				shouldRestartHome = true;
                Toast.makeText(Settings.this, getString(R.string.clips_deleted),Toast.LENGTH_LONG).show();
                dialog.dismiss();
			}
		})
		.setCancelable(true).setNegativeButton(android.R.string.cancel, new OnClickListener() {				
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
