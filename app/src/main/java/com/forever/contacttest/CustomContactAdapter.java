package com.forever.contacttest;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

/**
 * https://blog.csdn.net/guolin_blog/article/details/9033553
 * https://blog.csdn.net/guolin_blog/article/details/9050671
 */
public class CustomContactAdapter extends BaseAdapter{
    Cursor cursor;
    Context mContext;
    LayoutInflater inflater;
    SectionIndexer alphaIndexer;
    public CustomContactAdapter(Context context, Cursor cursor) {
        mContext = context;
        this.cursor = cursor;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setAlphaIndexer(SectionIndexer alphaIndexer) {
        this.alphaIndexer = alphaIndexer;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder;
        cursor.moveToPosition(position);
        if (view == null) {
            view = inflater.inflate(R.layout.contact_list_element, parent,
                    false);
            holder = new Holder();
            holder.tvCOntactName = (TextView) view
                    .findViewById(R.id.contact_name);
            holder.tvSeparator= (TextView) view
                    .findViewById(R.id.separator);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }

        String sortKey = cursor.getString(cursor
                .getColumnIndex(ContactsContract.Contacts.SORT_KEY_PRIMARY));
        holder.tvCOntactName.setText(cursor.getString(cursor
                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))+":"+sortKey);
        int section = alphaIndexer.getSectionForPosition(position);
        if (position == alphaIndexer.getPositionForSection(section)) {
            holder.tvSeparator.setText(getSortKey(sortKey));
            holder.tvSeparator.setVisibility(View.VISIBLE);
        } else {
            holder.tvSeparator.setVisibility(View.GONE);
        }
        return view;
    }

    /**
     * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
     *
     * @param str
     *            数据库中读取出的sort key
     * @return 英文字母或者#
     */
    private String getSortKey(String str) {
       if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式匹配
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase(); // 将小写字母转换为大写
        } else {
            return "#";
        }
    }

    class Holder {
        TextView tvCOntactName,tvSeparator;
    }

    public int getPositionForSection(int section) {

        return alphaIndexer.getPositionForSection(section);
    }

    public int getSectionForPosition(int position) {
        return alphaIndexer.getSectionForPosition(position);
    }

    public Object[] getSections() {
        return alphaIndexer.getSections();
    }
}
