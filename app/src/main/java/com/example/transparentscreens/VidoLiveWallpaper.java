package com.example.transparentscreens;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * 项目名： TransparentScreens
 * 包名：   com.example.transparentscreens
 * 文件名：  VidoLiveWallpaper
 * 创建者：  Koc
 * 创建时间： 2017/5/16 9:22
 * 描述：       WallpaperService
 */

public class VidoLiveWallpaper extends WallpaperService {

    private static final String VIDEO_PARAMS_CONTROL_ACTION = "com.koc.livewallpaper";
    private static final String KEY_ACTION = "action";
    private static final int ACTION_VOICE_SILENCE = 110;
    private static final int ACTION_VOICE_NORMAL = 111;

    private BroadcastReceiver mVideoParamsControlReceiver;

    @Override
    public Engine onCreateEngine() {
        return new VideoEngine();
    }


    public static void voiceSilence(Context context) {
        Intent intent = new Intent(VidoLiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(VidoLiveWallpaper.KEY_ACTION, VidoLiveWallpaper.ACTION_VOICE_SILENCE);
        context.sendBroadcast(intent);
    }

    public static void voiceNormal(Context context) {
        Intent intent = new Intent(VidoLiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
        intent.putExtra(VidoLiveWallpaper.KEY_ACTION, VidoLiveWallpaper.ACTION_VOICE_NORMAL);
        context.sendBroadcast(intent);
    }

    public static void setToWallPaper(Context context) {
        final Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(context, VidoLiveWallpaper.class));
        context.startActivity(intent);
    }

    class VideoEngine extends Engine {
        private MediaPlayer mMediaPlayer;
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            IntentFilter intentFilter = new IntentFilter(VIDEO_PARAMS_CONTROL_ACTION);
            registerReceiver(mVideoParamsControlReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    int action = intent.getIntExtra(KEY_ACTION, -1);
                    switch (action) {
                        case ACTION_VOICE_NORMAL:
                            mMediaPlayer.setVolume(1.0f, 1.0f);
                            break;
                        case ACTION_VOICE_SILENCE:
                            mMediaPlayer.setVolume(0, 0);
                            break;
                    }
                }
            }, intentFilter);
        }

        @Override
        public void onDestroy() {
            unregisterReceiver(mVideoParamsControlReceiver);
            super.onDestroy();
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setSurface(holder.getSurface());
            try {
                AssetManager assetManager = getApplicationContext().getAssets();
                AssetFileDescriptor fileDescriptor = assetManager.openFd("test1.mp4");
                mMediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                        fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                mMediaPlayer.setLooping(true);
                mMediaPlayer.setVolume(0, 0);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {

            if (visible) {
                mMediaPlayer.start();
            } else {
                mMediaPlayer.pause();

            }
        }

    }


}
