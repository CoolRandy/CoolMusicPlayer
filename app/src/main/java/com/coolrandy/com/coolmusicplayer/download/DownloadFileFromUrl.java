package com.coolrandy.com.coolmusicplayer.download;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by admin on 2016/1/11.
 */
public class DownloadFileFromUrl extends AsyncTask<String, String, String> {

    private Context context;
    private ImageView imageView;
    private ProgressDialog pDialog;

    public DownloadFileFromUrl(Context context, ImageView imageView, ProgressDialog pDialog) {
        this.context = context;
        this.imageView = imageView;
        this.pDialog = pDialog;
    }

    /**
     * 在开始后台加载之前，显示progress
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showDialog(pDialog);
    }


    @Override
    protected String doInBackground(String... params) {
        int count;
        try {
            URL url = new URL(params[0]);
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file  不建议采用硬编码，因为不同的手机设备可能sdcard路径不同
//            OutputStream output = new FileOutputStream("/sdcard/downloadedfile.jpg");
            String storageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            String fileName = "downloadedfile.jpg";
            File imageFile = new File(storageDir + "/" + fileName);
            OutputStream output = new FileOutputStream(imageFile);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress(""+(int)((total*100)/lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
//        super.onProgressUpdate(values);
        // setting progress percentage
        pDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String s) {
//        super.onPostExecute(s);
        // dismiss the dialog after the file was downloaded
        dismissDialog(pDialog);

        // Displaying downloaded image into image view
        // Reading image path from sdcard
        String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
        // setting downloaded into image view
        imageView.setImageDrawable(Drawable.createFromPath(imagePath));
    }

    public void showDialog(ProgressDialog pDialog){
//        pDialog = new ProgressDialog(context);
//        pDialog.setMessage("Downloading file. Please wait...");
//        pDialog.setIndeterminate(false);
//        pDialog.setMax(100);
//        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        pDialog.setCancelable(true);
        pDialog.show();
    }
    public void dismissDialog(ProgressDialog pDialog){
        pDialog.dismiss();
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
