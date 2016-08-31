package transfer.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * �����ļ����͵ķ�����߳�
 * @author EsauLu
 *
 */
public class ServerThread extends TransferBase implements Runnable {

    /**
     * ������׽���
     */
    private ServerSocket mServerSocket;

    /**
     * �ļ�����·��
     */
    private String mFileSavaPath;

    /**
     * ���ڱ�ֻ֤��һ��ServerThreadʵ��������
     */
    private static ServerThread mServerThread;

    /**
     * �յ���������ʱ�û��Ĳ����ӿ�
     */
    public interface ReceiveOper {
        public void operate(ReceiveThread receive);
    }
    private ReceiveOper mReceiveOper;   //�ӿ�ʵ��

    /**
     * ���캯��
     * @param mPort	�˿ں�
     */
    private ServerThread(String ip,int mPort,String path) {
        // TODO Auto-generated constructor stub
        this.mIp=ip;
        this.mPort=mPort;
        this.mFileSavaPath=path;
        this.mThread=new Thread(this);
    	this.mHostName="";
        init();
    }

    /**
     * ���캯��
     * @param ip IP
     * @param mPort �˿ں�
     * @param path �ļ�����·��
     * @param hostName ������
     */
    private ServerThread(String ip,int mPort,String path ,String hostName) {
        // TODO Auto-generated constructor stub
    	this.mHostName=hostName;
        this.mIp=ip;
        this.mPort=mPort;
        this.mFileSavaPath=path;
        this.mThread=new Thread(this);
        init();
    }

    /**
     * ���캯��
     * @param mPort	�˿ں�
     */
    private ServerThread(int mPort,String path) {
        // TODO Auto-generated constructor stub
        this.mIp=null;
        this.mPort=mPort;
        this.mFileSavaPath=path;
        this.mThread=new Thread(this);
    	this.mHostName="";
        init();
    }

    private void init(){
    	
    	//����������
//    	mHostName="��������";
    	if(mHostName==null||mHostName.equals("")){
    		try {
				InetAddress ad=InetAddress.getLocalHost();
				mHostName=ad.getHostName();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				mHostName=mThread.getId()+""+(Math.random()*1000)%255;
			}
    	}
    	
        //Ĭ�ϵ��յ����������ʵ�ֽӿڣ��������������շ��͹������ļ�
        mReceiveOper=new ReceiveOper() {
            @Override
            public void operate(ReceiveThread receive) {
                receive.start();
            }
        };
    }

    /**
     * ����һ��ServerThreadʵ��
     * @param ip    ָ��ip
     * @param port  �˿�
     * @param path  �ļ�����·��
     * @return
     */
    public static synchronized ServerThread createServerThread(String ip,int port,String path){
        if(mServerThread!=null){
        	mServerThread.close();
        	mServerThread=null;
        }
        mServerThread=new ServerThread(ip,port,path);
        return mServerThread;
    }

    /**
     * ����һ��ServerThreadʵ��
     * @param ip    ָ��ip
     * @param port  �˿�
     * @param path  �ļ�����·��
     * @return
     */
    public static synchronized ServerThread createServerThread(String ip,int port,String path ,String hostName){
        if(mServerThread!=null){
        	mServerThread.close();
        	mServerThread=null;
        }
        mServerThread=new ServerThread(ip,port,path,hostName);
        return mServerThread;
    }

    /**
     * ����һ��ServerThreadʵ��
     * @param port  �˿�
     * @param path  ·��
     * @return
     */
    public static synchronized ServerThread createServerThread(int port,String path){
        if(mServerThread!=null){
        	mServerThread.close();
        	mServerThread=null;
        }
        mServerThread=new ServerThread(port,path);
        return mServerThread;
    }

    /**
     * ע����յ����������Ĳ����ӿ�
     * @param mReceiveOper
     */
    public void registerReceiveOper(ReceiveOper mReceiveOper){

        this.mReceiveOper=mReceiveOper;

    }

    @Override
    public void run(){
        // TODO Auto-generated method stub
        try{
            while (true) {
				try {
					if (mIp == null) {
						mServerSocket = new ServerSocket(mPort);
						mIp=mServerSocket.getLocalSocketAddress().toString();	
					} else {
						mServerSocket = new ServerSocket(mPort, 0,
								InetAddress.getByName(mIp));       
					}
					break;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					mPort++;
				}
			}
			while(true){
                try {
                    Socket sc=mServerSocket.accept();//�ȴ�����
					mReceiveOper.operate(new ReceiveThread(sc, mFileSavaPath,getHostName()));   //�յ��������Ӻ󣬴��������
				} catch (Exception e) {
					// TODO Auto-generated catch block
				}
            }
            
        }catch(Exception e){
        	mServerThread=null;
        }finally{
            try{
                if(mServerSocket!=null)
                    mServerSocket.close();
            }catch(IOException e){
            	
            }
        	mServerThread=null;
        }
    }

    /**
     * ���ص�ǰ�ļ�����·��
     * @return
     */
    public String getFileSavaPath() {
        return mFileSavaPath;
    }

    /**
     * �����ļ�����λ��
     * @param FileSavaPath
     */
    public void setFileSavaPath(String FileSavaPath) {
        this.mFileSavaPath = FileSavaPath;
    }
    
    /**
     * �ж�
     */
    public void interrupt(){
    	mThread.interrupt();
    }
    
    /**
     * �Ƿ��ж�
     * @return
     */
    public boolean isInterrupted(){
    	return mThread.isInterrupted();
    }
    
    public boolean close() {
		// TODO Auto-generated method stub
    	try {
			if(mServerSocket!=null){
				mServerSocket.close();
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
    	return false;
	}
    
}












































