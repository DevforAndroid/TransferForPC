package transfer.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import transfer.net.ReceiveThread;
import transfer.net.ServerThread;
import transfer.view.MainFrame;
import transfer.view.ReceiveDialog;

/**
 * �ļ����������
 * @author EsauLu
 *
 */
public class TransferManager {
	
	
	//�������߳�
	private ServerThread m_server_thread;
	private ServerThread.ReceiveOper m_receive_oper;
	
	//Ĭ�ϱ���·��
	private String m_default_save_path;
	
	//��־�ļ�
	private File m_log_file;
	private BufferedWriter m_log_os;
	
	public static final int port=6602;
	
	//�û�����
	private MainFrame m_frame;
	
	//�����ļ�·��
	private String m_properties_file;
	
	/**
	 * ����
	 */
	private static TransferManager instance=null;

	/**
	 * ���캯��
	 */
	private TransferManager() {
		// TODO Auto-generated constructor stub
		init();
//		m_frame.setVisible(true);
	}
	
	/**
	 * ��ȡTransferManagerʵ��
	 * @return ����һ��TransferManagerʵ��
	 */
	public static synchronized TransferManager getInstance() {
		if(instance==null){
			instance=new TransferManager();
		}
		return instance;
	}
	
	private void init() {
		// TODO Auto-generated method stub
		
		m_default_save_path="C:\\transfer";
		m_properties_file="setting.properties";
		
		Properties properties=loadProperties();
		
		//�ȴ��߳�
		//=========================================================================================
		m_server_thread=ServerThread.createServerThread(getCurrenIP(),port, properties.getProperty("file_path"),properties.getProperty("host_name"));
		m_receive_oper=new ServerThread.ReceiveOper() {
			@Override
			public void operate(ReceiveThread receive) {
				// TODO Auto-generated method stub
				new ReceiveDialog(m_frame, receive);
			}
		};
		m_server_thread.registerReceiveOper(m_receive_oper);
		m_server_thread.start();
		//=========================================================================================
		
		m_frame=new MainFrame(this);
		initLog();		
		createAP();		
	}
	
	/**
	 * �����ȵ�
	 */
	public void createAP(){
//		File file=new File("createAP.bat");
//		if(file.exists()){
//			return ;
//		}
//		new Thread(){
//			public void run() {
//				try{
//					
//					String ssid=TransferUtils.convert("MyWifiApDemo"+m_server_thread.getHostName());
//					String psd="12345678";
//					Runtime r=Runtime.getRuntime();
//					r.exec("netsh wlan stop hostednetwork");
//					r.exec("netsh wlan set hostednetwork mode=allow ssid="+ssid+" key="+psd);
//					r.exec("netsh wlan start hostednetwork");
//					
//				}catch(Exception e){
//					
//				}
//			};
//		}.start();
	}
	
	/**
	 * �ر��ȵ�
	 */
	public void stopAP(){
//		try{
//			Runtime r=Runtime.getRuntime();
//			r.exec("netsh wlan stop hostednetwork");			
//		}catch(Exception e){
//			
//		}
	}
	
