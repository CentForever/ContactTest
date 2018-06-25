package com.forever.contacttest;

import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.AlphabetIndexer;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
     * 弹出式分组的布局
     */
    private RelativeLayout sectionToastLayout;
    /**
     * 定义字母表的排序规则
     */
    private String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
	private int lastFirstVisibleItem = -1;


    /**
     * 右侧可滑动字母表
     */
    private Button alphabetButton;

    /**
     * 分组上显示的字母
     */
    private TextView title;

    /**
     * 弹出式分组上的文字
     */
    private TextView sectionToastText;
    private SideBar sideBar;
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
        sectionToastLayout = (RelativeLayout) findViewById(R.id.section_toast_layout);
        title = (TextView) findViewById(R.id.title);
        lstContact = (ListView) findViewById(R.id.contacts_list_view);
        sideBar = (SideBar) findViewById(R.id.sideBar);
        sideBar.setTextView(sectionToastText);

        getSupportLoaderManager().initLoader(1, null, this);

        sectionToastText = (TextView) findViewById(R.id.section_toast_text);
        alphabetButton = (Button) findViewById(R.id.alphabetButton);
        //设置右侧SideBar触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s,int sectionPosition) {
                //该字母首次出现的位置
                int position = indexer.getPositionForSection(sectionPosition);
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

        setAlpabetListener();
    }

    /**
     * 设置字母表上的触摸事件，根据当前触摸的位置结合字母表的高度，计算出当前触摸在哪个字母上。
     * 当手指按在字母表上时，展示弹出式分组。手指离开字母表时，将弹出式分组隐藏。
     */
    private void setAlpabetListener() {
        alphabetButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float alphabetHeight = alphabetButton.getHeight();
                float y = event.getY();
                int sectionPosition = (int) ((y / alphabetHeight) / (1f / 27f));
                if (sectionPosition < 0) {
                    sectionPosition = 0;
                } else if (sectionPosition > 26) {
                    sectionPosition = 26;
                }
                String sectionLetter = String.valueOf(alphabet.charAt(sectionPosition));
                int position = indexer.getPositionForSection(sectionPosition);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        alphabetButton.setBackgroundResource(R.drawable.a_z_click);
                        sectionToastLayout.setVisibility(View.VISIBLE);
                        sectionToastText.setText(sectionLetter);
                        lstContact.setSelection(position);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        sectionToastText.setText(sectionLetter);
                        lstContact.setSelection(position);
                        break;
                    default:
                        alphabetButton.setBackgroundResource(R.drawable.a_z);
                        sectionToastLayout.setVisibility(View.GONE);
                }
                return true;
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
