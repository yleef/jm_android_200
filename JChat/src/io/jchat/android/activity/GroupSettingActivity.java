package com.wepinche.jmus.activity;

import android.os.Bundle;
import com.wepinche.jmus.R;
import com.wepinche.jmus.controller.GroupSettingController;
import com.wepinche.jmus.view.GroupSettingView;

public class GroupSettingActivity extends BaseActivity {
	
	private GroupSettingView mGroupSettingView;
	private GroupSettingController mGroupSettingController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_group_setting);
		
		int which = getIntent().getIntExtra(ChatDetailActivity.START_FOR_WHICH, 0);
		mGroupSettingView = (GroupSettingView) findViewById(R.id.group_setting_view);
		mGroupSettingView.initModule();
		mGroupSettingController = new GroupSettingController(mGroupSettingView, this, which);
		mGroupSettingView.setListeners(mGroupSettingController);
	}

}