	private void initLog() {
		// TODO Auto-generated method stub
		m_log_file=new File("log.txt");
		try {
			while(!m_log_file.exists()){
				m_log_file.createNewFile();
			}
			m_log_os=new BufferedWriter(new FileWriter(m_log_file,true));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		new Thread(){
			public void run() {
				String line=null;
				try {
					BufferedReader m_log_is=new BufferedReader(new InputStreamReader(new FileInputStream(m_log_file)));
					while((line=m_log_is.readLine())!=null){
						m_frame.addLog(line);
					}
					m_log_is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
				}
			};
		}.start();
		
	}
	
	/**
	 * ������־
	 * @param log
	 */
	public void saveLog(String log){
		try {
			m_log_os.newLine();
			m_log_os.write(log);
			m_log_os.flush();
			m_frame.addLog(log);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * �����־
	 */
	public void clearLog(){
		try {
			if(m_log_os!=null){
				m_log_os.close();
			}
			if(m_log_file.exists()){
				m_log_file.delete();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
		try {
			m_log_file=new File("log.txt");
			while(!m_log_file.exists()){
				m_log_file.createNewFile();
			}
			m_log_os=new BufferedWriter(new FileWriter(m_log_file,true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
	}
	
	/**
	 * ��ȡ����IP�б� 
	 * @return ���ر���IP�б� 
	 */
	 public static List<String> getLocalIPList() {
	        List<String> ipList = new ArrayList<String>();
	        try {
	            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
	            NetworkInterface networkInterface;
	            Enumeration<InetAddress> inetAddresses;
	            InetAddress inetAddress;
	            String ip;
	            while (networkInterfaces.hasMoreElements()) {
	                networkInterface = networkInterfaces.nextElement();
	                inetAddresses = networkInterface.getInetAddresses();
	                while (inetAddresses.hasMoreElements()) {
	                    inetAddress = inetAddresses.nextElement();
	                    if (inetAddress != null && inetAddress instanceof Inet4Address) { // IPV4
	                        ip = inetAddress.getHostAddress();
	                        ipList.add(ip);
	                    }
	                }
	            }
	        } catch (SocketException e) {
	            e.printStackTrace();
	        }
	        return ipList;
	 } 	 
	 
	 /**
	  * ���ķ������ȴ��߳�
	  * @param ip
	  * @param port
	  * @param filePath
	  */
	 public boolean changeServerThread(String ip,int port,String filePath,String name) {
		m_server_thread.close();
		m_server_thread=null;
		try {
			while(m_server_thread==null){
				m_server_thread = ServerThread.createServerThread(ip, port, filePath,name);
			}
			m_server_thread.registerReceiveOper(m_receive_oper);
			m_server_thread.start();
		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	 }
	 
	 /**
	  * Ĭ������
	  */
	 public Properties setDefaultProperty(){	 
		 Properties p=new Properties();	
		 try {
			  File file=new File(m_properties_file);
			  file.createNewFile();
			  FileOutputStream out=new FileOutputStream( file);
			  InetAddress ad=InetAddress.getLocalHost();
			  p.setProperty("file_path", m_default_save_path);
			  if(ad!=null){
				  p.setProperty("host_name", ad.getHostName());
			  }else{
			 	  p.setProperty("host_name", "null");
			  }
			  p.store(out, "Comment");
		 } catch (Exception e) {
		  	  // TODO Auto-generated catch block
		 }
		 return p;
	 }
	 
	 /**
	  * ���س�������
	  * @return
	  */
	 public Properties loadProperties(){		 
		 Properties p=new Properties();
		 try {
			 File file=new File(m_properties_file);
			 if(!file.exists()){
				 return setDefaultProperty();
			 }
			 FileInputStream in=new FileInputStream( file);
			 p.load(in);
		 } catch (Exception e) {
			// TODO Auto-generated catch block
		 }
		 return p;
	 }
	 
	 /**
	  * �����������
	  * @param property �����б�
	  */
	 public void saveProperties(HashMap<String , String > property){		 
		 Properties p=new Properties();		
		 for(String key:property.keySet()){
			 p.setProperty(key, property.get(key));
		 }
		 try {
			FileOutputStream out=new FileOutputStream(m_properties_file);
			p.store(out, "Comment");
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	 }
	 
	 /**
	  * ��ȡ��ǰIP
	  * @return IP
	  */
	 private String getCurrenIP() {
		// TODO Auto-generated method stub
		String[] ips= getIpArray(getLocalIPList());
		String curr_ip="0.0.0.0";
		
		if(ips!=null){
			curr_ip=ips[0];
		}else{
			try {
				InetAddress net=InetAddress.getLocalHost();
				if(net!=null){
					curr_ip=net.getHostAddress();
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
		
		return curr_ip;
	}
		
	/**
	* ��ip�б�ת��������
	* @param list ��List��¼��ip�б�
	* @return ������������ʽ��ip�б�
	*/
	public String[] getIpArray(List<String> list){
		if(list==null){
			return null;
		}
		String[] arr=new String[list.size()];
		int i=0,j=0;
		for(String s:list){
			arr[i++]=s;
			if(s.matches("192\\.168\\.10*.\\d{1,3}")){
				j=i-1;
			}
		}
		String tem=arr[0];
		arr[0]=arr[j];
		arr[j]=tem;
		return arr;
	}
	 
	/**
	 * ��ȡ��ǰIP
	 * @return ����IP�ַ���ֵ
	 */
	public String getIp() {
		return m_server_thread.getIP();
	}
	
	/**
	 * ��ȡ��ǰ���ŵĶ˿ں�
	 * @return �˿ں�
	 */
	public int getPort() {
		return m_server_thread.getPort();
	}
	
	/**
	 * �����ļ�����·��
	 * @return ·��
	 */
	public String getFileSavePath(){
		return m_server_thread.getFileSavaPath();
	}
	
	/**
	 * ����Ĭ���ļ�����·��
	 * @return Ĭ��·��
	 */
	public String getDefaultSavePath() {
		return m_default_save_path;
	}
	
	/**
	 * �����ļ�����·��
	 * @param path ·��
	 */
	public void setFileSavePath(String path){
		m_server_thread.setFileSavaPath(path);
	}
	
	/**
	 * ����������
	 * @return
	 */
	public String getHostName(){
		return m_server_thread.getHostName();
	}
	
	/**
	 * ����������
	 * @return
	 */
	public void setHostName(String name){
		m_server_thread.setHostName(name);
	}
	
	
	 
}





































