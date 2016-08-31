package transfer.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * �����ļ��߳�
 * @author EsauLu
 *
 */
public class SendThread extends TransferSocket implements Runnable {

    /**
     * �����׽���
     */
    private Socket mSocket;

    /**
     * ������
     */
    private DataInputStream mDis;	//���������
    private DataOutputStream mDos;	//���͵�����
    private DataInputStream mFis;	//��ȡ�ļ�

    /**
     * Ҫ���͵��ļ�
     */
    private File mFile;

    /**
     *
     * @param iP
     * @param port
     */
    public SendThread(String iP, int port, File file) throws NoSuchFieldException{
        super();
        this.mIp = iP;
        this.mPort = port;
        this.mFile=file;
        if(!file.exists()){
            throw new NoSuchFieldException("�ļ�������");
        }
        init();
    }

    private void init(){
        mCurrRate=0;
        mTransferLenght=0;
        mFileLenght=mFile.length();
        mFileName=mFile.getName();
        isEnd=false;
        mThread=new Thread(this);

        //��ʼ��������
        data=new byte[8192];
        len=0;
        
    }

    private void initRun() throws IOException{

        //�������ӣ���ʱ10������ʧ��
        mSocket=new Socket();
        mSocket.connect(new InetSocketAddress(mIp, mPort), 5000);

        //��ȡ������
        mDis=new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));
        mDos=new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));
        mFis=new DataInputStream(new BufferedInputStream(new FileInputStream(mFile)));
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        try{

            initRun();
            
            //��ȡ������
            len=mDis.read(data);
            mHostName=new String(data, 0, len,"GBK");
            
            mDos.writeLong(mFileLenght);
            mDos.flush();
            mDos.write(mFileName.getBytes("GBK"));
            mDos.flush();
            mDis.read();

            //���㷢���ٶ�
            new CountRate().start();
            
            //�����ļ�����
            len=0;
            while((len=mFis.read(data))!=-1){
                mDos.write(data, 0, len);
                mTransferLenght+=len;//��¼�ѷ��͵ĳ���
            }
            mDos.flush();

        }catch(IOException e){
            isEnd=true;
            mCurrRate=0;
        }finally{
            try{
                if(mSocket!=null){
                    mSocket.close();
                }
                if(mFis!=null){
                    mFis.close();
                }
                isEnd=true;
            }catch(IOException e){
            	
            }
        }

    }

    /**
     * ȡ�������ļ�
     */
    public void close(){
        try{
            this.mSocket.close();
        }catch(IOException e){
        }
    }

    /**
     * ���ط����ٶ�
     * @return
     */
    public long getSendRate() {
        return getRate();
    }

    /**
     * ���ڼ��㷢���ٶȵ��߳�
     * @author EsauLu
     *
     */
    private class CountRate extends Thread{

        public CountRate() {
            // TODO Auto-generated constructor stub
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();

            long preTime;
            long currTime;
            long timeDiff;
            long preLenght;
            long currLenght;

            preLenght=mTransferLenght;
            preTime=System.currentTimeMillis();
            while(mTransferLenght<mFileLenght&&!isEnd){
                currTime=System.currentTimeMillis();
                currLenght=mTransferLenght;
                timeDiff=currTime-preTime;
                if(timeDiff!=0){
                    mCurrRate=(long)((double)(mTransferLenght-preLenght)*1000.0/(double)timeDiff);
                }else{
                    mCurrRate=0;
                }
                preLenght=currLenght;
                preTime=currTime;
                try{
                    Thread.sleep(500);
                }catch(Exception e){                	
                }
            }
            mCurrRate=0;

        }

    }

}







































