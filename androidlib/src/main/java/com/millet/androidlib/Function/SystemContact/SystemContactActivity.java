package com.millet.androidlib.Function.SystemContact;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AlphabetIndexer;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.millet.androidlib.Base.BaseActivity;
import com.millet.androidlib.R;


import java.util.ArrayList;
import java.util.List;

public class SystemContactActivity extends BaseActivity {

    //分组的布局
    private LinearLayout mTitleLayout;

    //分组上显示的字母
    private TextView mTitle;

    //联系人ListView
    private ListView mContactsListView;

    //联系人列表适配器
    private ContactAdapter mAdapter;

    // 用于进行字母表分组
    private AlphabetIndexer mIndexer;

    //存储所有手机中的联系人
    private List<ContactEntity> mContacts = new ArrayList<>();

    //定义字母表的排序规则
    private String mAlphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    //上次第一个可见元素，用于滚动时记录标识。
    private int mLastFirstVisibleItem = -1;

    //按字母显示联系人UI
    private RelativeLayout mSectionToastLayout;
    private TextView mSectionToastText;
    private Button mAlphabetButton;

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_system_contact);
        mTitleLayout = (LinearLayout) findViewById(R.id.title_layout);
        mTitle = (TextView) findViewById(R.id.title);
        mContactsListView = (ListView) findViewById(R.id.contacts_list_view);
        mAdapter = new ContactAdapter(this, R.layout.item_contact_adapter, mContacts);
        mSectionToastLayout = (RelativeLayout) findViewById(R.id.section_toast_layout);
        mAlphabetButton = (Button) findViewById(R.id.alphabetButton);
        mSectionToastText = (TextView) findViewById(R.id.section_toast_text);
    }

    @Override
    protected void loadData(Bundle savedInstanceState) {
        //从ContentProvider中获取联系人数据
        Uri _uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor _cursor = getContentResolver().query(_uri, new String[]{"display_name", "sort_key"}, null, null, "sort_key");
        if (_cursor.moveToFirst()) {
            do {
                String _name = _cursor.getString(0);
                String _sortKey = getSortKey(_cursor.getString(1));
                ContactEntity _contactEntity = new ContactEntity();
                _contactEntity.setName(_name);
                _contactEntity.setSortKey(_sortKey);
                mContacts.add(_contactEntity);
            } while (_cursor.moveToNext());
        }
        startManagingCursor(_cursor);
        mIndexer = new AlphabetIndexer(_cursor, 1, mAlphabet);
        mAdapter.setIndexer(mIndexer);
        if (mContacts.size() > 0) {
            setupContactsListView();
            setAlphabetListener();
        }
    }

    /**
     * 为联系人ListView设置监听事件，根据当前的滑动状态来改变分组的显示位置，从而实现挤压动画的效果。
     */
    private void setupContactsListView() {
        mContactsListView.setAdapter(mAdapter);
        mContactsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int _section = mIndexer.getSectionForPosition(firstVisibleItem);
                int _nextSecPosition = mIndexer.getPositionForSection(_section + 1);
                if (firstVisibleItem != mLastFirstVisibleItem) {
                    ViewGroup.MarginLayoutParams _params = (ViewGroup.MarginLayoutParams) mTitleLayout.getLayoutParams();
                    _params.topMargin = 0;
                    mTitleLayout.setLayoutParams(_params);
                    mTitle.setText(String.valueOf(mAlphabet.charAt(_section)));
                }
                if (_nextSecPosition == firstVisibleItem + 1) {
                    View _childView = view.getChildAt(0);
                    if (_childView != null) {
                        int _titleHeight = mTitleLayout.getHeight();
                        int _bottom = _childView.getBottom();
                        ViewGroup.MarginLayoutParams _params = (ViewGroup.MarginLayoutParams) mTitleLayout.getLayoutParams();
                        if (_bottom < _titleHeight) {
                            float _pushedDistance = _bottom - _titleHeight;
                            _params.topMargin = (int) _pushedDistance;
                            mTitleLayout.setLayoutParams(_params);
                        } else {
                            if (_params.topMargin != 0) {
                                _params.topMargin = 0;
                                mTitleLayout.setLayoutParams(_params);
                            }
                        }
                    }
                }
                mLastFirstVisibleItem = firstVisibleItem;
            }
        });
    }

    /**
     * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
     *
     * @param _sortKeyString 数据库中读取出的sort key
     * @return 英文字母或者#
     */
    private String getSortKey(String _sortKeyString) {
        String key = _sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }

    private void setAlphabetListener() {
        mAlphabetButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float alphabetHeight = mAlphabetButton.getHeight();
                float y = event.getY();
                int sectionPosition = (int) ((y / alphabetHeight) / (1f / 27f));
                if (sectionPosition < 0) {
                    sectionPosition = 0;
                } else if (sectionPosition > 26) {
                    sectionPosition = 26;
                }
                String sectionLetter = String.valueOf(mAlphabet.charAt(sectionPosition));
                int position = mIndexer.getPositionForSection(sectionPosition);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mAlphabetButton.setBackgroundResource(R.mipmap.a_z_click);
                        mSectionToastLayout.setVisibility(View.VISIBLE);
                        mSectionToastText.setText(sectionLetter);
                        mContactsListView.setSelection(position);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mSectionToastText.setText(sectionLetter);
                        mContactsListView.setSelection(position);
                        break;
                    default:
                        mAlphabetButton.setBackgroundResource(R.mipmap.a_z);
                        mSectionToastLayout.setVisibility(View.GONE);
                }
                return true;
            }
        });
    }

}
