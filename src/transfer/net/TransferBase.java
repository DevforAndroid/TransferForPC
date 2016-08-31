package transfer.net;

public class TransferBase {


    /**
     * ָ��ip
     */
    protected String mIp;

    /**
     * �˿ں�
     */
    protected int mPort;
    
    /**
     * ������
     */
    protected String mHostName;
    
    /**
     * �̶߳���
     */
    protected Thread mThread;

	public TransferBase() {
		// TODO Auto-generated constructor stub
	}
    
    /**
     * �����߳�
     */
    public void start() {
    	mThread.start();
	}
    
    /**
     * �߳��Ƿ�̬
     * @return
     */
    public boolean isAlive(){
    	return mThread.isAlive();
    }

    /**
     * ���ؽ��ն�IP
     * @return
     */
    public String getIP() {
        return mIp;
    }

    /**
     * ���ض˿ں�
     * @return
     */
    public int getPort() {
        return mPort;
    }
    
    /**
     * ����������
     * @param hostName ������
     */
    public void setHostName(String hostName) {
		this.mHostName = hostName;
	}
    
    
    /**
     * ����������
     * @return ������
     */
    public String getHostName() {
		return mHostName;
	}
	
}





































