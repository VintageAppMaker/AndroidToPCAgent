/*
    ����: sendtoapk
    �ۼ���: �ڼ���(adsloader@naver.com)
    �ۼ���: 2011.05.26
    ����  : 1. APK���� �������� �޽��� ����
            2. adb shell�� �̿��Ͽ� PC���� ���� 
    
 */
#include <stdio.h>
#include <unistd.h>
#include <sys/socket.h> /* basic socket definitions */
#include <sys/types.h> /* basic system data types */
#include <sys/un.h> /* for Unix domain sockets */
#include <netinet/in.h> /* for Unix internet sockets */

#define PACKET_SIZE 1024
#define KEYWORD_SIZE 100

// Packet ���� 
typedef struct tagPacket{
    char keyword[KEYWORD_SIZE];
    char pData [PACKET_SIZE - KEYWORD_SIZE];
} Packet;

enum ERRORID{
    PARAMERROR =0,
    SOCEKTERROR,
    PARSINGERROR
};

// ���� ����
static void usage()
{
    char* usemessage = "=======================================\r\n"
                       "usage...\r\n"
                       "./cmd_apk [command] [data]\r\n"
                       "ex) cmd_apk app com.android.phone\r\n"
                       "    cmd_apk dev PSW\r\n"
                       "    cmd_apk debug PSW\r\n"
                       "\r\n"
                       "\r\n"
                       "[command]\r\n"
                       "   -app:  app infomation. [data] is package name\r\n"
                       "   -dev:  device information. [data] is always \"PSW\"\r\n"
                       "   -debug:check debuggable. [data] is always \"PSW\"\r\n"
                       "   -all  :display all insatlled apk. [data] is always \"PSW\"\r\n"
                       "   -sms  :display all sms data. [data] is always \"PSW\"\r\n"
                       "   -pic  :display all Picture. [data] is always \"PSW\"\r\n"
                       "   -mp3  :display all music. [data] is always \"PSW\"\r\n"
                       "   -video:display all Movie. [data] is always \"PSW\"\r\n"
                       "   -keyguard:keyguard enable/disable. [data] 'on', 'off' \r\n"
                       "\r\n"
                       "=======================================\r\n";

    printf(usemessage);
}

// Error �޽��� 
static char* GetMessage(int nId)
{
    char* szMessage [] = {
        "ERROR# Parameter wrong", 
        "ERROR# Socket", 
        "ERROR# Parsing" 
    };

    return szMessage[nId];
}

// Error ���ڿ� ��� -> �ᱹ PC �м�
void ErrMSG(char* msg)
{
    printf("%s", msg);
}

// ũ�⸸ŭ �б� 
static int RecvBySize(int fd, char* pData, int nSize)
{
    int nReadSize = 0;
    int nReserved = nSize;

    while(1){
        int nRead = recv(fd, pData + nReadSize, nReserved, 0);

        nReadSize += nRead;
        nReserved -= nRead;
        
        if(nReadSize == nSize) break;
        if(nRead     == -1   ) return nRead;
        if(nRead     == 0    ) break;
    }

    return nReadSize;
}

// ������ 
static void ReadWait(int fd)
{   
    char pSize[KEYWORD_SIZE];
    Packet p;
    
    int length = -1;
        
    if ((length = RecvBySize(fd, pSize, KEYWORD_SIZE)) <= 0){
        ErrMSG ( GetMessage(SOCEKTERROR) ) ;
        return; 
    }
    
    // ���� ũ�� ����
    length = atoi(pSize);
    if (length <= 0) return;
    
    char* pData = malloc(length);

    if ((length = RecvBySize(fd, pData, length)) <= 0){
        ErrMSG ( GetMessage(SOCEKTERROR) ) ;
        return; 
    }
    
    printf("\r\n___________result(size = %s)_____________\r\n", pSize);
    printf("%s\r\n", pData);

    free(pData);
}

// �ޱ� 
static void SendCommand(int fd, char* szKeyword, char* szData)
{
    Packet p;
    
    bzero(&p, sizeof(Packet));       
    strcpy(p.keyword, szKeyword);
    strcpy(p.pData, szData);
    
    send(fd, (char*)&p, PACKET_SIZE, 0);
}

// ��ɾ� ó�� 
static void CommandProgress(char* szKeyword, char* szData)
{
    int DEST_PORT = 12222;
    char* DEST_IP = "127.0.0.1";

    int sockfd;
    struct sockaddr_in dest_addr;  


    sockfd = socket(AF_INET, SOCK_STREAM, 0); 

    dest_addr.sin_family = AF_INET;        
    dest_addr.sin_port = htons(DEST_PORT); 
    dest_addr.sin_addr.s_addr = inet_addr(DEST_IP);
    bzero(&(dest_addr.sin_zero), 8);       
    
    if (connect(sockfd, (struct sockaddr *)&dest_addr, sizeof(struct sockaddr)) == -1){
        ErrMSG ( GetMessage(SOCEKTERROR) ) ;
        exit(0);
    }
    
    SendCommand(sockfd, szKeyword, szData);
    ReadWait(sockfd);
    
    close( sockfd );
}


// ���� �Լ�
int main( int argc, char **argv )
{
    if (argc < 3){
        usage();
        //ErrMSG (GetMessage(PARAMERROR));
        exit(0);
    }

    CommandProgress(argv[1], argv[2]); 
}


