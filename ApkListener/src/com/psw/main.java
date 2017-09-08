package com.psw;

import java.net.Socket;

import com.psw.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Intent mServiceIntent = new Intent(); 
		mServiceIntent.setAction("com.psw.IPSMainService"); 
		startService(mServiceIntent);
        
    }
      	
}