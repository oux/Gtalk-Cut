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
import android.provider.Contacts.Phones;
import android.provider.Contacts.Photos;
import android.provider.Contacts.ContactMethods;
import android.provider.Contacts;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.content.ContentUris;
import android.content.res.Resources;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
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
        // From : http://www.higherpass.com/Android/Tutorials/Working-With-Android-Contacts/2/
//        String imWhere = Contacts.ContactMethods.KIND + " = ?"; 
//        String[] imWhereParams = new String[]{ Contacts.ContactMethods.CONTENT_IM_ITEM_TYPE}; 
        String imWhere = ContactMethods.KIND + " = ?"; 
        String[] imWhereParams = new String[]{ ContactMethods.CONTENT_IM_ITEM_TYPE}; 

        // Get a cursor with all people
        Cursor c = getContentResolver().query(ContactMethods.CONTENT_URI,
                null, null, null, People.DISPLAY_NAME);
                // null, imWhere, imWhereParams, null); 

        Log.v("Gtalk Picker", "Im Item : "+ ContactMethods.CONTENT_IM_ITEM_TYPE);
        startManagingCursor(c);
        ListAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                c,
                new String[] {People.DISPLAY_NAME, ContactMethods.DATA},
                new int[] {android.R.id.text1, android.R.id.text2});
        setListAdapter(adapter);
    }

    /**
     * Generates a phone number shortcut icon. Adds an overlay describing the
     * type of the phone
     * number, and if there is a photo also adds the call action icon.
     *
     * @param personUri The person the phone number belongs to
     * @return The bitmap for the icon
     */
    private Bitmap generateGtalkIcon(Uri personUri) {
        final Resources r = getResources();
        boolean drawPhoneOverlay = true;
        Log.v("Gtalk Picker", "personUri: "+ personUri);

        Bitmap photo = People.loadContactPhoto(this, personUri, 0, null);
        if (photo == null) {
            return null;
        }

        // Setup the drawing classes
        int iconSize = (int) r.getDimension(android.R.dimen.app_icon_size);
        Bitmap icon = Bitmap.createBitmap(iconSize, iconSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(icon);

        // Copy in the photo
        Paint photoPaint = new Paint();
        photoPaint.setDither(true);
        photoPaint.setFilterBitmap(true);
        Rect src = new Rect(0,0, photo.getWidth(),photo.getHeight());
        Rect dst = new Rect(0,0, iconSize,iconSize);
        canvas.drawBitmap(photo, src, dst, photoPaint);

        // Create an overlay for the phone number type
        String overlay = "G";
        if (overlay != null) {
            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
            textPaint.setTextSize(20.0f);
            textPaint.setTypeface(Typeface.DEFAULT_BOLD);
            textPaint.setColor(r.getColor(R.color.textColorIconOverlay));
            textPaint.setShadowLayer(3f, 1, 1, r.getColor(R.color.textColorIconOverlayShadow));
            canvas.drawText(overlay, 2, 16, textPaint);
        }

        return icon;
    }


    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        Cursor c = ((Cursor) getListAdapter().getItem(position));
        String gtalk = c.getString(c.getColumnIndex(ContactMethods.DATA));
        String name = c.getString(c.getColumnIndex(People.DISPLAY_NAME));
        long personId = c.getLong(c.getColumnIndex(Phones.PERSON_ID));
        Intent result = new Intent();
        result.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_SENDTO, Uri.parse("imto://gtalk/" + gtalk)));

        // Set the name of the activity
        result.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Talk to " + name);

        // Build the icon info for the gtalk contact
        Uri personUri = ContentUris.withAppendedId(People.CONTENT_URI, personId);
        result.putExtra(Intent.EXTRA_SHORTCUT_ICON,
                generateGtalkIcon(personUri));

        // Set the result
        setResult(RESULT_OK, result);
        finish();
    }
}
