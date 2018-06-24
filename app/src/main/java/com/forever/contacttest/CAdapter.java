package com.forever.contacttest;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * http://www.worldbestlearningcenter.com/tips/Android-CursorLoader.htm
 */
public class CAdapter extends CursorAdapter {
    private final LayoutInflater mInflater;
    public CAdapter(Context context, Cursor cursor) {
        super(context, cursor, false);
        mInflater = LayoutInflater.from(context);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return mInflater.inflate(R.layout.contact_list_element, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvCOntactName = (TextView) view
                .findViewById(R.id.contact_name);
        TextView tvSeparator= (TextView) view
                .findViewById(R.id.separator);
        tvCOntactName.setText(cursor.getString(cursor
                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
    }
}
