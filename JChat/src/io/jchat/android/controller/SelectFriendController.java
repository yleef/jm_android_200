package com.wepinche.jmus.controller;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.wepinche.jmus.R;
import com.wepinche.jmus.activity.SelectFriendActivity;
import com.wepinche.jmus.adapter.StickyListAdapter;
import com.wepinche.jmus.application.JChatDemoApplication;
import com.wepinche.jmus.database.FriendEntry;
import com.wepinche.jmus.database.UserEntry;
import com.wepinche.jmus.tools.PinyinComparator;
import com.wepinche.jmus.view.SelectFriendView;
import com.wepinche.jmus.view.SideBar;

/**
 * Created by Ken on 2015/10/9.
 */
public class SelectFriendController implements View.OnClickListener,
        SideBar.OnTouchingLetterChangedListener, TextWatcher{

    private SelectFriendView mSelectFriendView;
    private SelectFriendActivity mContext;
    private StickyListAdapter mAdapter;
    private List<FriendEntry> mData;

    public SelectFriendController(SelectFriendView view, SelectFriendActivity context) {
        this.mSelectFriendView = view;
        this.mContext = context;
        initData();
    }

    private void initData() {
        UserEntry userEntry = JChatDemoApplication.getUserEntry();
        mData = userEntry.getFriends();
        Collections.sort(mData, new PinyinComparator());
        mAdapter = new StickyListAdapter(mContext, mData, true);
        mSelectFriendView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.jmui_cancel_btn:
                mContext.finish();
                break;
            case R.id.search_btn:
                break;
            case R.id.finish_btn:
                Intent intent = new Intent();
                intent.putStringArrayListExtra("SelectedUser", mAdapter.getSelectedUser());
                mContext.setResult(JChatDemoApplication.RESULT_CODE_SELECT_FRIEND, intent);
                mContext.finish();
                break;
        }
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        //该字母首次出现的位置
        int position = mAdapter.getSectionForLetter(s);
        Log.d("SelectFriendController", "Section position: " + position);
        if (position != -1 && position < mAdapter.getCount()) {
            mSelectFriendView.setSelection(position);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        filterData(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<FriendEntry> filterDateList = new ArrayList<FriendEntry>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = mData;
        } else {
            filterDateList.clear();
            for(FriendEntry entry : mData) {
                String name = entry.displayName;
                if (name.contains(filterStr) || name.startsWith(filterStr)
                        || entry.letter.equals(filterStr.substring(0, 1).toUpperCase())) {
                    filterDateList.add(entry);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, new PinyinComparator());
        mAdapter.updateListView(filterDateList);
    }

}
