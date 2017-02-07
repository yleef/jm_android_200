package com.wepinche.jmus.listener;


import com.wepinche.jmus.entity.FileType;

public interface UpdateSelectedStateListener {
    public void onSelected(String path, long fileSize, FileType type);
    public void onUnselected(String path, long fileSize, FileType type);
}
