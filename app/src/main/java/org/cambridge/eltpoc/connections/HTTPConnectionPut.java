package org.cambridge.eltpoc.connections;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import org.cambridge.eltpoc.util.DialogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by etorres on 7/8/15.
 */
public class HTTPConnectionPut extends AsyncTask<Object, Object, Object>{
    private static final String PUT_METHOD = "PUT";
    private static final String UTF_FORMAT = "UTF-8";

    private boolean isFailed = false;
    private Context context;
    private ContentValues contentValues;
    private URL url;
    private String response = "";
    private String errorMessage = "";
    private String errorTitle;

    public HTTPConnectionPut(Context context, URL url, ContentValues contentValues, String errorTitle) {
        this.context = context;
        this.url = url;
        this.contentValues = contentValues;
        this.errorTitle = errorTitle;
    }

    public interface OnPostCompletedListener {
        public void onPostCompleted(String response, boolean isFailed);
    }

    private OnPostCompletedListener onPostCompletedListener;

    public OnPostCompletedListener getOnPostCompletedListener() {
        return onPostCompletedListener;
    }

    public void setOnPostCompletedListener(OnPostCompletedListener onPostCompletedListener) {
        this.onPostCompletedListener = onPostCompletedListener;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        response = "";
        isFailed = false;
        errorMessage = "";

        HttpURLConnection urlConnection = null;
        InputStream inStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(PUT_METHOD);
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, UTF_FORMAT));
            writer.write(getQuery(contentValues));
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();
            inStream = urlConnection.getInputStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
            String temp;
            while ((temp = bReader.readLine()) != null)
                response += temp;
        } catch (final Exception e) {
            isFailed = true;
            errorMessage = e.getMessage();
            e.printStackTrace();
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException ignored) {
                }
            }
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (isFailed)
            DialogUtils.createDialog(context, errorTitle, errorMessage);
        if (onPostCompletedListener != null)
            onPostCompletedListener.onPostCompleted(response, isFailed);
    }

    private String getQuery(ContentValues params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> entry : params.valueSet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), UTF_FORMAT));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue().toString(), UTF_FORMAT));
        }
        return result.toString();
    }
}
