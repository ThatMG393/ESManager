package com.thatmg393.esmanager.crashhandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.thatmg393.esmanager.MainActivity;

public class DebugActivity extends Activity {
	String[] exceptionType = {
			"StringIndexOutOfBoundsException",
			"IndexOutOfBoundsException",
			"ArithmeticException",
			"NumberFormatException",
			"ActivityNotFoundException"
	};
	String[] errMessage= {
			"Invalid string operation\n",
			"Invalid list operation\n",
			"Invalid arithmetical operation\n",
			"Invalid toNumber block operation\n",
			"Invalid intent operation"
	};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
			stopService(MainActivity.rpcActIntent);
		} catch (NullPointerException npe) {
			System.out.println(npe);
		}

		Intent intent = getIntent();
		String errMsg = "";
		StringBuilder madeErrMsg = new StringBuilder();
		if(intent != null){
			errMsg = intent.getStringExtra("error");
			String[] spilt = errMsg.split("\n");
			//errMsg = spilt[0];
			try {
				for (int j = 0; j < exceptionType.length; j++) {
					if (spilt[0].contains(exceptionType[j])) {
						madeErrMsg.append(errMessage[j]);
						int addIndex = spilt[0].indexOf(exceptionType[j]) + exceptionType[j].length();
						madeErrMsg.append(spilt[0].substring(addIndex, spilt[0].length()));
						break;
					}
				}
				if(madeErrMsg.toString().isEmpty()) madeErrMsg.append(errMsg);
			} catch(Exception e) { }
		}
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setTitle("An error occured");
		bld.setMessage( madeErrMsg );
		bld.setNeutralButton("End Application", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
		bld.create().show();
    }
}