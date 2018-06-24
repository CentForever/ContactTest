package com.forever.contacttest;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AlphabetIndexer;
import android.widget.ListView;
import android.widget.TextView;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AlphabetIndexer;
import android.widget.LinearLayout;
import android.view.View;
import android.widget.LinearLayout;
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    String[] mProjection;
    ListView lstContact;
    CustomContactAdapter adapter;
    AlphabetIndexer indexer;
    /**
     * 分组的布局
     */
	private LinearLayout titleLayout;
    /**
     * 定义字母表的排序规则
     */
    private String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ#";
    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
	private int lastFirstVisibleItem = -1;

	/**
     * 分组上显示的字母
     */
	private TextView title;
    private SideBar sideBar;
    private TextView dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProjection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.SORT_KEY_PRIMARY
        };
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) findViewById(R.id.title);
        lstContact = (ListView) findViewById(R.id.lstContacts);
        sideBar = (SideBar) findViewById(R.id.sideBar);
        dialog = (TextView) findViewById(R.id.dialog);
        sideBar.setTextView(dialog);
        lstContact.setFastScrollEnabled(true);
        getSupportLoaderManager().initLoader(1, null, this);
        //设置右侧SideBar触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                android.util.Log.e("mgg","setOnTouchingLetterChangedListener:"+position+" s.charAt(0)");
                if (position != -1) {
                    lstContact.setSelection(position);
                }
            }
        });
        lstContact.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
					int totalItemCount) {
			    if(indexer==null){
			        return ;
                }
				int section = indexer.getSectionForPosition(firstVisibleItem);
				int nextSecPosition = indexer.getPositionForSection(section + 1);
				if (firstVisibleItem != lastFirstVisibleItem) {
					MarginLayoutParams params = (MarginLayoutParams) titleLayout.getLayoutParams();
					params.topMargin = 0;
					titleLayout.setLayoutParams(params);
					title.setText(String.valueOf(alphabet.charAt(section)));
				}
				if (nextSecPosition == firstVisibleItem + 1) {
					View childView = view.getChildAt(0);
					if (childView != null) {
						int titleHeight = titleLayout.getHeight();
						int bottom = childView.getBottom();
						MarginLayoutParams params = (MarginLayoutParams) titleLayout
								.getLayoutParams();
						if (bottom < titleHeight) {
							float pushedDistance = bottom - titleHeight;
							params.topMargin = (int) pushedDistance;
							titleLayout.setLayoutParams(params);
						} else {
							if (params.topMargin != 0) {
								params.topMargin = 0;
								titleLayout.setLayoutParams(params);
							}
						}
					}
				}
				lastFirstVisibleItem = firstVisibleItem;
			}
		});
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = new CursorLoader(this,  ContactsContract.Contacts.CONTENT_URI,
                mProjection,
                null,
                null,
                ContactsContract.Contacts.SORT_KEY_PRIMARY);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if(data!=null && data.getCount()>0) {
            data.moveToFirst();
            adapter = new CustomContactAdapter(this, data);
            indexer = new AlphabetIndexer(data, 2, alphabet);
            adapter.setAlphaIndexer(indexer);
            lstContact.setAdapter(adapter);
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }
}
