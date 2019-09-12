package cn.muse.giffun.activity;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author: wanshi
 * created on: 2019-09-10 16:04
 * description:
 */
public class BaseActivity extends AppCompatActivity {

    protected ProgressDialog progressDialog;
    protected boolean progressCanOutCancel = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
    }

    public void showProgressDialog() {
        showProgressDialog("请稍后...");
    }

    public void setProgressCanOutCancel(boolean progressCanOutCancel) {
        this.progressCanOutCancel = progressCanOutCancel;
    }

    public void showProgressDialog(String loadingTxt) {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.setCanceledOnTouchOutside(progressCanOutCancel);
            if (!TextUtils.isEmpty(loadingTxt)) {
                progressDialog.setMessage(loadingTxt);
            }
            progressDialog.show();
        }
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
