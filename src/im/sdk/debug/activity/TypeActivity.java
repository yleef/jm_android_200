package im.sdk.debug.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.DownloadCompletionCallback;
import cn.jpush.im.android.api.callback.ProgressUpdateCallback;
import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.ContactNotifyEvent;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.event.NotificationClickEvent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import  com.wepinche.jmus.R;
import im.sdk.debug.activity.conversation.ConversationActivity;
import im.sdk.debug.activity.createmessage.CreateGroupTextMsgActivity;
import im.sdk.debug.activity.createmessage.CreateMessageActivity;
import im.sdk.debug.activity.createmessage.CreateSigTextMessageActivity;
import im.sdk.debug.activity.createmessage.ShowCustomMessageActivity;
import im.sdk.debug.activity.createmessage.ShowDownloadVoiceInfoActivity;
import im.sdk.debug.activity.createmessage.ShowMessageActivity;
import im.sdk.debug.activity.friend.FriendContactManager;
import im.sdk.debug.activity.friend.ShowFriendReasonActivity;
import im.sdk.debug.activity.groupinfo.GroupInfoActivity;
import im.sdk.debug.activity.imagecontent.ShowDownloadPathActivity;
import im.sdk.debug.activity.notify.ShowGroupNotificationActivity;
import im.sdk.debug.activity.setting.SettingMainActivity;
import im.sdk.debug.activity.setting.ShowLogoutReasonActivity;

/**
 * Created by ${chenyn} on 16/3/23.
 *
 * @desc : 各个接口的的引导界面
 */
