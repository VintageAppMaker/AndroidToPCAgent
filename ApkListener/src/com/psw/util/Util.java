/*
 *   ����:  ����ƽ�Լ� ���� ��ƿ��Ƽ  
 *   �ۼ���: �ڼ���(adsloader@naver.com)    
 *   �ۼ���: 2010.06.10
 *   ����  : ����ƽ�Լ� ����
 */
package com.psw.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;


import android.app.KeyguardManager;
import android.app.Service;
import android.util.Log;


public  class Util 
{
	
	private static Service sMain = null;
	private static KeyguardManager.KeyguardLock keyLock  = null;
	
	// Service ��ü�� �����ص��ڱ���... ��� API ����� ����
	static public void SetServiceObject(Service s)
	{
	    Util.sMain = s;	
	}
	
	// Service ��ü�� �Ѱ��ش�.
	static public Service GetServiceObject()
	{
	    return Util.sMain;	
	}
	
	// KeyguardLock ��ü�� �����ص��ڱ���... ��� API ����� ����
	static public void SetKeyguardLockObject(KeyguardManager.KeyguardLock s)
	{
	    Util.keyLock = s;	
	}
	
	// KeyguardLock ��ü�� �Ѱ��ش�.
	static public KeyguardManager.KeyguardLock GetKeyguardLockObject()
	{
	    return Util.keyLock;	
	}
	
	static public void SaveFile(String msg){
		
		String fName = "/sdcard/AA.txt";
        File file = new File(fName);
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(file, "rw");
			f.write(msg.getBytes() );
			f.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	// prop ��������.
	static public String getProp(String key)
	{
		boolean b = false;
		
		Runtime runtime = Runtime.getRuntime();
        
		try{
            Process p = runtime.exec("getprop");
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            
            while ((line = br.readLine())!=null){
                if ( line.indexOf(key) == 0) 
                	return line.replace(key, "");
            }
            br.close();
         
        }catch(Exception e){
            Log.d("Test",e.toString());
        }
        
        return "";
	}
	
	public final static int ID_SOFTVERSION = 0;
	public final static int ID_WIFIMAC     = 1;
	public final static int ID_BTMAC       = 2;
	public final static int ID_SERIAL      = 3;
	
	public static String INFO_FILE[] = {
		"/upgrade/version",
		"/upgrade/.btmac",
		"/upgrade/.wifimac",
		"/upgrade/.serialno"
		
	};
	
	// ������������
	static public String GetSettingInfo(int nIndx)
	{   
	    String strVerSion = "-1";
	    
	    File f = new File(INFO_FILE[nIndx]);
        if(f != null){
        	if(f.canRead()){
        	    try{
        	    	
        	    	 FileReader fileReader = new FileReader(f);
                     BufferedReader bReader = new BufferedReader(fileReader);
                     try {
                    	 strVerSion = bReader.readLine();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
        	    	
        	    } catch(FileNotFoundException e){
        	    	
        	    }
        	}
        }
	    
	    return 	strVerSion;
	}
	
	
	
	
}
