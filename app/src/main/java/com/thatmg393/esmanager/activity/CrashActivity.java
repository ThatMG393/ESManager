package com.thatmg393.esmanager.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itsaky.utils.logsender.LogSender;
import com.thatmg393.esmanager.BuildConfig;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class CrashActivity extends AppCompatActivity {
    
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private FirebaseStorage firebase;
    private StorageReference firebaseSR;
    
    private Handler delayer = new Handler();
    
    private boolean isUserViewingLogs;
    private boolean isUserAuthenticated;
    private String fName;
    private String logFilePath;
    private String currentDate;
    private final String newLine = System.lineSeparator();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        currentDate = getCurrentDate();
        LogSender.startLogging(this);
		super.onCreate(savedInstanceState);
        
        firebaseAuth = FirebaseAuth.getInstance();
    	if (currentUser == null) {
            firebaseAuth.signInAnonymously()
        		.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                	@Override
                	public void onComplete(Task<AuthResult> result) {
                    	if (result.isSuccessful()) {
                        	currentUser = firebaseAuth.getCurrentUser();
                    	}
                	}
            	});
        }
        
        firebase = FirebaseStorage.getInstance();
        firebaseSR = firebase.getReference();
        
        setupCrashDialog();
	}
    
    private void setupCrashDialog() {
        LayoutInflater li = LayoutInflater.from(new ContextThemeWrapper(getApplicationContext(), R.style.App_Dark));
        View layoutV = li.inflate(R.layout.crash_dialog, null);
        
        setupCrashDialogView(layoutV);
        
        AlertDialog bld = new AlertDialog.Builder(this).create();
		bld.setTitle("UH OH");
		bld.setIcon(R.drawable.ic_danger_outline);
        bld.setView(layoutV);
        
        bld.setButton(DialogInterface.BUTTON_POSITIVE, "Report", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int buttonType) {
                dialog.dismiss();
                LayoutInflater li = LayoutInflater.from(new ContextThemeWrapper(getApplicationContext(), R.style.App_Dark));
        		View layoutV2 = li.inflate(R.layout.crash_upload_screen, null);
        
                AlertDialog upd = new AlertDialog.Builder(CrashActivity.this).create();
        		upd.setView(layoutV2);
                upd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            		@Override
            		public void onCancel(DialogInterface dialog) {
                		kms();
            		}
        		});
                upd.show();
                uploadLog(layoutV2);
            }
        });
        bld.setButton(DialogInterface.BUTTON_NEGATIVE, "Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int buttonType) {
                kms();
            }
        });
        
        bld.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                kms();
            }
        });
        
		bld.show();
    }
    
    private void setupCrashDialogView(View v) {
        ((TextView)v.findViewById(R.id.crash_message)).setText("Developer is so bad at coding");
        
        ((TextView)v.findViewById(R.id.crash_comment_et_hint)).setText("Any comments?");
        
        ((TextView)v.findViewById(R.id.crash_overview)).setText("Caused by: " + getIntent().getStringExtra("exceptionName"));
        ((TextView)v.findViewById(R.id.crash_logs)).setText(getIntent().getStringExtra("exceptionStackTrace"));
        ((Button)v.findViewById(R.id.crash_view_logs)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                toggleLogsVisibility(v);
            }
        });
    }
    
    private void toggleLogsVisibility(View v) {
        if (isUserViewingLogs) {
            ((TextView)v.findViewById(R.id.crash_message)).setVisibility(View.VISIBLE);
            ((LinearLayout)v.findViewById(R.id.crash_comment_container)).setVisibility(View.VISIBLE);
            ((TextView)v.findViewById(R.id.crash_overview)).setVisibility(View.VISIBLE);
            ((Button)v.findViewById(R.id.crash_view_logs)).setText("View logs");
            
            ((TextView)v.findViewById(R.id.crash_logs)).setVisibility(View.GONE);
        } else {
        	((TextView)v.findViewById(R.id.crash_message)).setVisibility(View.GONE);
            ((LinearLayout)v.findViewById(R.id.crash_comment_container)).setVisibility(View.GONE);
            ((TextView)v.findViewById(R.id.crash_overview)).setVisibility(View.GONE);
            ((Button)v.findViewById(R.id.crash_view_logs)).setText("Hide logs");
            
        	((TextView)v.findViewById(R.id.crash_logs)).setVisibility(View.VISIBLE);
        }
        isUserViewingLogs = !isUserViewingLogs;
    }
    
    private void uploadLog(View v) {
        createLogOnLogsDir();
        Uri logFileUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", new File(logFilePath));
        StorageReference logFileSR = firebaseSR.child(currentDate + ".log");
        StorageMetadata logMetadata = new StorageMetadata.Builder()
        		.setContentType("text/plain")
                .setContentEncoding("utf-8")
                .build();
        
        logFileSR.putFile(logFileUri, logMetadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        	@Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ((ImageView)v.findViewById(R.id.crash_upload_icon)).setVisibility(View.GONE);
                ((TextView)v.findViewById(R.id.crash_upload_text)).setText("Uploaded log file successfully!\nExiting in 4s");
                
                delayer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        kms();
                    }
                }, 4000);
            }
         }).addOnFailureListener(new OnFailureListener() {
        	@Override
            public void onFailure(Exception e) {
                ((ImageView)v.findViewById(R.id.crash_upload_icon)).setVisibility(View.VISIBLE);
                ((ProgressBar)v.findViewById(R.id.crash_upload_pb)).setVisibility(View.GONE);
                ((TextView)v.findViewById(R.id.crash_upload_text)).setText("Upload failed!\nReason: " + e.getMessage() + "\nExiting in 4s");
                
                delayer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        kms();
                    }
                }, 4000);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
        	@Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double uploadProgress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                ((TextView)v.findViewById(R.id.crash_upload_text)).setText("Uploading log file (" + uploadProgress +"% / 100%)");
            }
        });
    }
    
    private String getDeviceInfo() {
        final String newLine = System.lineSeparator();
        StringBuilder devInfo = new StringBuilder();
        
        String codeName = "UNKNOWN";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Field[] fields = Build.VERSION_CODES.class.getFields();
			codeName = fields[Build.VERSION.SDK_INT + 1].getName();
        } else {
        	Field[] fields = Build.VERSION_CODES.class.getFields();
			for (Field field : fields) {
  			  try {
    				if (field.getInt(Build.VERSION_CODES.class) == Build.VERSION.SDK_INT) {
         			   codeName = field.getName();
                    }
    			} catch (IllegalAccessException e) {
        			e.printStackTrace();
                }
    		}
		}
        
        devInfo.append("Device Brand: " + Build.BRAND + newLine);
        devInfo.append("Device Model : " + Build.MODEL + newLine);
        devInfo.append("Device Manufacturer : " + Build.MANUFACTURER + newLine);
        devInfo.append("Device ABIS: " + TextUtils.join(",", Build.SUPPORTED_ABIS) + newLine);
        devInfo.append("Device Board: " + Build.BOARD + newLine);
        devInfo.append("OS Codename: " + codeName + newLine);
        devInfo.append("OS Release: " + Build.VERSION.RELEASE + newLine);
        devInfo.append("OS Version: " + Build.VERSION.RELEASE + newLine);
        devInfo.append("OS API Level: " + Build.VERSION.SDK_INT + newLine);
        devInfo.append("Application Version: " + BuildConfig.VERSION_NAME  + newLine);
        devInfo.append("Application Variant: " + BuildConfig.BUILD_TYPE + newLine);
        
        return devInfo.toString();
    }
    
    private String getCurrentDate() {
        String rDate = "NULL";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            try {
            	Calendar cal = Calendar.getInstance();
        		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    			String strDate = sdf.format(cal.getTime());
        		SimpleDateFormat sdf1 = new SimpleDateFormat();
        		sdf1.applyPattern("dd/MM/yyyy HH:mm:ss");
        		Date date = sdf1.parse(strDate);
        	    rDate = date.toString();
        	} catch (ParseException pe) {}
        } else {
        	return ZonedDateTime.now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replace("T", "_").replace(".", "-");
        }
        return rDate.trim();
    }
    
    private void createLogOnLogsDir() {
        String logsDir = Utils.FileUtils.getExternalFilesDirPaths(getApplicationContext(), Utils.FileUtils.LOGS_FOLDER);
        File logFile = new File(logsDir + "/" + currentDate + ".log");
        try {
            logFile.createNewFile();
            FileWriter openedLogFile = new FileWriter(logFile);
            openedLogFile.write(constructLog());
            openedLogFile.flush();
            openedLogFile.close();
            
            logFilePath = logFile.getAbsolutePath();
        } catch (IOException ioe) { Utils.LoggerUtils.logErr("ESManager/CrashActivity", ioe.toString());}
    }
    
    private String constructLog() {
        StringBuilder content = new StringBuilder();
        
        content.append(getDeviceInfo());
        content.append(newLine);
        content.append("Cause by: " + getIntent().getStringExtra("exceptionName"));
        content.append(newLine);
        content.append("Full log:" + getIntent().getStringExtra("exceptionStackTrace"));
        
        return content.toString();
    }
    
    private void kms() {
        //if (tmpFile != null) tmpFile.delete();
        finishAffinity();
        finishAndRemoveTask();
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
        Runtime.getRuntime().exit(0);
    }
}

