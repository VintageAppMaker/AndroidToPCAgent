/*
 *   ����: Packet Ŭ����  
 *   �ۼ���: �ڼ���(adsloader@naver.com)    
 *   �ۼ���: 2010.06.07
 *   ����  : Packet ���� �� �⺻ Action ����
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
    ��Ʈ�� ������
    ********************************/
    public int Send(OutputStream os) throws IOException
    {
        // ���յ� ��Ŷ �޸� ������ �������� ����
        try {
        	
        	// ACK���� keyword���� ũ�⸦ �����Ѵ�. -> Send�� ���۸��ϱ� ���ؼ� Keyword�� ũ�� �����Ѵ�.
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
     ��Ʈ�� �ޱ� (TFileHeader�� ���߿� ó��!)
    ********************************/
    public int Recive(InputStream is) throws IOException
    {
        int READSIZE = PACKET_SIZE;
        int nRead = -1;
        try{
        	// ��Ŷ ����� ���� ���� ����̹Ƿ� ���۸� ����.
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
        
        // ����� �����Ѵ�.
        if (nRead == -1) return -1;
       
        
        return  0;
    }
    
}
