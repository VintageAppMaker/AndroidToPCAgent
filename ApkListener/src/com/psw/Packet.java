/*
 *   제목: Packet 클래스  
 *   작성자: 박성완(adsloader@naver.com)    
 *   작성일: 2010.06.07
 *   목적  : Packet 정의 및 기본 Action 수행
 */


package com.psw;

import java.io.*;
import java.net.*;

import android.util.Log;

public class Packet 
{
	static int  PACKET_SIZE   = 1024;
	static int  KEY_SIZE      = 100;
	static int  DATA_SIZE     = PACKET_SIZE - KEY_SIZE;
	
    
    public byte[] m_pRcvBinary = new byte[PACKET_SIZE];
    public byte[] m_pSendData  = null;
    
    public String  m_keyword = "";
    public String  m_data    = "";
    
    /*******************************
    스트림 보내기
    ********************************/
    public int Send(OutputStream os) throws IOException
    {
        // 조합된 패킷 메모리 영역을 소켓으로 전송
        try {
        	
        	// ACK값의 keyword에는 크기를 지정한다. -> Send는 버퍼링하기 위해서 Keyword에 크기 지정한다.
        	int nSendSize = KEY_SIZE + m_data.getBytes("euc-kr").length ;
        	m_keyword = Integer.toString( nSendSize - KEY_SIZE );
        	
        	m_pSendData = new byte[ nSendSize ];
        	
        	System.arraycopy(m_keyword.getBytes(), 0, m_pSendData,    0,  m_keyword.length());
        	System.arraycopy(m_data.getBytes("euc-kr"), 0, m_pSendData,    KEY_SIZE,  m_data.getBytes("euc-kr").length);
        	
        	os.write(m_pSendData, 0, nSendSize);
            os.flush();
            return 0;
        } catch (SocketException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
   
    /*******************************
     스트림 받기 (TFileHeader는 나중에 처리!)
    ********************************/
    public int Recive(InputStream is) throws IOException
    {
        int READSIZE = PACKET_SIZE;
        int nRead = -1;
        try{
        	// 패킷 사이즈가 적고 내부 통신이므로 버퍼링 안함.
        	nRead = is.read(m_pRcvBinary, 0, PACKET_SIZE);
        	
        	byte [] pKeyword = new byte[KEY_SIZE];
        	byte [] pdata    = new byte[DATA_SIZE];
        	
        	System.arraycopy(m_pRcvBinary, 0,   pKeyword, 0, KEY_SIZE);
        	System.arraycopy(m_pRcvBinary, KEY_SIZE, pdata,    0, DATA_SIZE);
        	
        	m_keyword = new String(pKeyword, "euc-kr").trim();
        	m_data    = new String(pdata, "euc-kr").trim();
        	
            
        } catch(java.net.SocketException e ){
            e.printStackTrace();
        	return -1;
        }     
        
        // 종료시 리턴한다.
        if (nRead == -1) return -1;
       
        
        return  0;
    }
    
}