public class TypeActivity extends Activity implements View.OnClickListener {
    public static final String CREATE_GROUP_CUSTOM_KEY     = "create_group_custom_key";
    public static final String SET_DOWNLOAD_PROGRESS       = "set_download_progress";
    public static final String IS_DOWNLOAD_PROGRESS_EXISTS = "is_download_progress_exists";
    public static final String CUSTOM_MESSAGE_STRING       = "custom_message_string";
    public static final String CUSTOM_FROM_NAME            = "custom_from_name";
    public static final String DOWNLOAD_INFO               = "download_info";
    public static final String DOWNLOAD_ORIGIN_IMAGE       = "download_origin_image";
    public static final String DOWNLOAD_THUMBNAIL_IMAGE    = "download_thumbnail_image";
    public static final String IS_UPLOAD                   = "is_upload";
    public static final String LOGOUT_REASON               = "logout_reason";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JMessageClient.registerEventReceiver(this);
        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_type);
        Button bt_type = (Button) findViewById(R.id.bt_about_setting);
        Button bt_createMessage = (Button) findViewById(R.id.bt_create_message);
        Button bt_groupInfo = (Button) findViewById(R.id.bt_group_info);
        Button bt_conversation = (Button) findViewById(R.id.bt_conversation);
        Button bt_friend = (Button) findViewById(R.id.bt_friend);

        bt_type.setOnClickListener(this);
        bt_createMessage.setOnClickListener(this);
        bt_groupInfo.setOnClickListener(this);
        bt_conversation.setOnClickListener(this);
        bt_friend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.bt_about_setting:
                intent.setClass(getApplicationContext(), SettingMainActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_create_message:
                intent.setClass(getApplicationContext(), CreateMessageActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_group_info:
                intent.setClass(getApplicationContext(), GroupInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_conversation:
                intent.setClass(getApplicationContext(), ConversationActivity.class);
                startActivity(intent);
                break;
            case R.id.bt_friend:
                intent.setClass(getApplicationContext(), FriendContactManager.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void onEvent(NotificationClickEvent event) {

        Message msg = event.getMessage();

        final Intent notificationIntent = new Intent(getApplicationContext(), ShowMessageActivity.class);
        MessageContent content = msg.getContent();
        switch (msg.getContentType()) {
            case text:
                TextContent textContent = (TextContent) content;
                notificationIntent.setFlags(1);
                notificationIntent.putExtra(CreateSigTextMessageActivity.TEXT_MESSAGE, "消息类型 = " + msg.getContentType() +
                        "\n消息内容 = " + textContent.getText() + "\n附加字段 = " + textContent.getStringExtras());
                startActivity(notificationIntent);
                break;
            case image:
                ImageContent imageContent = (ImageContent) content;
                imageContent.downloadOriginImage(msg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int i, String s, File file) {
                        if (i == 0) {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                            byte[] bitmapByte = bos.toByteArray();
                            notificationIntent.setFlags(2);
                            notificationIntent.putExtra("bitmap", bitmapByte);
                            startActivity(notificationIntent);
                        }
                    }
                });
                final List<String> list = new ArrayList<String>();
                msg.setOnContentDownloadProgressCallback(new ProgressUpdateCallback() {
                    @Override
                    public void onProgressUpdate(double v) {
                        String progressStr = (int) (v * 100) + "%";
                        list.add(progressStr);
                        notificationIntent.putStringArrayListExtra(SET_DOWNLOAD_PROGRESS, (ArrayList<String>) list);
                    }
                });

                boolean callbackExists = msg.isContentDownloadProgressCallbackExists();
                notificationIntent.putExtra(IS_DOWNLOAD_PROGRESS_EXISTS, callbackExists + "");
                break;
            case voice:
                VoiceContent voiceContent = (VoiceContent) content;
                voiceContent.downloadVoiceFile(msg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int i, String s, File file) {
                        if (i == 0) {
                            String path = file.getPath();
                            notificationIntent.setFlags(3);
                            notificationIntent.putExtra("voice", path);
                            startActivity(notificationIntent);
                        }
                    }
                });
                break;
            case file:
                UserInfo fromUser = msg.getFromUser();
                String userName = fromUser.getUserName();
                String appKey = fromUser.getAppKey();
                ConversationType targetType = msg.getTargetType();

                int id = msg.getId();

                notificationIntent.putExtra("user", userName);
                notificationIntent.putExtra("appkey", appKey);
                notificationIntent.putExtra("msgid", id);
                notificationIntent.putExtra("isGroup", targetType + "");
                notificationIntent.setFlags(10);

                startActivity(notificationIntent);
                break;

            case location:
                LocationContent locationContent = (LocationContent) content;
                String address = locationContent.getAddress();
                Number latitude = locationContent.getLatitude();
                Number scale = locationContent.getScale();
                Number longitude = locationContent.getLongitude();

                String la = String.valueOf(latitude);
                String sc = String.valueOf(scale);
                String lo = String.valueOf(longitude);

                notificationIntent.setFlags(4);
                notificationIntent.putExtra("address", address);
                notificationIntent.putExtra("latitude", la);
                notificationIntent.putExtra("scale", sc);
                notificationIntent.putExtra("longitude", lo);

                startActivity(notificationIntent);
                break;

            default:
                break;
        }
    }

    public void onEvent(MessageEvent event) {
        Message msg = event.getMessage();
        switch (msg.getContentType()) {
            case custom:
                final ConversationType targetType = event.getMessage().getTargetType();
                final Intent intent = new Intent(getApplicationContext(), ShowCustomMessageActivity.class);
                CustomContent customContent = (CustomContent) msg.getContent();
                Map allStringValues = customContent.getAllStringValues();
                if (targetType.equals(ConversationType.group)) {
                    intent.putExtra(CREATE_GROUP_CUSTOM_KEY, allStringValues.toString());
                    intent.setFlags(1);

                } else if (targetType.equals(ConversationType.single)) {
                    intent.putExtra(CUSTOM_MESSAGE_STRING, allStringValues.toString());
                    UserInfo fromUser = msg.getFromUser();
                    intent.putExtra(CUSTOM_FROM_NAME, fromUser.getUserName());
                    intent.setFlags(2);
                }
                startActivity(intent);

                break;
            case voice:
                final Intent intentVoice = new Intent(getApplicationContext(), ShowDownloadVoiceInfoActivity.class);
                final VoiceContent voiceContent = (VoiceContent) msg.getContent();
                final int duration = voiceContent.getDuration();
                final String format = voiceContent.getFormat();
                /**=================     下载语音文件    =================*/
                voiceContent.downloadVoiceFile(msg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int i, String s, File file) {
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "下载成功", Toast.LENGTH_SHORT).show();
                            intentVoice.putExtra(DOWNLOAD_INFO, "path = " + file.getPath() + "\n" + "duration = " + duration + "\n" + "format = " + format + "\n");
                            startActivity(intentVoice);
                        } else {
                            Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
                            Log.i("CreateSigVoiceMsgActivity", "downloadVoiceFile" + ", responseCode = " + i + " ; desc = " + s);
                        }
                    }
                });
                break;
            case eventNotification:
                String eventText = ((EventNotificationContent) msg.getContent()).getEventText();
                Intent intentNotification = new Intent(getApplicationContext(), ShowGroupNotificationActivity.class);
                intentNotification.putExtra(CreateGroupTextMsgActivity.GROUP_NOTIFICATION, eventText);

                List<String> userNames = ((EventNotificationContent) msg.getContent()).getUserNames();
                intentNotification.putExtra(CreateGroupTextMsgActivity.GROUP_NOTIFICATION_LIST, userNames.toString());
                startActivity(intentNotification);
                break;
            case image:

                final Intent intentImage = new Intent(getApplicationContext(), ShowDownloadPathActivity.class);
                final ImageContent imageContent = (ImageContent) msg.getContent();

                /**=================     下载图片信息中的缩略图    =================*/
                imageContent.downloadThumbnailImage(msg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int i, String s, File file) {
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "下载缩略图成功", Toast.LENGTH_SHORT).show();
                            intentImage.putExtra(DOWNLOAD_THUMBNAIL_IMAGE, file.getPath());
                        } else {
                            Toast.makeText(getApplicationContext(), "下载原图失败", Toast.LENGTH_SHORT).show();
                            Log.i("CreateImageContentAsyncFile", "downloadThumbnailImage" + ", responseCode = " + i + " ; desc = " + s);
                        }
                    }
                });

                /**=================     下载图片消息中的原图    =================*/
                imageContent.downloadOriginImage(msg, new DownloadCompletionCallback() {
                    @Override
                    public void onComplete(int i, String s, File file) {
                        if (i == 0) {
                            Toast.makeText(getApplicationContext(), "下载原图成功", Toast.LENGTH_SHORT).show();
                            intentImage.putExtra(IS_UPLOAD, imageContent.isFileUploaded() + "");
                            intentImage.putExtra(DOWNLOAD_ORIGIN_IMAGE, file.getPath());
                            startActivity(intentImage);
                        } else {
                            Toast.makeText(getApplicationContext(), "下载原图失败", Toast.LENGTH_SHORT).show();
                            Log.i("CreateImageContentAsyncFile", "downloadOriginImage" + ", responseCode = " + i + " ; desc = " + s);
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    public void onEvent(ContactNotifyEvent event) {
        String reason = event.getReason();
        String fromUsername = event.getFromUsername();
        String appkey = event.getfromUserAppKey();

        Intent intent = new Intent(getApplicationContext(), ShowFriendReasonActivity.class);

        switch (event.getType()) {
            case invite_received://收到好友邀请
                intent.putExtra("invite_received", "fromUsername = " + fromUsername + "\nfromUserAppKey" + appkey + "\nreason = " + reason);
                intent.putExtra("username", fromUsername);
                intent.putExtra("appkey", appkey);
                intent.setFlags(1);
                startActivity(intent);
                break;
            case invite_accepted://对方接收了你的好友邀请
                intent.putExtra("invite_accepted", "对方接受了你的好友邀请");
                intent.setFlags(2);
                startActivity(intent);
                break;
            case invite_declined://对方拒绝了你的好友邀请
                intent.putExtra("invite_declined", "对方拒绝了你的好友邀请\n拒绝原因:" + event.getReason());
                intent.setFlags(3);
                startActivity(intent);
                break;
            case contact_deleted://对方将你从好友中删除
                intent.putExtra("contact_deleted", "对方将你从好友中删除");
                intent.setFlags(4);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void onEvent(LoginStateChangeEvent event) {
        LoginStateChangeEvent.Reason reason = event.getReason();
        UserInfo myInfo = event.getMyInfo();
        Intent intent = new Intent(getApplicationContext(), ShowLogoutReasonActivity.class);
        intent.putExtra(LOGOUT_REASON, "reason = " + reason + "\n" + "logout user name = " + myInfo.getUserName());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }
}
