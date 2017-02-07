package im.sdk.debug.activity.conversation;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import  com.wepinche.jmus.R;

/**
 * Created by ${chenyn} on 16/4/11.
 *
 * @desc :获取会话的info
 */
public class GetConversationInfoActivity extends Activity {


    private EditText  mEt_userName;
    private Button    mBt_send;
    private TextView  mTv_showInfo;
    private Button    mBt_getUnreadCount;
    private Button    mBt_resetUnreadCount;
    private EditText  mEt_assignMessageId;
    private Button    mBt_getMessage;
    private Button    mBt_deleteMessage;
    private EditText  mEt_userAppkey;
    private EditText  mEt_groupId;
    private UserInfo  mUserInfo;
    private GroupInfo mGroupInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    private void initData() {
        mBt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Conversation conversation = getConversation();
                if (conversation == null) return;
                /**=================     获取会话中的属性    =================*/
                List<Message> allMessage = conversation.getAllMessage();
                String id = conversation.getId();
                ConversationType type = conversation.getType();
                String targetAppKey = conversation.getTargetAppKey();
                String title = conversation.getTitle();
                File avatarFile = conversation.getAvatarFile();
                Message latestMessage = conversation.getLatestMessage();
                String text = null;
                if (latestMessage != null) {
                    MessageContent content = latestMessage.getContent();
                    if (content.getContentType() == ContentType.text) {
                        TextContent stringExtra = (TextContent) content;
                        text = stringExtra.getText();
                    }
                }

                String path = null;
                if (avatarFile != null) {
                    path = avatarFile.getPath();
                }
                mTv_showInfo.setText("");

                StringBuilder sb = new StringBuilder();
                for (Message msgList : allMessage) {
                    sb.append("消息ID = " + msgList.getId());
                    sb.append("~~~消息类型 = " + msgList.getFromType());
                    sb.append("\n");
                }
                String groupName = null;
                String userName = null;
                long groupId = 0;
                if (!TextUtils.isEmpty(mEt_userName.getText().toString()) && mUserInfo != null) {
                    userName = mUserInfo.getUserName();
                }
                if (!TextUtils.isEmpty(mEt_groupId.getText().toString()) && mGroupInfo != null) {
                    groupName = mGroupInfo.getGroupName();
                    groupId = mGroupInfo.getGroupID();
                }
                mTv_showInfo.append("getId = " + id + "\n" + "getType = " + type + "\n"
                        + "getTargetAppKey = " + targetAppKey + "\n" + "getTitle = "
                        + title + "\n" + "getAvatarPath = " + path + "\n" + "最后一条文本消息 = "
                        + text + "\n" + "groupId = " + groupId + "\n" + "userName = " + userName + "\n"
                        + "groupName = " + groupName + "\ngetAllMessage = \n" + sb.toString());
            }
        });
