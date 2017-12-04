package com.millet.androidlib.Function.ChooseMultiplePhoto;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.millet.androidlib.Base.BaseActivity;
import com.millet.androidlib.R;
import com.millet.androidlib.Utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/17 0017.
 */

public class AlbumDetailActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String TAG = AlbumDetailActivity.class.getSimpleName();
    public static final int REQUEST_CODE_CHOOSE_MULTIPLE_PHOTO = 20001;

    //key
    public static final String ALBUM_ID = "ALBUM_ID";//相册ID
    public static final String MAX_SIZE_CHOOSE = "MAX_SIZE_CHOOSE";//最大选择数量
    public static final String SELECTED_PIC = "SELECTED_PIC";//已经选择的图片ID

    //ui
    private GridView mGridView;
    private TextView mTextCancel;
    private TextView mTextSure;
    private AlbumDetailAdapter mAdapter;

    //data
    private String mAlbumId;
    private int mMaxSize;
    private ArrayList<String> mSelectedList = new ArrayList<>();//已经选择的
    private ArrayList<String> mAlbumDetailList = new ArrayList<>();//全部的
    private SparseBooleanArray mSparseBooleanArray = new SparseBooleanArray();
    private int mSelectedSize;


    public static void launch(Activity _activity, String _albumId, int _maxSize, ArrayList<String> _chooseList) {
        Intent _intent = new Intent(_activity, AlbumDetailActivity.class);
        _intent.putExtra(ALBUM_ID, _albumId);
        _intent.putExtra(MAX_SIZE_CHOOSE, _maxSize);
        _intent.putStringArrayListExtra(SELECTED_PIC, _chooseList);
        _activity.startActivityForResult(_intent, REQUEST_CODE_CHOOSE_MULTIPLE_PHOTO);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent _intent = getIntent();
        if (null == _intent) return;
        if (_intent.hasExtra(ALBUM_ID))
            mAlbumId = _intent.getStringExtra(ALBUM_ID);
        if (_intent.hasExtra(MAX_SIZE_CHOOSE))
            mMaxSize = _intent.getIntExtra(MAX_SIZE_CHOOSE, -1);
        if (_intent.hasExtra(SELECTED_PIC)) {
            mSelectedList = _intent.getStringArrayListExtra(SELECTED_PIC);
            mSelectedSize = mSelectedList.size();
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_album);
        mGridView = (GridView) findViewById(R.id.activity_album_grid_view);
        mGridView.setOnItemClickListener(this);
        mTextCancel = (TextView) findViewById(R.id.activity_album_right_cancel);
        mTextCancel.setOnClickListener(this);
        mTextSure = (TextView) findViewById(R.id.activity_album_right_sure);
        mTextSure.setOnClickListener(this);
        mAdapter = new AlbumDetailAdapter(this);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    protected void loadData(Bundle savedInstanceState) {
        LoadAlbumDetailAsyncTask _loadAlbumDetail = new LoadAlbumDetailAsyncTask();
        _loadAlbumDetail.execute(mAlbumId);
    }

    @Override
    public void onClick(View v) {
        int _id = v.getId();
        if (R.id.activity_album_right_cancel == _id) {
            confirmChoose();
        } else if (R.id.activity_album_right_sure == _id) {
            confirmChoose();
        }
    }

    @Override
    public void onBackPressed() {
        confirmChoose();
    }

    /**
     * 确认选择完成
     */
    public void confirmChoose() {
        Intent _intent = getIntent();
        _intent.putExtra(SELECTED_PIC, mSelectedList);
        setResult(Activity.RESULT_OK, _intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        boolean _state = mSparseBooleanArray.get(position);
        if (mSelectedSize >= mMaxSize && !_state) {
            Toast.makeText(this, "不能再选了！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (_state) {
            mSparseBooleanArray.delete(position);
            mSelectedList.remove(mAlbumDetailList.get(position));
            mSelectedSize--;
        } else {
            mSparseBooleanArray.put(position, !_state);
            mSelectedList.add(mAlbumDetailList.get(position));
            mSelectedSize++;
        }
        mAdapter.setSparseBooleanArray(mSparseBooleanArray);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 读取相册详情内容
     */
    public class LoadAlbumDetailAsyncTask extends AsyncTask<String, Integer, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            String _albumId = null;
            List<String> _listString = null;
            if (null != params) {
                _albumId = params[0];
            }
            _listString = loadAlbumDetail(_albumId);
            return _listString;
        }

        @Override
        protected void onPostExecute(List<String> strings) {
            if (null == strings) return;
            mAlbumDetailList.clear();
            mAlbumDetailList.addAll(strings);
            mAdapter.setSparseBooleanArray(mSparseBooleanArray);
            mAdapter.setData(mAlbumDetailList);
        }
    }

    /**
     * 根据相册ID，拿到里面的图片
     *
     * @param _albumId
     * @return
     */
    public List<String> loadAlbumDetail(String _albumId) {
        try {
            String _sortRule = MediaStore.Images.Media.DATE_TAKEN + " desc";
            List<String> _stringList = null;
            Cursor _cursor = null;
            if (null == _albumId) {
                //查询所有图片
                _cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA}, null, null, _sortRule);
            } else {
                //查询某个相册中图片
                _cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media.DATA}, MediaStore.Images.Media.BUCKET_ID + "=?", new String[]{_albumId}, _sortRule);
            }
            if (null != _cursor) {
                _stringList = new ArrayList<>();
                for (int i = 0; i < _cursor.getCount(); i++) {
                    _cursor.moveToPosition(i);
                    int _dataColumnIndex = _cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    _stringList.add(_cursor.getString(_dataColumnIndex));
                }
            }
            _cursor.close();
            if (null != mSelectedList && !mSelectedList.isEmpty()) {
                showSelectedStatus(_stringList);
            }
            return _stringList;
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
            return null;
        }
    }

    /**
     * 记录重复的
     *
     * @param _stringList
     */
    private void showSelectedStatus(final List<String> _stringList) {
        try {
            for (int i = 0; i < _stringList.size(); i++) {
                for (int j = 0; j < mSelectedList.size(); j++) {
                    if (_stringList.get(i).equals(mSelectedList.get(j))) {
                        mSparseBooleanArray.put(i, true);
                    }
                }
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

}

