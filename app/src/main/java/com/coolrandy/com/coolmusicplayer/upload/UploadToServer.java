package com.coolrandy.com.coolmusicplayer.upload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coolrandy.com.coolmusicplayer.R;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by randy on 2016/1/11.
 * 使用http请求最为麻烦的就是构造文件上传的报文格式，这一点比较容易出错
 * http://androidexample.com/Upload_File_To_Server_-_Android_Example/index.php?view=article_discription&aid=83&aaid=106
 * http://programmerguru.com/android-tutorial/how-to-upload-image-to-php-server/
 * http://www.codepuppet.com/2013/03/26/android-uploading-a-file-to-a-php-server/
 * http://stackoverflow.com/questions/23921356/android-upload-image-to-php-server
 * 上传文件到服务器 这里上传一张图片
 * 上传文件实质就是采用post的方式请求server
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

        String fileName = sourceFileUri;
        HttpURLConnection connection = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bufferSize, bytesAvailable;
        byte[] buffers;
        int maxBufferSize = 1 * 1024 * 1024;//1M字节

        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    + uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                    msgText.setText("Source File not exist :"
                            +uploadFilePath + "" + uploadFileName);
                }
            });
            return 0;

        }else {

            //本地上传文件存在，发起请求连接
            try {
                FileInputStream fin = new FileInputStream(sourceFile);
                URL url = new URL(sourceFileUri);

                //open url connection
                connection = (HttpURLConnection)url.openConnection();
                //设置post请求必须设置以下两行
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(connection.getOutputStream());
                //将参数头数据写入到输出流中
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                //TODO 正常设置参数头参数后，需要添加两个换行之后才是具体内容，这里分析可能是由于最后的参数设置最后包含了一个换行，所以这里只写了一个换行
                dos.writeBytes(lineEnd);
                //创建一个最大尺寸的buffer
                bytesAvailable = fin.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffers = new byte[bufferSize];

                //读取数据文件，然后写入到输出流
                bytesRead = fin.read(buffers, 0, bufferSize);//每次从输入流读取buffersize大小的数据
                while (bytesRead > 0){
                    dos.write(buffers, 0, bufferSize);//将每次读取的buffersize大小的数据写到输出流中
                    //然后重新获取输入流可获取的数据大小，并设置buffersize值，然后继续从输入流中读取数据，循环判断，直到数据读取完毕
                    bytesAvailable = fin.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fin.read(buffers, 0, bufferSize);
                }
                //最后添加换行
                dos.writeBytes(lineEnd);
                //定义最后数据分割线  --boundary--   换行
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                //获取server响应
                serverResponseCode = connection.getResponseCode();
                String serverResponseMsg = connection.getResponseMessage();

                Log.e("TAG", "Http Response is: " + serverResponseMsg + ", " + serverResponseCode);

                //根据不同想响应码执行相应的操作
                if(200 == serverResponseCode){
                    //成功响应
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String msg = "File upload completed.\n\n See uploaded file here\n\n"
                                    + "http://127.0.0.1/media/uploads/" + uploadFileName;

                            msgText.setText(msg);
                            Toast.makeText(UploadToServer.this, "Upload File Successful!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close stream
                fin.close();
                dos.flush();
                dos.close();
            }catch (MalformedURLException e){

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        msgText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(UploadToServer.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + e.getMessage(), e);
            }catch (Exception e){

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        msgText.setText("Got Exception : see logcat ");
                        Toast.makeText(UploadToServer.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Exception", "Exception : " + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;
        }
    }
}
