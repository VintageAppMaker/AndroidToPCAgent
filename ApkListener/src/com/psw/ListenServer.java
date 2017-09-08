/*
	제목:   ListenServer
	목적:   클라이언트 소켓 연결을 기다린다.
	작성자: 박성완
	작성일: 기억안남....--;
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
	        System.out.println("서버소켓생성에 실패했습니다.");
	        System.exit(1);
	    }
	    
	}
	
	//  Listen 쓰레드 수행
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
