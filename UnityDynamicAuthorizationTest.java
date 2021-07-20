
package com.test.common;
        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.net.Uri;
        import android.os.Bundle;
        import android.provider.Settings;
        import com.unity3d.player.UnityPlayerActivity;

        // 动态权限
        import android.support.v4.content.PermissionChecker;
        import android.support.v4.app.ActivityCompat;

public class UnityDynamicAuthorizationTest extends UnityPlayerActivity {

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    // 定位权限
    private final String GETLOCATION = "android.permission.ACCESS_FINE_LOCATION";
    private final int GETLOCATION_CODE = 100;




    // ------- 开放给 Unity 的接口 -------
    
    // 尝试获取定位权限
    public void ACCESS_FINE_LOCATION() {
        requestExternalStorage(GETLOCATION, GETLOCATION_CODE);
    }



    // ------- Andorid 层 -------

    //前面说过了静态方法,android.permission.WRITE_EXTERNAL_STORAGE是外部存储权限,同理其他权限也可以动态请求
    private void requestExternalStorage(String permissions,int code) {
        //检查权限避免重复请求相同权限,参数:activity,权限名
        if (PermissionChecker.checkSelfPermission(this, permissions) == 0) {
            // 有权限
            // if (mPermissionCallback != null) {
            // mPermissionCallback.onGranted();
            // }
        } else {
            // 无权限；
            //请求权限,参数:activity,权限名,请求码(不同的权限要求不同的请求码,可以自己定,比如我这个权限是100,另外的可以填102,103...)
            ActivityCompat.requestPermissions(this, new String[]{permissions}, code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case GETLOCATION_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获得授权

                } else {
                    // 拒绝授权
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]) == true) {
                        showPermissionRationale_Prompt("提示", "如果没有您的授权，功能将无法正常使用，是否开启授权？",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ACCESS_FINE_LOCATION();
                                    }
                                }
                        );
                    } else {
                        // 用户选择了不再提示；
                        showPermissionRationale("提示", "如果没有您的授权，功能将无法正常使用，是否前往设置界面修改权限？", requestCode);
                    }
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request

            default: {
                super.onRequestPermissionsResult(requestCode,permissions,grantResults);
                return;
            }


        }
    }

    // 提示界面 - 拒绝
    private void showPermissionRationale_Prompt(String title, String content, DialogInterface.OnClickListener OnClick) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setNegativeButton("否", null);
        builder.setPositiveButton("是", OnClick);
        builder.show();
    }

    // 提示界面 - 拒绝 - 不再提示
    private void showPermissionRationale(String title, String content, final int code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setNegativeButton("否", null);
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoSetting(code);
            }
        });
        builder.show();
    }

    // 跳转到设置界面；
    private void gotoSetting(int code) {
        // Log.e(TAG, "前往设置界面" );
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivityForResult(intent, code);
    }

    // 从设置界面回来
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GETLOCATION_CODE) {
            // 此处可以处理从设置界面回来后的处理


        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

}
