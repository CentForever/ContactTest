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

                   ViewHolder viewHolder= new ViewHolder();

           View view=mInflater.inflate(R.layout.contact_list_element, parent, false);

           viewHolder.tvCOntactName = (TextView) view
                .findViewById(R.id.contact_name);
           viewHolder.tvSeparator= (TextView) view
                .findViewById(R.id.separator);
           view.setTag(viewHolder);

            return view;

    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
          ViewHolder viewHolder=(ViewHolder) view.getTag();
        viewHolder.tvCOntactName.setText(cursor.getString(cursor
                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

    }

    class ViewHolder {
        TextView tvCOntactName,tvSeparator;
    }
}
