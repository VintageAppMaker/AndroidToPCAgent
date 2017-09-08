/*
 *   ����:   APKMainService Ŭ����  
 *   �ۼ���: �ڼ���(adsloader@naver.com)    
 *   �ۼ���: 2010.08.31
 *   ����  :  
 */
package com.psw;

import java.io.File;
import java.io.IOException;

import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;

import com.psw.util.Util;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;


// Main Function Thread
public class APKMainService extends Service{ 
                              
	public static final String MEESAGE_ACTION = "IPS.Com.IPSMainService.MESSAGE_ACTION";
	
	public static final int msgInstallAPK    = 0;
	public static final int msgMediaScanner  = 1;
	public static final int msgAppendMessage = 2;
	public static final int msgWIFIScan      = 3;
	public static final int msgImageDown     = 4;
	public static final int msgSckConnection = 5;
	
	// ���� �������� äũ��
	boolean mIsStart = false;
	
	// �ڵ鷯 ��ü 
	public Handler mHandler ;
	
	// ���� ��ü��
	public ListenServer svr;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void  onStart(Intent intent, int startId) {
		// ���� ������� �̷���!!!
		// ----------------------------------------------
		android.os.Debug.waitingForDebugger();
		super.onStart(intent, startId);
		
		if ( mIsStart ){ 
			return;
		}
		
		KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock keyLock = km.newKeyguardLock(KEYGUARD_SERVICE);
		
		// KeyLock ��ü�� �����Ѵ�.
		Util.SetKeyguardLockObject(keyLock);
		
		// ��� API�� ����ϱ� ���ؼ� Service ��ü�� �����Ѵ�.
		Util.SetServiceObject(this);
		
		mIsStart = true;

		
		// ���� ���� ����
		StartServer();
		
		
	}
	
	@Override
	public void onDestroy() {
	
	}
	
	public void StartServer()
	{
		new ListenServer();
	}
	
}

