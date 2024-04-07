package com.fongmi.android.tv.ui.dialog;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.fongmi.android.tv.Setting;
import com.fongmi.android.tv.databinding.DialogX5webviewBinding;
import com.fongmi.android.tv.utils.Download;
import com.fongmi.android.tv.utils.Notify;
import com.fongmi.android.tv.utils.Xwalk;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.io.File;
import java.util.Locale;

public class X5WebViewDialog  implements Download.Callback {

    private DialogX5webviewBinding binding;
    private AlertDialog dialog;
    private Activity activity;
    private boolean confirm;

    public X5WebViewDialog(Activity activity) {
        this.confirm = false;
        this.activity = activity;
    }

    public void show() {
        binding = DialogX5webviewBinding.inflate(LayoutInflater.from(activity));
        binding.confirm.setOnClickListener(this::confirm);
        binding.cancel.setOnClickListener(this::cancel);
        dialog = new MaterialAlertDialogBuilder(activity).setView(binding.getRoot()).setCancelable(false).create();
        dialog.show();
    }

    public static X5WebViewDialog create(Activity activity) {
        return new X5WebViewDialog(activity);
    }

    private String getXwalk() {
        return Xwalk.url();
    }

    private File getFile() {
        return Xwalk.file();
    }

    private void cancel(View view) {
        dismiss();
    }

    private void confirm(View view) {
        if (confirm) return;
        confirm = true;
        binding.confirm.setEnabled(false);
        Xwalk.remove();
        Download.create(getXwalk(), getFile(), this).start();
    }

    private void dismiss() {
        try {
            if (dialog != null) dialog.dismiss();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void progress(int progress) {
        binding.confirm.setText(String.format(Locale.getDefault(), "%1$d%%", progress));
    }

    @Override
    public void error(String msg) {
        Notify.show(msg);
        Setting.putParseWebView(0);
        dismiss();
    }

    @Override
    public void success(File file) {
        boolean extract = Xwalk.extract();
        if (extract) {
            Setting.putParseWebView(1);
            Xwalk.init();
        } else {
            Setting.putParseWebView(0);
        }
        dismiss();
    }


}