package com.thoughts.apps.clipper;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Toast;

import com.thoughts.apps.clipper.database.DbManager;
import com.thoughts.apps.clipper.database.DbOpenHelper;
import com.thoughts.apps.clipper.utils.ClipHelper;

public class NoUiActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            String action = getIntent().getAction();
            String data = "";
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                data = getIntent().getCharSequenceExtra(Intent.EXTRA_TEXT).toString();
                ClipHelper.addItemToClipboard(this, action, data);
                DbManager.saveClip(this, data);
                Toast.makeText(this, "Data copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        }
        this.finish();
    }
}
