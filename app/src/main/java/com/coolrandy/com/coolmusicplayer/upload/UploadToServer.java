package com.coolrandy.com.coolmusicplayer.upload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.coolrandy.com.coolmusicplayer.R;

/**
 * Created by randy on 2016/1/11.
 * http://androidexample.com/Upload_File_To_Server_-_Android_Example/index.php?view=article_discription&aid=83&aaid=106
 * http://programmerguru.com/android-tutorial/how-to-upload-image-to-php-server/
 * http://www.codepuppet.com/2013/03/26/android-uploading-a-file-to-a-php-server/
 * http://stackoverflow.com/questions/23921356/android-upload-image-to-php-server
 * 上传文件到服务器 这里上传一张图片
 */
public class UploadToServer extends Activity{

    private Button uploadBtn;
    private TextView msgText;

    int serverResponseCode = 0;
    private ProgressDialog dialog = null;

    String uploadUri = null;

    //file path
    final String uploadFilePath = "/mnt/sdcard";
    final String uploadFileName = "service_lifecycle.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.upload_to_server_activity);
        uploadBtn = (Button)findViewById(R.id.uploadButton);
        msgText = (TextView)findViewById(R.id.messageText);

        msgText.setText("Uplaoding file path: - '/mnt/sdcard/" + uploadFileName + "'");
        //php script path
        uploadUri = "http://127.0.0.1/media/UploadToServer.php";
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(UploadToServer.this, "", "Uploading file...", true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                msgText.setText("uploading started...");
                            }
                        });

                        uploadFile(uploadFilePath + "" + uploadFileName);
                    }
                }).start();
            }
        });
    }

    //TODO
    public int uploadFile(String sourceFileUri){

        return 0;
    }
}
