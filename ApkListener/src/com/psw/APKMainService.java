/*
 *   제목:   APKMainService 클래스  
 *   작성자: 박성완(adsloader@naver.com)    
 *   작성일: 2010.08.31
 *   목적  :  
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
	
	// 최초 실행인지 채크용
	boolean mIsStart = false;
	
	// 핸들러 개체 
	public Handler mHandler ;
	
	// 서버 개체들
	public ListenServer svr;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void  onStart(Intent intent, int startId) {
		// 서비스 디버깅은 이렇게!!!
		// ----------------------------------------------
		android.os.Debug.waitingForDebugger();
		super.onStart(intent, startId);
		
		if ( mIsStart ){ 
			return;
		}
		
		KeyguardManager km = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
		KeyguardManager.KeyguardLock keyLock = km.newKeyguardLock(KEYGUARD_SERVICE);
		
		// KeyLock 개체를 세팅한다.
		Util.SetKeyguardLockObject(keyLock);
		
		// 몇몇 API를 사용하기 위해서 Service 개체를 저장한다.
		Util.SetServiceObject(this);
		
		mIsStart = true;

		
		// 각종 서버 실행
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

