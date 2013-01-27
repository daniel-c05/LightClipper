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
package com.deadpixels.light.clipper;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

/**
 * We are using a Preference Activity as a PreferenceFragment required high API levels. 
 * @author SkullKandy
 *
 */
public class Settings extends PreferenceActivity {
	
	public static final String KEY_PREF_CUR_THEME = "pref_key_current_theme";
	public static final String KEY_PREF_MAKE_LINKS_CLICKABLE = "pref_key_make_links_clickable";

		@SuppressWarnings("deprecation")
		@Override
		    protected void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        
		        if (Home.darkThemeEnabled) {
		        	getListView().setBackgroundColor(getResources().getColor(R.color.black));
					setTheme(R.style.DarkTheme);
				}
		        else {
		        	setTheme(R.style.LightTheme);
		        }
		        
		        addPreferencesFromResource(R.xml.preferences);   
		        
		        findPreference(KEY_PREF_CUR_THEME).setOnPreferenceChangeListener(prefChangeListener);
		        findPreference(KEY_PREF_MAKE_LINKS_CLICKABLE).setOnPreferenceChangeListener(prefChangeListener);		       		        
		    }
		
		private static Preference.OnPreferenceChangeListener prefChangeListener = new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object value) {
				//final Context context = preference.getContext();
				String stringValue = value.toString();
				String prefName = preference.getTitle().toString();
				Log.v(Home.TAG, "Preference changed");
				if (preference instanceof CheckBoxPreference) {					
					Log.v(Home.TAG, prefName + "'s new value is: " + stringValue);												
				}
				return true;
			}
		};
		
		@Override
		protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
		    theme.applyStyle(resid, true);
		}
}
