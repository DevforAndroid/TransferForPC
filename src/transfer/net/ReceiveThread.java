package transfer.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

/**
 * �����ļ��߳�
 * @author EsauLu
 *
 */
public class ReceiveThread extends TransferSocket implements Runnable{

    /**
     * �����׽���
     */
    private Socket mSocket;

    /**
     * ����������
     */
    private DataInputStream mDis;

    /**
     * ���������
     */
    private DataOutputStream mDos;

    /**
     * �ļ�����·��
     */
    private String mPath;

    /**
     * ·���ָ���
     */
    public final static String mSplit;

    //��̬��ʼ����
    static {
        //��ȡ��ǰ����·���ָ���
        Properties p=System.getProperties();
        mSplit=p.getProperty("file.separator");
    }

    /**
     * ���캯�������ݴ���������׽��ִ��������߳�
     * @param mSocket	�뷢�Ͷ����ӵ��׽���
     */
    public ReceiveThread(Socket mSocket ,String path ,String host) throws IOException{
        super();
        this.mHostName=host;
        this.mSocket = mSocket;
        this.mPath=path;
        this.mThread=new Thread(this);
        init();
    }

    /**
     * ��ʼ��
     */
    private void init() throws IOException{

        //������
        mDis=new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));
        mDos=new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));

        isEnd=false;	//��־����δ��ʼ����û�н���

        //��ʼ��������
        data=new byte[8192];
        len=0;

        //��ʼ���ļ�����
        mTransferLenght=0;
        mFileLenght=0;

        //��ȡ���Ͷ˵�ַ
        mIp=mSocket.getInetAddress().getHostAddress();
        
        //����������
        mDos.write(mHostName.getBytes("GBK"));
        mDos.flush();

        //��ȡ�ļ�����
        mFileLenght=mDis.readLong();

        //��ȡ�ļ���
        len=mDis.read(data);
        mFileName=new String(data, 0, len,"GBK");
        
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        File file=null;
        DataOutputStream fos=null;

        try{
            mDos.write(1);
            mDos.flush();

            //�����ļ����������
            file=createFile();
            fos=new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

            //������������ٶȵ��߳�
            new CountRate().start();

            //�����ļ�����
            while((len=mDis.read(data))!=-1){
                fos.write(data, 0, len);
                mTransferLenght+=len;
            }
            fos.flush();

        }catch(Exception e){
            isEnd=true;
            System.out.println("�ж�");
        }finally{
            try{
                if(mSocket!=null){
                    mSocket.close();
                }
                if(fos!=null){
                    fos.close();
                }
                isEnd=true;
                System.out.println("����");
                if(file!=null&&file.isFile()&&file.exists()&&mFileLenght!=mTransferLenght){            	
                    System.out.println("ɾ�� "+file.getAbsolutePath()+" "+file.delete());
                }
            }catch(IOException e){
            	
            }
        }

    }

    /**
     * ���ļ�����·���´���һ�����ظ��ļ������ļ�
     * @return
     */
    private File createFile(){    	
        
    	//���·���Ƿ����
        File file=new File(mPath);
        if(!file.exists()){
        	file.mkdir();
        }
    	
        //�����ļ�
        String sb=mPath+mSplit+mFileName;        
        file=new File(sb);
        
        int i=1;
        int index=sb.lastIndexOf(".");
        while(file.exists()){
            i++;
            if(index<0){
                file=new File(sb+"("+i+")");
            }else{
                file=new File(sb.substring(0, index)+"("+i+")"+sb.substring(index));
            }
        }
        try{
            file.createNewFile();
        }catch(Exception e){
        	
        }
        return file;
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
     * �����ļ�·��
     * @return
     */
    public String getPath() {
        return mPath;
    }

    /**
     * ���ؽ����ٶ�
     * @return
     */
    public long getReceiveRate() {
        return getRate();
    }

    /**
     * ���ڼ�������ٶȵ��߳�
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















































