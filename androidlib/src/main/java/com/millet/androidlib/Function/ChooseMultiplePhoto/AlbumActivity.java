package com.millet.androidlib.Function.ChooseMultiplePhoto;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.millet.androidlib.Base.BaseActivity;
import com.millet.androidlib.Base.IBaseAdapter;
import com.millet.androidlib.Function.TakePhoto.AlbumEntity;
import com.millet.androidlib.R;
import com.millet.androidlib.Utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/5/16 0016.
 */

public class AlbumActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    public static final String CHOOSE_MAX_SIZE = "CHOOSE_MAX_SIZE";
    public static final int REQUEST_CODE_CHOOSE_PHOTO = 1001;

    public static void launch(Activity _activity, int _chooseSize) {
        Intent _intent = new Intent(_activity, AlbumActivity.class);
        _intent.putExtra(CHOOSE_MAX_SIZE, _chooseSize);
        _activity.startActivityForResult(_intent, REQUEST_CODE_CHOOSE_PHOTO);
    }

    //ui
    private GridView mGridView;
    private IBaseAdapter mAdapter;
    private TextView mTextCancel;
    private TextView mTextSure;
    //data
    private int mMaxSize;
    private ArrayList<String> mSelectItems = new ArrayList<>();

    @Override
    protected void initData(Bundle savedInstanceState) {
        Intent _intent = getIntent();
        if (null == _intent) return;
        if (_intent.hasExtra(CHOOSE_MAX_SIZE))
            mMaxSize = _intent.getIntExtra(CHOOSE_MAX_SIZE, -1);
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
        mAdapter = new AlbumAdapter(this);
        mGridView.setAdapter(mAdapter);
    }

    @Override
    protected void loadData(Bundle savedInstanceState) {
        LoadAlbumAsyncTask _loadAlbum = new LoadAlbumAsyncTask();
        _loadAlbum.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AlbumEntity _albumEntity = (AlbumEntity) mAdapter.getItem(position);
        if (null == _albumEntity) return;
        String _albumId = _albumEntity.getAlbumId();
        AlbumDetailActivity.launch(this, _albumId, mMaxSize, mSelectItems);
    }

    @Override
    public void onClick(View v) {
        int _id = v.getId();
        if (R.id.activity_album_right_cancel == _id) {
            finish();
        } else if (R.id.activity_album_right_sure == _id) {
            confirmChoose();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AlbumDetailActivity.REQUEST_CODE_CHOOSE_MULTIPLE_PHOTO && resultCode == RESULT_OK) {
            ArrayList<String> _selectedList = data.getStringArrayListExtra(AlbumDetailActivity.SELECTED_PIC);
            chooseResult(_selectedList);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 选择图片后的结果
     *
     * @param _list
     */
    public void chooseResult(ArrayList<String> _list) {
        try {
            if (null != _list) {
                mSelectItems.clear();
                mSelectItems.addAll(_list);
            }
        } catch (Exception _e) {
            LogUtils.catchInfo(_e.toString());
        }
    }

    /**
     * 确认选择完成
     */
    public void confirmChoose() {
        Intent _intent = getIntent();
        _intent.putExtra(AlbumDetailActivity.SELECTED_PIC, mSelectItems);
        setResult(RESULT_OK, _intent);
        finish();
    }

    public class LoadAlbumAsyncTask extends AsyncTask<Void, Integer, List<AlbumEntity>> {

        @Override
        protected List<AlbumEntity> doInBackground(Void... params) {
            List<AlbumEntity> _albumEntity = null;
            Uri _uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] _albumFields = new String[]{MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.IS_PRIVATE};
            String _albumSql = MediaStore.Images.Media.BUCKET_ID + "!=?) group by (" + MediaStore.Images.Media.BUCKET_ID;
            Cursor _albumCursor = getContentResolver().query(_uri, _albumFields, _albumSql, new String[]{""}, null);
            if (null == _albumCursor) return null;
            _albumEntity = new ArrayList<>();
            for (int i = 0; i < _albumCursor.getCount(); i++) {
                _albumCursor.moveToPosition(i);
                String _coverPath = _albumCursor.getString(_albumCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                boolean _isHide = isHiddenDir(_coverPath);
                if (_coverPath != null && (_coverPath.startsWith("/data") || _isHide)) {
                    // 系统路径 pass 隐藏目录pass
                    continue;
                }
                AlbumEntity _entity = new AlbumEntity();
                _entity.setAlbumId(_albumCursor.getString(_albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID)));
                _entity.setName(_albumCursor.getString(_albumCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)));
                _entity.setCoverPath(_coverPath);
                _entity.setPicCount(queryPicCountByAlbumId(_entity.getAlbumId()));
                _albumEntity.add(_entity);
            }
            _albumCursor.close();
            return _albumEntity;
        }

        @Override
        protected void onPostExecute(List<AlbumEntity> albumEntities) {
            //加载数据
            mAdapter.setData(albumEntities);
        }

        /**
         * 根据ID获取相册夹的个数
         *
         * @param id
         * @return
         */
        int queryPicCountByAlbumId(String id) {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String countSql = MediaStore.Images.Media.BUCKET_ID + "=?";
            Cursor countCursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.BUCKET_ID}, countSql, new String[]{id}, null);
            int picCount = 0;
            if (countCursor != null) {
                picCount = countCursor.getCount();
                countCursor.close();
            }
            return picCount;
        }

        boolean isHiddenDir(String path) {
            String regex = "/\\.";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(path);
            return matcher.find();
        }
    }

}
