package transfer.net;

public class TransferSocket extends TransferBase{

    /**
     * �ļ���
     */
    protected String mFileName;

    /**
     * �ѷ��ͳ���
     */
    protected long mTransferLenght;

    /**
     * �ļ��ܳ���
     */
    protected long mFileLenght;

    /**
     * �����ٶ�
     */
    protected long mCurrRate;

    /**
     * �����Ƿ�����ı�־��������ɻ��߱����ն˾ܾ������ڷ��ͽ���
     */
    protected boolean isEnd;

    /**
     * ������
     */
    protected byte[] data;
    protected int len;
	
	public TransferSocket() {
		// TODO Auto-generated constructor stub
	}    
	
	/**
     * �����ļ�����
     * @return
     */
    public long getFileLenght() {
        return mFileLenght;
    }
    

    /**
     * ���ص�ǰ�����Ƿ����
     * @return
     */
    public boolean isEnd() {
    	if(isEnd&&mCurrRate==0){
    		return true;
    	}
        return false;
    }

    /**
     * �����ļ���
     * @return
     */
    public String getFileName() {
        return mFileName;
    }

    /**
     * ����һ��0~1֮��ĸ�����,��ʾ���ͽ���
     * @return
     */
    public double getProgress(){
        if(mFileLenght==0){
            return 0.0;
        }
        return (double)mTransferLenght/(double)mFileLenght;
    }

    /**
     * ���ط����ٶ�
     * @return
     */
    public long getRate() {
        return mCurrRate;
    }
    
}
