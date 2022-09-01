package com.thatmg393.esmanager.rpc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.thatmg393.esmanager.MainActivity;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

public class RPCActivity extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setTheme(R.style.AppThemeTransparent);
		
		showDialog();
	}
	
    @SuppressWarnings("deprecation")
	private void showDialog() {
		if (MainActivity.sharedPreferencesUtil.getString("uname") == null) {
			LayoutInflater li = LayoutInflater.from(getApplicationContext());
			View layoutV = li.inflate(R.layout.rpc_login, null);

			final AlertDialog adb = new AlertDialog.Builder(RPCActivity.this).create();

			WebView webView = layoutV.findViewById(R.id.rcp_webview);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setDatabaseEnabled(true);
			webView.getSettings().setDomStorageEnabled(true);
			
			webView.setWebViewClient(new WebViewClient() {
				@Override
				public boolean shouldOverrideUrlLoading(WebView v, String url) {
					if (url.endsWith("/app")) {
						MainActivity.sharedPreferencesUtil.addString("uname", extractToken());
						if (!Utils.ServiceUtils.isServiceRunning(getBaseContext(), RPCService.class)) {
							startService(MainActivity.rpcServIntent);
						}
						adb.dismiss();
						finish();
					}
					return true;
				}
			});
			
			adb.setButton(DialogInterface.BUTTON_NEGATIVE, "Sign in later", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface di, int idk) {
						di.dismiss();
						finish();
					}
				});

			adb.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						finish();
					}
				});

			adb.setView(layoutV);
			adb.show();
			
			webView.loadUrl("https://discord.com/login");
		} else {
			if (!Utils.ServiceUtils.isServiceRunning(getBaseContext(), RPCService.class)) {
				startService(MainActivity.rpcServIntent);
			}
			finish();
		}
	}
	
	private String extractToken() {
        try {
            File f = new File(getFilesDir().getParentFile(), "app_webview/Default/Local Storage/leveldb");
            File[] fArr = f.listFiles(new FilenameFilter(){
					@Override
					public boolean accept(File file, String name) {
						return name.endsWith(".log");
					}
				});
            if (fArr.length == 0) {
                return null;
            }
            f = fArr[0];
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("token")) {
                    break;
                }
            }
            line = line.substring(line.indexOf("token") + 5);
            line = line.substring(line.indexOf("\"") + 1);
            return line.substring(0, line.indexOf("\""));
        } catch (Throwable e) {
            return null;
        }
    }
}
