package kim.hsl.rtmp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /**
     * 显示图像的 SurfaceView 组件
     */
    private SurfaceView mSurfaceView;

    /**
     * 直播推流器
     */
    private LivePusher mLivePusher;

    /**
     * 需要获取的权限列表
     */
    private String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };

    /**
     * 动态申请权限的请求码
     */
    private static final int PERMISSION_REQUEST_CODE = 888;

    /**
     * 动态申请权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initPermissions() {
        if (isLacksPermission()) {
            //动态申请权限 , 第二参数是请求吗
            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * 判断是否有 permissions 中的权限
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isLacksPermission() {
        for (String permission : permissions) {
            if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化权限
        initPermissions();

        mSurfaceView = findViewById(R.id.surfaceView);

        // 创建直播推流器, 用于将采集的视频数据推流到服务器端
        // 800_000 代表 800K 的码率
        mLivePusher = new LivePusher(this, 800, 480, 800_000, 10, Camera.CameraInfo.CAMERA_FACING_BACK);

        // 设置 Camera 采集的图像本地预览的组件, 在 mSurfaceView 界面先绘制摄像头
        // 此处要为 SurfaceHolder 设置 SurfaceHolder.Callback 回调 , 通过里面的回调函数
        // 驱动整个推流开始
        mLivePusher.setPreviewDisplay(mSurfaceView.getHolder());

        findViewById(R.id.button_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // rtmp://47.94.36.51/myapp/0
                // 0 相当于 直播的 密码
                // 配置好服务器后, 记录 IP 地址, 替换 47.94.36.51 IP 地址
                mLivePusher.startLive("rtmp://47.94.36.51/myapp/0");
            }
        });

    }
}
