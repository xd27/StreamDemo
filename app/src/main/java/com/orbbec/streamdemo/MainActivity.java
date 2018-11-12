package com.orbbec.streamdemo;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

public class MainActivity extends Activity  implements SurfaceHolder.Callback,PreviewCallback{

    private SurfaceView surfaceview;

    private SurfaceHolder surfaceHolder;

    private Camera camera;

    private Parameters parameters;

    int width = 640;

    int height = 480;

    int framerate = 15;

    int biterate = 120*1000;

    private static int yuvqueuesize = 10;

    //待解码视频缓冲队列，静态成员！
    public static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(yuvqueuesize);

    private AvcEncoder avcCodec;

    Button btn_start;
    Button btn_stop;
    Button btn_connect;

    private Client mClient = null;

    private boolean surfaceReady = false;
    private boolean streamMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setContentView(R.layout.activity_main);
        surfaceview = (SurfaceView)findViewById(R.id.surfaceview);
        btn_start = (Button)findViewById(R.id.btn_start);
        btn_stop = (Button)findViewById(R.id.btn_stop);
        btn_connect = (Button)findViewById(R.id.btn_connect);
        surfaceHolder = surfaceview.getHolder();
        surfaceHolder.addCallback(this);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(surfaceReady){
                    camera = getBackCamera();
                    startcamera(camera);

                    //创建AvEncoder对象
                    avcCodec = new AvcEncoder(width,height,framerate,biterate,mClient);
                    avcCodec.setMode(streamMode);
                    //启动编码线程
                    avcCodec.StartEncoderThread();
                }
            }
        });
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != camera) {
                    camera.setPreviewCallback(null);
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                    avcCodec.StopThread();
                }
            }
        });
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClient = Client.getInstance();
                streamMode = true;
            }
        });
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceReady = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != camera) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
            avcCodec.StopThread();
        }
    }


    @Override
    public void onPreviewFrame(byte[] data, android.hardware.Camera camera) {
        //将当前帧图像保存在队列中
        putYUVData(data,data.length);
    }

    public void putYUVData(byte[] buffer, int length) {
        if (YUVQueue.size() >= 10) {
            YUVQueue.poll();
        }
        YUVQueue.add(buffer);
    }


    private void startcamera(Camera mCamera){
        if(mCamera != null){
            try {
                mCamera.setPreviewCallback(this);
                mCamera.setDisplayOrientation(90);
                if(parameters == null){
                    parameters = mCamera.getParameters();
                }
                //获取默认的camera配置
                parameters = mCamera.getParameters();
                //设置预览格式
                parameters.setPreviewFormat(ImageFormat.NV21);
                //设置预览图像分辨率
                parameters.setPreviewSize(width, height);
                //配置camera参数
                mCamera.setParameters(parameters);
                //将完全初始化的SurfaceHolder传入到setPreviewDisplay(SurfaceHolder)中
                //调整相机镜头的方向
                setOrientation();
                //没有surface的话，相机不会开启preview预览
                mCamera.setPreviewDisplay(surfaceHolder);
                //调用startPreview()用以更新preview的surface，必须要在拍照之前start Preview
                mCamera.startPreview();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setOrientation() {
        // 横竖屏镜头自动调整
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait"); //
            parameters.set("rotation", 90); // 镜头角度转90度
            camera.setDisplayOrientation(90); //
        } else {// 如果是横屏
            parameters.set("orientation", "landscape"); //
            camera.setDisplayOrientation(0); //
        }
    }
    private Camera getBackCamera() {
        Camera c = null;
        try {
            //获取Camera的实例
            c = Camera.open(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //获取Camera的实例失败时返回null
        return c;
    }


}