/**#################    获取未读消息数    #################*/
        mBt_getUnreadCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Conversation conversation = getConversation();
                if (conversation == null) return;
                int unReadMsgCnt = conversation.getUnReadMsgCnt();
                mTv_showInfo.setText("");
                mTv_showInfo.append("getUnReadMsgCnt = " + unReadMsgCnt);
            }
        });
        /**#################    重置未读消息数    #################*/
        mBt_resetUnreadCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTv_showInfo.setText("");
                Conversation conversation = getConversation();
                if (conversation == null) return;
                boolean b = conversation.resetUnreadCount();
                Toast.makeText(getApplicationContext(), "重置" + b, Toast.LENGTH_SHORT).show();
            }
        });
        /**#################    获取指定id的message    #################*/
        mBt_getMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageId = mEt_assignMessageId.getText().toString();
                Conversation conversation = getConversation();
                if (conversation == null) return;
                if (TextUtils.isEmpty(messageId)) {
                    getAllMessage(conversation);
                } else {
                    try {
                        int id = Integer.parseInt(messageId);
                        Message message = conversation.getMessage(id);

                        mTv_showInfo.setText("");
                        if (message != null) {
                            String userName = message.getFromUser().getUserName();
                            mTv_showInfo.append("指定id的message = " + "\n消息id = " + message.getId() + "~~~消息发送者 = " + userName);
                        } else {
                            Toast.makeText(GetConversationInfoActivity.this, "获取的id不存在", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(GetConversationInfoActivity.this, "输入不合法", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        /**#################    删除指定id的message    #################*/
        mBt_deleteMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageId = mEt_assignMessageId.getText().toString();

                Conversation conversation = getConversation();
                if (conversation == null) return;
                if (!TextUtils.isEmpty(messageId)) {
                    mTv_showInfo.setText("");
                    try {
                        int id = Integer.parseInt(messageId);
                        boolean b = conversation.deleteMessage(id);
                        getAllMessage(conversation);
                        Toast.makeText(getApplicationContext(), "删除指定id的message = " + b, Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(GetConversationInfoActivity.this, "输入不合法", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    boolean b = conversation.deleteAllMessage();
                    getAllMessage(conversation);
                    Toast.makeText(getApplicationContext(), "删除所有消息 = " + b, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private Conversation getConversation() {
        String userName = mEt_userName.getText().toString();
        String appKey = mEt_userAppkey.getText().toString();
        String groupID = mEt_groupId.getText().toString();
        Conversation conversation;
        if (TextUtils.isEmpty(userName) && !TextUtils.isEmpty(groupID)) {
            long gid = Long.parseLong(groupID);
            conversation = JMessageClient.getGroupConversation(gid);
            if (conversation != null) {
                mGroupInfo = (GroupInfo) conversation.getTargetInfo();
            } else {
                Toast.makeText(GetConversationInfoActivity.this, "会话不存在", Toast.LENGTH_SHORT).show();
            }
        } else if (!TextUtils.isEmpty(userName) && TextUtils.isEmpty(groupID)) {
            conversation = JMessageClient.getSingleConversation(userName, appKey);
            if (conversation != null) {
                mUserInfo = (UserInfo) conversation.getTargetInfo();
            } else {
                Toast.makeText(GetConversationInfoActivity.this, "会话不存在", Toast.LENGTH_SHORT).show();
                return null;
            }
        } else {
            mTv_showInfo.setText("");
            Toast.makeText(GetConversationInfoActivity.this, "需要userName或groupId", Toast.LENGTH_SHORT).show();
            return null;
        }
        return conversation;
    }

    private void getAllMessage(Conversation conversation) {
        List<Message> allMessage = conversation.getAllMessage();
        StringBuilder sb = new StringBuilder();
        for (Message msg : allMessage) {
            sb.append("消息ID = " + msg.getId());
            sb.append("~~~消息发送者 = " + msg.getFromUser().getUserName());
            sb.append("\n");
        }
        mTv_showInfo.setText("");
        mTv_showInfo.append("getAllMessage = " + "\n" + sb.toString());
    }


    private void initView() {
        setContentView(R.layout.activity_get_conversation_info);
        mEt_userName = (EditText) findViewById(R.id.et_user_name);
        mBt_send = (Button) findViewById(R.id.bt_send);
        mTv_showInfo = (TextView) findViewById(R.id.tv_show_info);
        mBt_getUnreadCount = (Button) findViewById(R.id.bt_get_unread_count);
        mBt_resetUnreadCount = (Button) findViewById(R.id.bt_reset_unread_count);
        mEt_assignMessageId = (EditText) findViewById(R.id.et_assign_message_id);
        mBt_getMessage = (Button) findViewById(R.id.bt_get_message);
        mBt_deleteMessage = (Button) findViewById(R.id.bt_delete_message);
        mEt_userAppkey = (EditText) findViewById(R.id.et_user_appkey);
        mEt_groupId = (EditText) findViewById(R.id.et_group_id);
    }
}
