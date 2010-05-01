/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.anycut;

import android.widget.Toast;
import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.provider.Contacts.People;
import android.provider.Contacts.ContactMethods;
import android.provider.Contacts;

import android.net.Uri;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.ListView;
import android.util.Log;

/**
 * A list view example where the data comes from a cursor.
 */
public class GtalkPickerActivity extends ListActivity {
    private static String[] PROJECTION = new String[] {
        Contacts.ContactMethods.CONTENT_IM_ITEM_TYPE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        // mPhone = (TextView) findViewById(R.id.phone);
        //getListView().setOnItemSelectedListener(this);
        // From : http://www.higherpass.com/Android/Tutorials/Working-With-Android-Contacts/2/
        // /home/sebastien/android-sdk-linux_x86-1.6_r1/platforms/android-1.6/samples/ApiDemos/src/com/example/android/apis/view/List7.java
//        String imWhere = Contacts.ContactMethods.KIND + " = ?"; 
//        String[] imWhereParams = new String[]{ Contacts.ContactMethods.CONTENT_IM_ITEM_TYPE}; 
        String imWhere = ContactMethods.KIND + " = ?"; 
        String[] imWhereParams = new String[]{ ContactMethods.CONTENT_IM_ITEM_TYPE}; 

        // Get a cursor with all people
        Cursor c = getContentResolver().query(ContactMethods.CONTENT_URI,
                null, null, null, null);
                // null, imWhere, imWhereParams, null); 

        Log.v("Gtalk Picker", "Im Item : "+ ContactMethods.CONTENT_IM_ITEM_TYPE);
        for (int i = 0; i < (c.getColumnNames()).length; i++)
            Log.v("Gtalk Picker", "column Name "+ i + " : "+ c.getColumnNames()[i]);
        startManagingCursor(c);
        int kindIndex = c.getColumnIndexOrThrow(ContactMethods.KIND);
        int dataIndex = c.getColumnIndexOrThrow(ContactMethods.DATA);
        int auxDataIndex = c.getColumnIndexOrThrow(ContactMethods.AUX_DATA);

        mPhoneColumnIndex = c.getColumnIndex(ContactMethods.DATA);
        ListAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                c,
                new String[] {People.DISPLAY_NAME, ContactMethods.DATA},
                new int[] {android.R.id.text1, android.R.id.text2});
        setListAdapter(adapter);
    }


    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        String item = "michoux@gmail.com"; //(String) getListAdapter().getItem(position);
        //        Cursor c = ((Cursor) listAdapter.getItem(position));
        //        long phoneNumber = c.getLong(c.getColumnIndex(People.NUMBER));
//        intent.setComponent(new ComponentName(info.activityInfo.applicationInfo.packageName,
//                info.activityInfo.name));
        Intent result = new Intent();
        result.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_SENDTO, Uri.parse("imto://gtalk/" + item)));

        // Set the name of the activity
        result.putExtra(Intent.EXTRA_SHORTCUT_NAME, item);

        // Build the icon info for the activity
		/*
        Drawable drawable = info.loadIcon(mPackageManager);
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            result.putExtra(Intent.EXTRA_SHORTCUT_ICON, bd.getBitmap());
        }
		*/
        // Set the result
        setResult(RESULT_OK, result);
        finish();
    }

    private int mPhoneColumnIndex;
    // private TextView mPhone;
}
