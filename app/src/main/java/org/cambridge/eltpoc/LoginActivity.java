package org.cambridge.eltpoc;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.cambridge.eltpoc.javascript.CLMSJavaScriptInterface;
import org.cambridge.eltpoc.model.CLMSModel;
import org.cambridge.eltpoc.model.CLMSUser;
import org.cambridge.eltpoc.observers.Observer;
import org.cambridge.eltpoc.util.SharedPreferencesUtils;
import org.cambridge.eltpoc.util.UIUtils;

import java.net.MalformedURLException;


public class LoginActivity extends Activity implements Observer<CLMSModel> {
    private CLMSModel webModel = new CLMSModel();

    private EditText username;
    private EditText password;
    private ProgressBar progressBar;
    private View loadingLayout;
    private ImageView loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webModel.registerObserver(this);
    }

    private void initViews() {
        username = (EditText) findViewById(R.id.username_edit);
        password = (EditText) findViewById(R.id.password_edit);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        loadingLayout = findViewById(R.id.loading_layout);
        loginLayout = (ImageView) findViewById(R.id.login_layout);

        if(isPortrait())
            updateBackground(1.5f, 1);
        else
            updateBackground(1, 1.5f);

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO)
                    login(null);
                return false;
            }
        });
    }

    private void updateBackground(float widthOffset, float heightOffset) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        Bitmap resized = UIUtils.getResizedBitmap(largeIcon, (int) (width * widthOffset),
                (int) (height * heightOffset));
        largeIcon.recycle();
        loginLayout.setImageBitmap(resized);
    }

    public void login(View view) {
        loadingLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        try {
            new CLMSJavaScriptInterface(this, webModel).authenticateLogin(
                    username.getText().toString(), password.getText().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void checkLogin() {
        CLMSUser user = SharedPreferencesUtils.getLoggedInUser(this);
        if (!user.getPassword().equalsIgnoreCase("") && !user.getUsername().equalsIgnoreCase("")) {
            startMainActivity();
            try {
                new CLMSJavaScriptInterface(this, webModel).authenticateLogin(
                        user.getUsername(), user.getPassword());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else
            initViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webModel.removeObserver(this);
    }

    @Override
    public void update(CLMSModel model) {
        loadingLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        if (!model.isHasError())
            startMainActivity();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            updateBackground(1, 1.5f);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            updateBackground(1.5f, 1);
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private boolean isPortrait() {
        int orientation = this.getResources().getConfiguration().orientation;
        return orientation == Configuration.ORIENTATION_PORTRAIT;
    }
}
