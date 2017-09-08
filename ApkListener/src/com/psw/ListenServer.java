/*
	����:   ListenServer
	����:   Ŭ���̾�Ʈ ���� ������ ��ٸ���.
	�ۼ���: �ڼ���
	�ۼ���: ���ȳ�....--;
*/
package com.psw;
import java.io.*;
import java.net.*;

import android.os.Handler;
import android.util.Log;

import android.os.Bundle;
import android.os.Message;

public class ListenServer extends Thread
{
	ServerSocket serverSocket = null;
	public static final int DEFAULT_PORT = 12222;
	
	FileInputStream      fis;
	Handler m_Handler;
	
	public ListenServer() 
	{
	    try {
	    	serverSocket  = new ServerSocket(DEFAULT_PORT);
	        start();
		    Log.d("IPS", "Server Thread Start.");
	    } catch (IOException ie) {
	        System.out.println(ie.getMessage());
	        System.out.println("�������ϻ����� �����߽��ϴ�.");
	        System.exit(1);
	    }
	    
	}
	
	//  Listen ������ ����
	public void run(){
		while (true) {
	        try {
	            Socket clientSocket = serverSocket.accept();
	            
	            
	            new ServerThread(clientSocket);
	        } catch (IOException ie) {
	            break;
	        }
	    }
	   
	} 
} 
