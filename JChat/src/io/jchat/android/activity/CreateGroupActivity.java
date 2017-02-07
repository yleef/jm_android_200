package com.wepinche.jmus.activity;

import android.content.Intent;
import android.os.Bundle;
import com.wepinche.jmus.R;
import com.wepinche.jmus.application.JChatDemoApplication;
import com.wepinche.jmus.chatting.ChatActivity;
import com.wepinche.jmus.controller.CreateGroupController;
import com.wepinche.jmus.view.CreateGroupView;

/*
创建群聊
 */
public class CreateGroupActivity extends BaseActivity{
	
	private CreateGroupView mCreateGroupView;
	private CreateGroupController mCreateGroupController;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		mCreateGroupView = (CreateGroupView) findViewById(R.id.create_group_view);
		mCreateGroupView.initModule();
		mCreateGroupController = new CreateGroupController(mCreateGroupView, this);
		mCreateGroupView.setListeners(mCreateGroupController);
	}


	public void startChatActivity(long groupId, String groupName) {
		Intent intent = new Intent();
		//设置跳转标志
		intent.putExtra("fromGroup", true);
		intent.putExtra(JChatDemoApplication.GROUP_ID, groupId);
		intent.putExtra(JChatDemoApplication.GROUP_NAME, groupName);
		intent.putExtra(JChatDemoApplication.MEMBERS_COUNT, 1);
		intent.setClass(this, ChatActivity.class);
		startActivity(intent);
		finish();
	}

}
