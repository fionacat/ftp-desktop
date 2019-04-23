import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.synth.SynthSeparatorUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoManager;
public class Frame implements ActionListener{
	static JFrame window;
	JPanel topPanel;
	JPanel middlePanel;
	JPanel buttomPanel;
	JPanel remoteTreePanel;
	JPanel treePanel;
	JPanel statePanel;
	
	JScrollPane treeView;
	JScrollPane scrollpane;
	
	JTree localTree;
	JTree remoteTree =null;
	DefaultMutableTreeNode remoteRoot;
	
	JTextArea infomation;
	JTextField url;
	JTextField loginName;
	JTextField passWord;
	JTextField post;
	
	StringBuffer time = new StringBuffer("");
	String currentUrl; 
	
	JPopupMenu localFilePopup;
	JPopupMenu remoteFilePopup;
	JPopupMenu transPopup;
	JPopupMenu successPopup;
	JPopupMenu failPopup;
	
	static Socket commendSocket = null;
	static PrintWriter pw;
	URL remoteUrl;
	String remoteFile;
	int ID;
	
	JComboBox<String> localPath;
	JComboBox<String> remotePath;
	
	int lastRemotePath = 1;
	int remotefocusedRowIndex = -1;
	int localfocusedRowIndex = -1;
	int transfocusedRowIndex = -1;
	int successfocusedRowIndex = -1;
	int failfocusedRowIndex = -1;
	int mulucount = -1;
	int lastRemoteTreeRow = 0;
	
	Vector<Vector<String>> localData;
	Vector<Vector<String>> remoteData;
	Vector<Vector<String>> transData;
	Vector<Vector<String>> successData;
	Vector<Vector<String>> failData;
	
	MyTable localTable;
	MyTable remoteTable;
	MyTable transtable;
	MyTable successtable;
	MyTable failtable;
	
	double queueFileLength = 0;
	double lengthHaveRead =0;
	
	//��������Ӧ����
	class listenTask extends SwingWorker<Void,Void>{
        @Override//ͳ�Ƴ��ִ���
        protected Void doInBackground() throws Exception {
            // TODO Auto-generated method stub
        	InputStream is;
    		try {
    			is = commendSocket.getInputStream();
    			BufferedReader br = new BufferedReader(new InputStreamReader(is));
    			String info = null;
    			while(((info=br.readLine())!=null)) {//ѭ����ȡ�ͻ�����Ϣ
    				infomation.append(info+"\r\n");
    				infomation.setCaretPosition(infomation.getText().length());  
    				if(info.substring(0,3).equals("227"))
    				{	
    					ID = getport(info);
    				}
    			}
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            return null;
        }
        public void done() {
        }
      }
	//�������
	class handleTask extends SwingWorker<Void,Void>{
        @Override
        protected Void doInBackground() throws Exception {
            // TODO Auto-generated method stub
        	//ͳ�ƶ�����������Ҫ������ļ���С
        	for(int i=0;i<transtable.getRowCount();i++) {
        		queueFileLength = queueFileLength+sizeToLong(transtable.getValueAt(i, 3).toString());
        	}
        	//���ݴ�����еĵڶ��������жϲ������ϴ���������
        	for(int i=0;i<transtable.getRowCount();i++) {
        		if(transtable.getValueAt(i, 1).equals("<---")) {
        			//����
        			remoteFile = transtable.getValueAt(i, 2).toString();
    	        	downloadFile(localPath.getSelectedItem().toString(),remoteFile);
    	        	successData.addElement(transData.get(i));
    	        	successtable.updateUI();
        		} else {
        			//�ϴ�
        			File localFile = new File(transtable.getValueAt(i, 0).toString());
        			uploadFile(localFile);
    	        	successData.addElement(transData.get(i));
    	        	successtable.updateUI();
        		}
        	}
        	return null;
        }
        public void done() {
        	//ÿ�δ�������󣬸������ݺʹ����б������´δ���
        	queueFileLength = 0;
        	lengthHaveRead =0;
        	transData.removeAllElements();
    		transtable.updateUI();
        }
      }
	//��½ 
	public void login(){
		String link  = url.getText();
		String name = loginName.getText();
		String password = passWord.getText();
		String postID = post.getText();
//		String link = "ftp://mirrors.ustc.edu.cn/";
//		String name = "anonymous";
//		String password = "Email";

		remoteRoot = new DefaultMutableTreeNode("Զ�̷�����");
		remoteTree = new JTree(remoteRoot);
		//��½
		try {
			remoteUrl = new URL(link);
			commendSocket = new Socket(remoteUrl.getHost(),21);
			OutputStream os = commendSocket.getOutputStream();
			pw = new PrintWriter(os);
			if(name!=null){
				pw.write("USER "+name+"\r\n");
				pw.flush();
			}
			if(password!=null){
				pw.write("PASS "+password+"\r\n");
				pw.flush();
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		remoteTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {
				TreePath[] paths = evt.getPaths();
				
				for(int i=0;i<paths.length;i++) {
					if(evt.isAddedPath(i)) {
				
						String node = paths[i].getLastPathComponent().toString();
						
						if(!node.equals("Զ�̷�����")) {
							
							if(paths[i].getPathCount()-lastRemotePath!=1) {
								if(paths[i].getPathCount()-lastRemotePath==0)
									pw.write("CWD "+"../"+paths[i].getLastPathComponent()+"\r\n");
								else if(paths[i].getPathCount()-lastRemotePath==-1)
									pw.write("CWD "+"../../"+paths[i].getLastPathComponent()+"\r\n");
								else if(paths[i].getPathCount()-lastRemotePath==-2)
									pw.write("CWD "+"../../../"+paths[i].getLastPathComponent()+"\r\n");
								else if(paths[i].getPathCount()-lastRemotePath==-3)
									pw.write("CWD "+"../../../../"+paths[i].getLastPathComponent()+"\r\n");
								else if(paths[i].getPathCount()-lastRemotePath==-4)
									pw.write("CWD "+"../../../../../"+paths[i].getLastPathComponent()+"\r\n");
								else if(paths[i].getPathCount()-lastRemotePath==-5)
									pw.write("CWD "+"../../../../../../"+paths[i].getLastPathComponent()+"\r\n");
								else if(paths[i].getPathCount()-lastRemotePath==-6)
									pw.write("CWD "+"../../../../../../../"+paths[i].getLastPathComponent()+"\r\n");
								else if(paths[i].getPathCount()-lastRemotePath==-7)
									pw.write("CWD "+"../../../../../../../../"+paths[i].getLastPathComponent()+"\r\n");
								else if(paths[i].getPathCount()-lastRemotePath==-8)
									pw.write("CWD "+"../../../../../../../../../"+paths[i].getLastPathComponent()+"\r\n");
							
								pw.flush();
							} else {
								pw.write("CWD "+paths[i].getLastPathComponent()+"\r\n");
								pw.flush();
							}
					
							getDir2((DefaultMutableTreeNode) paths[i].getLastPathComponent());
						}
					lastRemotePath = paths[i].getPathCount();
					}
				}
			}
		});
		treeView = new JScrollPane(remoteTree);
		remoteTreePanel.add(treeView);
	}
	//��ť����
	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		String cmd = e.getActionCommand();
		if(cmd.equals("download")) {
			if(remoteTable.getValueAt(remotefocusedRowIndex,1).toString().equals("�ļ�")) {
				if(localPath.getItemCount()!=0&&localPath.getSelectedItem().toString().length()>0) {
					Vector<String> transferinfo = new Vector<String>();
					
					transferinfo.add(localPath.getSelectedItem().toString());
					transferinfo.add("<---");
					transferinfo.add(remoteTable.getValueAt(remotefocusedRowIndex,6).toString());
					transferinfo.add(remoteTable.getValueAt(remotefocusedRowIndex,4).toString());
					transferinfo.add(remoteTable.getValueAt(remotefocusedRowIndex,5).toString());
					
					transData.add(transferinfo);
					transtable.updateUI();
					statePanel.removeAll();
					statePanel.updateUI();
					
					
					ProgressBarDemo pbd  = new ProgressBarDemo();
					statePanel.add(pbd);
					statePanel.updateUI();
					new handleTask().execute();
			       	pbd.run();
			          	
	        	} else {
	        		JOptionPane.showMessageDialog(window, "<html><body><font face = '΢���ź�'>��ѡ��һ��������ļ��ı���Ŀ¼��<body/><html/>","��ʾ", JOptionPane.INFORMATION_MESSAGE);

	        	}
		    
			} else {	  
	       		JOptionPane.showMessageDialog(window, "<html><body><font face = '΢���ź�'>��ѡ��һ���ļ���<body/><html/>","��ʾ", JOptionPane.INFORMATION_MESSAGE);
	       }
		} else  if(cmd.equals("upload")) {
			if(localTable.getValueAt(localfocusedRowIndex,1).toString().equals("�ļ�")) {
				
				Vector<String> transferinfo = new Vector<String>();
				transferinfo.add(localPath.getSelectedItem().toString());
				transferinfo.add("--->");
				transferinfo.add("");
				transferinfo.add(localTable.getValueAt(localfocusedRowIndex,2).toString());
				transferinfo.add(localTable.getValueAt(localfocusedRowIndex,3).toString());
					
				transData.add(transferinfo);
				transtable.updateUI();
				statePanel.removeAll();
				statePanel.updateUI();
				
				ProgressBarDemo pbd  = new ProgressBarDemo();
				statePanel.add(pbd);
				statePanel.updateUI();
					
				new handleTask().execute();
			   	pbd.run();	
				
			} else {	  
	       		JOptionPane.showMessageDialog(window, "<html><body><font face = '΢���ź�'>��ѡ��һ���ϴ��ı����ļ���<body/><html/>","��ʾ", JOptionPane.INFORMATION_MESSAGE);
	        	
	        }
		} else if(cmd.equals("r_add to the queue")) {
			if(remotefocusedRowIndex!=-1&&remoteTable.getValueAt(remotefocusedRowIndex,1).toString().equals("�ļ�")) {
				if(localPath.getItemCount()!=0&&localPath.getSelectedItem().toString().length()>0) {
					
					Vector<String> transferinfo = new Vector<String>();
					transferinfo.add(localPath.getSelectedItem().toString());
					transferinfo.add("<---");
					transferinfo.add(remoteTable.getValueAt(remotefocusedRowIndex,6).toString());
					transferinfo.add(remoteTable.getValueAt(remotefocusedRowIndex,4).toString());
					transferinfo.add(remoteTable.getValueAt(remotefocusedRowIndex,5).toString());
					transData.add(transferinfo);
					transtable.updateUI();
				} else {
					JOptionPane.showMessageDialog(window, "<html><body><font face = '΢���ź�'>��ѡ��һ��������ļ��ı���Ŀ¼��<body/><html/>","��ʾ", JOptionPane.INFORMATION_MESSAGE);
				}
				
			} else {
				JOptionPane.showMessageDialog(window, "<html><body><font face = '΢���ź�'>��ѡ��һ���ļ���<body/><html/>","��ʾ", JOptionPane.INFORMATION_MESSAGE);
				
	        }

		} else if(cmd.equals("l_add to the queue")) {
			if(localTable.getValueAt(localfocusedRowIndex,1).toString().equals("�ļ�")) {
				Vector<String> transferinfo = new Vector<String>();
				
				transferinfo.add(localPath.getSelectedItem().toString());
				transferinfo.add("--->");
				transferinfo.add("");
				transferinfo.add(localTable.getValueAt(localfocusedRowIndex,2).toString());
				transferinfo.add(localTable.getValueAt(localfocusedRowIndex,3).toString());
				
				transData.add(transferinfo);
				transtable.updateUI();
		        	
				
	        } else {
        		JOptionPane.showMessageDialog(window, "<html><body><font face = '΢���ź�'>��ѡ��һ�����ϴ��ı����ļ���<body/><html/>","��ʾ", JOptionPane.INFORMATION_MESSAGE);
	        }
		} else if(cmd.equals("login")) {
			if(pw!=null){
				int yesOrNo = JOptionPane.showConfirmDialog(window,"<html><body><font face = '΢���ź�'>�Ƿ�Ͽ���ǰ����,��ʼ�µ�����<body/><html/>","ѯ��",JOptionPane.YES_NO_OPTION);
				if(yesOrNo==0) {
					pw.write("QUIT \r\n");
					pw.flush();
					login();
					new listenTask().execute();
					getDir2(remoteRoot);
					remoteTree.expandRow(0);
					remoteTree.updateUI();
	        	}
			} else {
				login();
				new listenTask().execute();
				getDir2(remoteRoot);
				remoteTree.expandRow(0);
				remoteTree.updateUI();
				System.out.println("hhah");
	        }
		} else if(cmd.equals("cut")){
			if(pw!=null) {
				pw.write("QUIT \r\n");
				pw.flush();
				if(url.getText()!=null||!url.getText().toString().equals(""))
					url.setText("");
		        if(loginName.getText()!=null||!url.getText().toString().equals(""))
		        	loginName.setText("");
		       	if(passWord.getText()!=null||!url.getText().toString().equals(""))
		       		passWord.setText("");
		        if(post.getText()!=null||!url.getText().toString().equals(""))
		        	post.setText("");
		        pw=null;
	        }
		} else if(cmd.equals("in_to_local_dir")) {
	        
			File file = new File(localPath.getSelectedItem().toString());
			if(file.isDirectory()){
				setLocalTree(file);
	        }
		} else if(cmd.equals("r_in_to_remote_dir")) {
	        	
		} else if(cmd.equals("handle_queue")) {
			statePanel.removeAll();
			statePanel.updateUI();
			    	 
			ProgressBarDemo pbd  = new ProgressBarDemo();
			statePanel.add(pbd);
			statePanel.updateUI();
			new handleTask().execute();
			pbd.run();
		} else if(cmd.equals("remove")) {
			transData.remove(transfocusedRowIndex);
			transtable.updateUI();
		} else if(cmd.equals("removeAll")) {
			transData.removeAllElements();
			transtable.updateUI();
		} else if(cmd.equals("s_remove")) {
			successData.remove(successfocusedRowIndex);
			successtable.updateUI();
		} else if(cmd.equals("s_removeAll")) {
			successData.removeAllElements();
			successtable.updateUI();
		} else if(cmd.equals("s_remove_all_handle_this")) {
			transData.add(successData.get(successtable.getSelectedRow()));
			successData.removeAllElements();
			successtable.updateUI();
			transtable.updateUI();
		} else if(cmd.equals("s_remove_all_handle_all")) {
			for(int i=0;i<successData.size();i++) {
				transData.add(successData.get(i));
			}
			successData.removeAllElements();
			successtable.updateUI();
			transtable.updateUI();
		} else if(cmd.equals("f_remove")) {
			failData.remove(failfocusedRowIndex);
			failtable.updateUI();
		} else if(cmd.equals("f_removeAll")) {
			failData.removeAllElements();
			failtable.updateUI();
		} else if(cmd.equals("f_remove_all_handle_this")) {
			transData.add(failData.get(failtable.getSelectedRow()));
			failData.removeAllElements();
			failtable.updateUI();
			failtable.updateUI();
	    } else if(cmd.equals("f_remove_all_handle_all")) {
	    	for(int i=0;i<successData.size();i++) {
				transData.add(failData.get(i));
			}
			failData.removeAllElements();
			failtable.updateUI();
			transtable.updateUI();
		 }
	}
	//������صĶ˿ں�
	static int getport(String info)
	{
		int p2tail = info.lastIndexOf(")");
		int p2head = info.lastIndexOf(",")+1;
		int p1tail = info.lastIndexOf(",");
		int p1head = info.lastIndexOf(",",p1tail-1)+1;
		String p1 = info.substring(p1head, p1tail);
		String p2 = info.substring(p2head, p2tail);
		int P1 = Integer.parseInt(p1);
		int P2 = Integer.parseInt(p2);
		int ID = P1*256+P2;
		return ID;
	}
	public void getDir2(DefaultMutableTreeNode parentNode) {
		String listinfo = null;
		pw.write("PASV\r\n");
		pw.flush();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		pw.write("LIST \r\n");
		pw.flush();
		Socket dataSocket;
		try {
			dataSocket = new Socket(remoteUrl.getHost(),ID);
			InputStream inputStream = dataSocket.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			//	commendSocket.shutdownOutput();//�ر������
			int offset;  
			byte[] bytes = new byte[1024];  
			while ((offset = inputStream.read(bytes)) != -1) { 
				outputStream.write(bytes, 0, offset);  
			}  
		    listinfo = new String(outputStream.toByteArray());
			inputStream.close();  
			outputStream.close();  
			dataSocket.close();
		} catch(ConnectException ee) {
	            JOptionPane.showMessageDialog(window, "<html><body><font face = '΢���ź�'>����ʧ�ܣ������ԡ�<body/><html/>","��ʾ", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updataRemoteTree(listinfo,parentNode);
        System.out.println("ok");
	}
	//��÷��������ļ��б�String��
	public String getDir(String link,String name,String password,String port) {
		String listinfo=null;
		try {
			//1.�����ͻ���Socket��ָ����������ַ�Ͷ˿�
			remoteUrl = new URL(link);
			commendSocket = new Socket(remoteUrl.getHost(),21);
			//2.��ȡ��������������������Ϣ
			OutputStream os = commendSocket.getOutputStream();//�ֽ������
			pw = new PrintWriter(os);//���������װΪ��ӡ��
			pw.write("USER "+name+"\r\n");
			pw.flush();
			pw.write("PASS "+password+"\r\n");
			pw.flush();
			pw.write("PASV\r\n");
			pw.flush();
	        ID = 0;
			//4.��ȡ�ļ�Ŀ¼
			pw.write("LIST \r\n");
			pw.flush();
			//3.��ȡ������������ȡ����������Ӧ��Ϣ
			InputStream is = commendSocket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String info = null;
			while((info=br.readLine())!=null) {//ѭ����ȡ�ͻ�����Ϣ
				infomation.append(info+"\r\n");
				InetAddress address = commendSocket.getInetAddress();
				
				if(info.substring(0,3).equals("227"))
				{	
					ID = getport(info);
					break;
				}
				
			}
			Socket dataSocket = new Socket(remoteUrl.getHost(),ID);
			InputStream inputStream = dataSocket.getInputStream();
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int offset;  
	        byte[] bytes = new byte[1024];  
	        while ((offset = inputStream.read(bytes)) != -1) { 
	            outputStream.write(bytes, 0, offset);  
	        }  
	        listinfo = new String(outputStream.toByteArray());
	        remoteRoot = new DefaultMutableTreeNode("");
	        remoteTree = new JTree(remoteRoot);
	        inputStream.close();  
	        outputStream.close();  
	        dataSocket.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e) {
          
		}

        return listinfo;
	}
	//������½����	
	public JPanel createLoginBar() {
		JPanel loginBar = new JPanel();
		loginBar.setPreferredSize(new Dimension(1200,33));
		url = new JTextField();
		loginName = new JTextField();
		passWord = new JTextField();
		post = new JTextField();
		
		JLabel label1 = new JLabel("����(H):");
		JLabel label2 = new JLabel("�û���(U):");
		JLabel label3 = new JLabel("����(W):");
		JLabel label4 = new JLabel("�˿�(P):");
		JButton login = new JButton("����");
		JButton cut = new JButton("�Ͽ�");
		
		label1.setFont(new Font("΢���ź�",0,13));
		label2.setFont(new Font("΢���ź�",0,13));
		label3.setFont(new Font("΢���ź�",0,13));
		label4.setFont(new Font("΢���ź�",0,13));
		
		url.setPreferredSize(new Dimension(150,22));
		loginName.setPreferredSize(new Dimension(100,22));
		passWord.setPreferredSize(new Dimension(100,22));
		post.setPreferredSize(new Dimension(50,22));
		
		
		login.addActionListener(this);
		login.setActionCommand("login");
		cut.addActionListener(this);
		cut.setActionCommand("cut");
		
		loginBar.add(label1);
		loginBar.add(url);
		loginBar.add(label2);
		loginBar.add(loginName);
		loginBar.add(label3);
		loginBar.add(passWord);
		loginBar.add(label4);
		loginBar.add(post);
		loginBar.add(login);
		loginBar.add(cut);
		
		return loginBar;
	}
	//���������������Ϣ�ı���
	public JPanel createInfoArea() {
		JPanel infoArea = new JPanel();
		infoArea.setPreferredSize(new Dimension(1200,80));
		infomation = new JTextArea();
		infomation.setEditable(false);
		infoArea.add(infomation);
        scrollpane = new JScrollPane();//���ù�����
		scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollpane.setPreferredSize(new Dimension(1200,82));
		scrollpane.setViewportView(infomation);
		infoArea.add(scrollpane);
		return infoArea;
	}
	//�������ش���
	public JPanel createLocalPanel() {
		JPanel localPanel = new JPanel();
		localPanel.setLayout(new BoxLayout(localPanel,BoxLayout.Y_AXIS));
	//	localPanel.setBackground(Color.pink);
		localPanel.add(createPathPanel("����վ��"));
		localPanel.add(createLocalTreePanel());
		return localPanel;
	}
	//����վ�� ��ַ�����������б�
  	public JPanel createPathPanel(String string) {
		JPanel pathSelect = new JPanel();
		pathSelect.setLayout(new BoxLayout(pathSelect,BoxLayout.X_AXIS));
		pathSelect.setBackground(Color.white);
		pathSelect.setPreferredSize(new Dimension(589,20));
		JLabel label = new JLabel(string,JLabel.CENTER);
		label.setFont(new Font("΢���ź�",0,13));
		label.setBackground(Color.gray);
		label.setPreferredSize(new Dimension(89,20));
		if(string.equals("����վ��")) {
			localPath = new JComboBox<String>();
			localPath.setEditable(true);
			localPath.setPreferredSize(new Dimension(500,20));
			pathSelect.add(label);
			pathSelect.add(localPath);
		} else {
			remotePath = new JComboBox<String>();
			remotePath.setEditable(true);
			remotePath.setPreferredSize(new Dimension(500,20));
			pathSelect.add(label);
			pathSelect.add(remotePath);
		}
		
		return pathSelect;
	}
  	//����վ����ļ�Ŀ¼��
	public JPanel createLocalTreePanel() {
		JPanel treePanel = new JPanel();
		treePanel.setLayout(new BoxLayout(treePanel,BoxLayout.Y_AXIS));
		treePanel.setPreferredSize(new Dimension(589,180));
		treePanel.setLayout(new BorderLayout());
		DefaultMutableTreeNode local = new DefaultMutableTreeNode("������");
		File[] roots = File.listRoots();
		for(File files:roots) {
			DefaultMutableTreeNode leaf = new DefaultMutableTreeNode (files.getAbsolutePath().substring(0,1));
			local.add(leaf);
			File[] fileList = files.listFiles();
			if(fileList!=null){
				leaf.add(new DefaultMutableTreeNode("is_Empty_Dir"));
			}
		}
		localTree = new JTree(local);
		localTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
		    public void valueChanged(TreeSelectionEvent evt) {
		        // Get all nodes whose selection status has changed
		        TreePath[] paths = evt.getPaths();
		 
		        // Iterate through all affected nodes
		        for (int i=0; i<paths.length; i++) {
		            if (evt.isAddedPath(i)) {
		            	
		            	traversalLocalNode((DefaultMutableTreeNode)paths[i].getLastPathComponent());
		            	localPath.addItem(getNodeFilePath((DefaultMutableTreeNode)paths[i].getLastPathComponent()));
		        		localPath.setSelectedIndex(localPath.getItemCount()-1);
		            	setLocalTree(new File(getNodeFilePath((DefaultMutableTreeNode)paths[i].getLastPathComponent())));
		        		
		            } else {
		            }
		        }
		        SwingUtilities.invokeLater(new Runnable() {  
                    public void run() {  
                        localTree.updateUI();  
                    }  
                });  
		    }
		});
		localTree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = localTree.getRowForLocation(e.getX(), e.getY());
				if(selRow!=-1) {
					if(e.getButton()==MouseEvent.BUTTON3) {
						
						
						TreePath selPath = localTree.getPathForLocation(e.getX(), e.getY());
						DefaultMutableTreeNode Node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
						localTree.setSelectionPath(selPath);
						localPath.setSelectedIndex(localPath.getItemCount()-1);
			        	localFilePopup.show(localTree, e.getX(), e.getY());
			    	 }
				}
			}
		});
		localTree.expandRow(0);
		JScrollPane treeView = new JScrollPane(localTree);
		treePanel.add(treeView);
		return treePanel;
	}
	//��������������ڵ��ȡ�����ļ���·��
	public String getNodeFilePath(DefaultMutableTreeNode parentNode) {
		TreeNode[] list = parentNode.getPath();
		StringBuffer path =new StringBuffer(list[1].toString()+":");
		for(int i=2;i<list.length;i++) {
			
			path.append("\\");
			path.append(list[i].toString());
		}
		String parentPath = path.toString();
		return parentPath;
	}
	//����Զ�̴���
	public JPanel createRemotePanel() {
		JPanel RemotePanel = new JPanel();
		RemotePanel.setLayout(new BoxLayout(RemotePanel,BoxLayout.Y_AXIS));
	//	RemotePanel.setBackground(Color.DARK_GRAY);
		RemotePanel.add(createPathPanel("Զ��վ��"));
		RemotePanel.add(createRemoteTreePanel());
		return RemotePanel;
	}
	//����Զ��վ����ļ�Ŀ¼��
	public JPanel createRemoteTreePanel() {
		remoteTreePanel = new JPanel();
		remoteTreePanel.setLayout(new BoxLayout(treePanel,BoxLayout.Y_AXIS));
	//	remoteTreePanel.setBackground(Color.PINK);
		remoteTreePanel.setPreferredSize(new Dimension(589,180));
		remoteTreePanel.setLayout(new BorderLayout());
		remoteRoot = null;

		
		return remoteTreePanel;
	}
	//����Զ���ļ��б�
	public void setRemoteList() {
		remoteTable.updateUI();
	}
	//����Զ���ļ�Ŀ¼��
	public void updataRemoteTree(String listinfo,DefaultMutableTreeNode parentNode) {
		try {
			System.out.println(listinfo);
			AnalysisLocal(listinfo);
		} catch(java.lang.NullPointerException e){
			
		}
	
		traversalRemoteNode(parentNode,remoteData);
		remoteTreePanel.updateUI();
	}
	//���������ļ��б�
	public JPanel createLocalTree() {
		JPanel localTree = new JPanel();
	//	localTree.setBackground(Color.white);
		JScrollPane listScroll = new JScrollPane();
		localTree.setLayout(new BoxLayout(localTree,BoxLayout.Y_AXIS));
		listScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		listScroll.setPreferredSize(new Dimension(589,200));
		Vector<String> columnNames =new Vector<String>();
		columnNames.add("�ļ���");
		columnNames.add("�ļ�����");
		columnNames.add("�ļ���С");
		columnNames.add("�޸�ʱ��");
		localData = new Vector<Vector<String>>();
		localTable = new MyTable(localData,columnNames);
		//��table����һ������¼�����������
		localTable.addMouseListener(new java.awt.event.MouseAdapter() {  
            public void mouseClicked(java.awt.event.MouseEvent evt) {  
            	localMouseRightButtonClick(evt);    
            }  
		});  
		listScroll.setViewportView(localTable);
		localTree.add(listScroll);
		return localTree;
	}
	
	//���±����ļ��б�
	
	public void setLocalTree(File dir) {

		
		
		localData.removeAllElements();
		String[] files = dir.list();
		if(files!=null) {
			for(String fileName:files) {
				Vector<String> row = new Vector<String>();
				File file = new File(dir.getPath()+File.separator+fileName);
				row.add(file.getName());
				if(file.isFile()) {
					row.add("�ļ�");
					row.add(formetFileSize(file.length()));
				}
				else {
					row.add("Ŀ¼");
					row.add(" ");
				}
				row.add(getStandardTime(file.lastModified()));
				localData.add(row);
			}
		}
		localTable.updateUI();
	}
	//����һ��long��Ķ���õ��ļ��Ĵ�С
	public String formetFileSize(long file) { 
        DecimalFormat df = new DecimalFormat("#.00"); 
        String fileSizeString = ""; 
        if (file < 1024) { 
            fileSizeString = df.format((double) file) + "B"; 
        } else if (file < 1048576) { 
            fileSizeString = df.format((double) file / 1024) + "K"; 
        } else if (file < 1073741824) { 
            fileSizeString = df.format((double) file / 1048576) + "M"; 
        } else { 
            fileSizeString = df.format((double) file / 1073741824) + "G"; 
        } 
        return fileSizeString; 
    } 
	//���ļ��Ĵ�Сת����long
	public double sizeToLong(String size) {
		double length = 0;
		if(size.substring(size.length(),size.length()).equals("B")) {
			length = Double.valueOf(size.substring(0,size.length()-1));
		} else if(size.substring(size.length()-1, size.length()).equals("K")) {
			length = Double.valueOf(size.substring(0,size.length()-1));
			length = length*1024;
		} else if(size.substring(size.length()-1, size.length()).equals("M")) {
			length = Double.valueOf(size.substring(0,size.length()-1));
			length = length*1024*1024;
		} else if(size.substring(size.length()-1,size.length()).equals("G")) {
			length = Double.valueOf(size.substring(0,size.length()-1));
			length = length*1024*1024*1024;
		}
		return Math.ceil(length);
	}
//����һ��long��Ķ���õ�ʱ��
	public String getStandardTime(long timestamp) {  
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timestamp); 
		Date date = c.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd   hh:mm:ss");
		return sdf.format(date);   
	}
	//����Զ���ļ��б�
	public JPanel createRemoteTree() {
		JPanel RemoteTree = new JPanel();
		RemoteTree.setLayout(new BoxLayout(RemoteTree,BoxLayout.Y_AXIS));
		JScrollPane listScroll = new JScrollPane();
		listScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		listScroll.setPreferredSize(new Dimension(589,200));
		Vector<String> columnNames =new Vector<String>();
		columnNames.add("�޸�ʱ��");
		columnNames.add("�ļ�����");
		columnNames.add("�ļ���С");
		columnNames.add("�ļ���");
		remoteData = new Vector<Vector<String>>();
		remoteTable = new MyTable(remoteData,columnNames);
		remoteTable.addMouseListener(new java.awt.event.MouseAdapter() {  
            public void mouseClicked(java.awt.event.MouseEvent evt) {  
            	remoteMouseClicked(evt);  
            }  
		}); 
		listScroll.setViewportView(remoteTable);
		RemoteTree.add(listScroll);
		return RemoteTree;
		
	}
	//����topPane
	public JPanel createTopPanel() {
		topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel,BoxLayout.Y_AXIS));
		//topPanel.setBackground(Color.BLUE);
		topPanel.setPreferredSize(new Dimension(1200,115));
		topPanel.add(createLoginBar());
		topPanel.add(createInfoArea());
		return topPanel;
	}
	//����middlPane
	public JPanel createMiddlePanel() {
		middlePanel = new JPanel();
		middlePanel.setLayout(new GridLayout(2,2,2,2));
		middlePanel.setBackground(Color.gray);
		middlePanel.setPreferredSize(new Dimension(1180,400));
		middlePanel.add(createLocalPanel());
		middlePanel.add(createRemotePanel());
		middlePanel.add(createLocalTree());
		middlePanel.add(createRemoteTree());
		return middlePanel;
	}
	//����bottoPane
	public JPanel createButtomPanel() {
		buttomPanel = new JPanel();
		buttomPanel.setLayout(new BoxLayout(buttomPanel,BoxLayout.Y_AXIS));
		buttomPanel.setPreferredSize(new Dimension(1180,150));
		buttomPanel.add(createTabPane());
		buttomPanel.add(createStatePanel());
		return buttomPanel;
	}
	//������ǩҳ
	public JTabbedPane createTabPane() {
		JTabbedPane tabPane = new JTabbedPane();
		tabPane.setPreferredSize(new Dimension(1180,140));
		tabPane.setTabPlacement(JTabbedPane.TOP);
		
		
		JPanel transferPanel = new JPanel();
		transferPanel.setLayout(new BoxLayout(transferPanel,BoxLayout.Y_AXIS));
		JScrollPane listScroll1 = new JScrollPane();
		listScroll1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		listScroll1.setPreferredSize(new Dimension(1180,107));
		Vector<String> columnNames1 =new Vector<String>();
		columnNames1.add("������/�����ļ�");
		columnNames1.add("����");
		columnNames1.add("Զ���ļ�");
		columnNames1.add("��С");
		columnNames1.add("ʱ��");
		transData = new Vector<Vector<String>>();
		transtable = new MyTable(transData,columnNames1);
		DefaultTableCellRenderer  r  =  new  DefaultTableCellRenderer();   
		r.setHorizontalAlignment(JTextField.CENTER);   
		transtable.getColumn("����").setCellRenderer(r);
		listScroll1.setViewportView(transtable);
		transferPanel.add(listScroll1);
		transtable.addMouseListener(new java.awt.event.MouseAdapter() {  
            public void mouseClicked(java.awt.event.MouseEvent evt) {  
            	transMouseClicked(evt);  
            }  
		});  
		
		JPanel successfulPanel = new JPanel();
		successfulPanel.setLayout(new BoxLayout(successfulPanel,BoxLayout.Y_AXIS));
		JScrollPane listScroll2 = new JScrollPane();
		listScroll2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		listScroll2.setPreferredSize(new Dimension(1180,107));
		Vector<String> columnNames2 =new Vector<String>();
		columnNames2.add("������/�����ļ�");
		columnNames2.add("����");
		columnNames2.add("Զ���ļ�");
		columnNames2.add("��С");
		columnNames2.add("ʱ��");
		successData = new Vector<Vector<String>>();
		successtable = new MyTable(successData,columnNames2);
		listScroll2.setViewportView(successtable);
		successfulPanel.add(listScroll2);
		successtable.addMouseListener(new java.awt.event.MouseAdapter() {  
            public void mouseClicked(java.awt.event.MouseEvent evt) {  
            	successMouseClicked(evt);  
            }  
		});  
		
		JPanel failPanel = new JPanel();
		failPanel.setLayout(new BoxLayout(failPanel,BoxLayout.Y_AXIS));
		JScrollPane listScroll3 = new JScrollPane();
		listScroll3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		listScroll3.setPreferredSize(new Dimension(1180,107));
		Vector<String> columnNames3 =new Vector<String>();
		columnNames3.add("������/�����ļ�");
		columnNames3.add("����");
		columnNames3.add("Զ���ļ�");
		columnNames3.add("��С");
		columnNames3.add("ʱ��");
		failData = new Vector<Vector<String>>();
		failtable = new MyTable(failData,columnNames3);
		listScroll3.setViewportView(failtable);
		failPanel.add(listScroll3);
		failtable.addMouseListener(new java.awt.event.MouseAdapter() {  
            public void mouseClicked(java.awt.event.MouseEvent evt) {  
            	failMouseClicked(evt);  
            }  
		});  
		
		tabPane.add("���е��ļ�",transferPanel);
		tabPane.addTab("����ɹ�", successfulPanel);
		tabPane.add("����ʧ��", failPanel);
		return tabPane;
	}
    private void localMouseRightButtonClick(MouseEvent evt) {
   	 //�ж��Ƿ�Ϊ�Ҽ�
   	 if(evt.getButton()==MouseEvent.BUTTON3) {
   		 //ͨ�����λ���ҵ����Ϊ����е���
   		 localfocusedRowIndex = localTable.rowAtPoint(evt.getPoint());
   		 if(localfocusedRowIndex == -1) {
   			 	return;
   		 }
       	 //�������ѡ����Ϊ��ǰ�Ҽ��һ�����
       	 localTable.setRowSelectionInterval(localfocusedRowIndex, localfocusedRowIndex);
       	 //�����˵�
  
      
       	 localPath.addItem(getNodeFilePath((DefaultMutableTreeNode)localTree.getSelectionPath().getLastPathComponent())+"\\"+localTable.getValueAt(localfocusedRowIndex, 0));
		localPath.setSelectedIndex(localPath.getItemCount()-1);
     localFilePopup.show(localTable, evt.getX(), evt.getY());
	   	 } else if(evt.getClickCount()==2) {
	   		 int focusedRowIndex = localTable.rowAtPoint(evt.getPoint());
	   		 if(focusedRowIndex == -1) {
	   			 	return;
	   		 }
	       	 //�������ѡ����Ϊ��ǰ�Ҽ��һ�����
	       	 localTable.setRowSelectionInterval(focusedRowIndex, focusedRowIndex);
	       	 //�����˵�
	      
	       	 File file = new File(localPath.getSelectedItem().toString()+"\\"+localTable.getValueAt(focusedRowIndex, 0));
	       
	    		localPath.addItem(file.getAbsolutePath());
	    		localPath.setSelectedIndex(localPath.getItemCount()-1);
	       
	       	 setLocalTree(file);
	   	 }
   	 
    }
	//��������״̬��
	public JPanel createStatePanel() {
		
		statePanel = new JPanel();
		statePanel.setLayout(new BoxLayout(statePanel,BoxLayout.Y_AXIS));
		statePanel.setBackground(Color.white);
		statePanel.setPreferredSize(new Dimension(1180,28));
		return statePanel;
	}
	//���������ǩҳ
	public JPanel createTransferPanel() {
		JPanel transferPanel = new JPanel();
		JScrollPane listScroll = new JScrollPane();
		listScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		listScroll.setPreferredSize(new Dimension(1180,107));
		Vector<String> columnNames =new Vector<String>();
		columnNames.add("������/�����ļ�");
		columnNames.add("����");
		columnNames.add("Զ���ļ�");
		columnNames.add("��С");
		columnNames.add("���ȼ�");
		columnNames.add("ʱ��");
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		MyTable table = new MyTable(data,columnNames);
		listScroll.setViewportView(table);
		transferPanel.add(listScroll);
		return transferPanel;
	}
	//����Զ��Ŀ¼�������ڵ�
	public void traversalRemoteNode(DefaultMutableTreeNode parentNode,Vector<Vector<String>> list) {
	
		if(!list.isEmpty()&&
				(parentNode.toString().equals("Զ�̷�����")||(parentNode.getChildCount()!=0&&parentNode.getFirstChild().toString().equals("is_Empty_Dir")))) {
			parentNode.removeAllChildren();
			for(int i=0;i<list.size();i++) {
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(list.get(i).get(3));
				parentNode.add(childNode);
				if(list.get(i).get(1).equals("Ŀ¼")) {
					childNode.add(new DefaultMutableTreeNode("is_Empty_Dir"));
				}
			}
		}
		SwingUtilities.invokeLater(new Runnable() {  
            public void run() {  
                remoteTree.updateUI();  
            }  
        });
	}
	//��������Ŀ¼�������ڵ�
	public void traversalLocalNode(DefaultMutableTreeNode parentNode) {
		File parentFile = new File(getNodeFilePath(parentNode));
		localPath.addItem(parentFile.getAbsolutePath());
		localPath.setSelectedIndex(localPath.getItemCount()-1);
		if(parentNode.getChildCount()!=0){
			if(parentNode.getFirstChild().toString().equals("is_Empty_Dir")) {
				File[] fileList = parentFile.listFiles();
				if(fileList.length!=0) {
					parentNode.removeAllChildren();
					for(int i=0;i<fileList.length;i++) {
						if(!fileList[i].isHidden()) {
							DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(fileList[i].getName());
							parentNode.add(childNode);
							if(fileList[i].listFiles()!=null)
								childNode.add(new DefaultMutableTreeNode("is_Empty_Dir"));
						}
					}

				}
				
			}
		}
		
	}
	//����GUI
	public static  void createAndShowGUI(){
	    Frame frame = new Frame();
	    //��������
	    frame.localPopupMenu();
	    frame.remotePopupMenu();
	    frame.transPopupMenu();
	    frame.successPopupMenu();
	    frame.failPopupMenu();
	    window = new JFrame("FTP");
	    window.setResizable(false);
	    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    window.addWindowListener(new WindowAdapter() {
	    	public void windowClosing(WindowEvent e) {
	    		int exitVal = JOptionPane.showConfirmDialog(null, "Ҫ�˳��ó�����","��ʾ",
	    				JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
	    		if(exitVal == JOptionPane.YES_OPTION) {
	    			if(pw!=null) {
		    			pw.write("QUIT \r\n");
		    			pw.flush();
		    			pw.close();
		    			try {
							commendSocket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		    		}
	    			window.dispose();
	    		}
	    		else {
	    			window.setVisible(true);
	    		}
	    	}
	    });
	    window.setSize(1200,768);
	    JPanel mainPanel = new JPanel();
	    mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
	    mainPanel.setBackground(Color.PINK);
	    	
	    mainPanel.add(frame.createTopPanel());
	    mainPanel.add(frame.createMiddlePanel());
	    mainPanel.add(frame.createButtomPanel()); 
	    
	    window.getContentPane().add(mainPanel);
	    window.setJMenuBar(createMenuBar());
	    window.setVisible(true);
	}
	//������
	public static void main(String[]args){
		//ʹ��Nimbus���
		for(LookAndFeelInfo info:UIManager.getInstalledLookAndFeels()) {
			if("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				createAndShowGUI();
			}
		});
	}
	//�˵���
	public static  JMenuBar createMenuBar() {

	      //�����˵��������ò�͸������ɫ����ʼ��С
	      JMenuBar menubar = new JMenuBar();
	      menubar.setOpaque(true);
	      menubar.setBackground(new Color(255,228,225));//255,228,225
	      menubar.setPreferredSize(new Dimension(800,20));
	      
	      //��Ӳ˵�
	      JMenu menu1 = new JMenu("�ļ�(F)");
	      JMenu menu2 = new JMenu("�༭(E)");
	      JMenu menu3 = new JMenu("�鿴(O)");
	      JMenu menu4 = new JMenu("����(V)");
	      JMenu menu5 = new JMenu("������(T)");
	      JMenu menu6 = new JMenu("��ǩ(B)");
	      JMenu menu7 = new JMenu("����(H)");
	      //���ü������Ƿ�,����Alt+�ַ���ݼ���
	      menu1.setMnemonic(KeyEvent.VK_F);
	      menu2.setMnemonic(KeyEvent.VK_E);
	      menu3.setMnemonic(KeyEvent.VK_O);
	      menu4.setMnemonic(KeyEvent.VK_V);
	      menu5.setMnemonic(KeyEvent.VK_T);
	      menu6.setMnemonic(KeyEvent.VK_B);
	      menu7.setMnemonic(KeyEvent.VK_H);
	      
	      menu1.setFont(new Font("΢���ź�",0, 12));
	      menu2.setFont(new Font("΢���ź�",0, 12));
	      menu3.setFont(new Font("΢���ź�",0, 12));
	      menu4.setFont(new Font("΢���ź�",0, 12));
	      menu5.setFont(new Font("΢���ź�",0, 12));
	      menu6.setFont(new Font("΢���ź�",0, 12));
	      menu7.setFont(new Font("΢���ź�",0, 12));
	      menu1.setForeground(new Color(139,26,26));
	      menu2.setForeground(new Color(139,26,26));
	      menu3.setForeground(new Color(139,26,26));
	      menu4.setForeground(new Color(139,26,26));
	      menu5.setForeground(new Color(139,26,26));
	      menu6.setForeground(new Color(139,26,26));
	      menu7.setForeground(new Color(139,26,26));
	      //���˵������˵���
	      menubar.add(menu1);
	      menubar.add(menu2);
	      menubar.add(menu3);
	      menubar.add(menu4);
	      menubar.add(menu5);
	      menubar.add(menu6);
	      menubar.add(menu7);
	      
	      //��Ӳ˵���1
	      JMenuItem menu1item1 = new JMenuItem("�½�(N)",new ImageIcon("newset.png"));      
	      JMenuItem menu1item2 = new JMenuItem("��(O)",new ImageIcon("open.png"));
	      JMenuItem menu1item3 = new JMenuItem("����(S)",new ImageIcon("save.png"));
	      JMenuItem menu1item4 = new JMenuItem("���Ϊ(A)",new ImageIcon("reset.png"));
	      JMenuItem menu1item5 = new JMenuItem("�˳�(E)",new ImageIcon("exit.png"));
	      menu1item1.setMnemonic(KeyEvent.VK_N);
	      menu1item2.setMnemonic(KeyEvent.VK_O);
	      menu1item3.setMnemonic(KeyEvent.VK_S);
	      menu1item4.setMnemonic(KeyEvent.VK_A);
	      menu1item5.setMnemonic(KeyEvent.VK_E);
	      menu1item1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.CTRL_MASK));
	      menu1item2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.CTRL_MASK));
	      menu1item3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
	      menu1item4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
	      menu1item5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK));
	      menu1item1.setFont(new Font("΢���ź�",0, 12));
	      menu1item2.setFont(new Font("΢���ź�",0, 12));
	      menu1item3.setFont(new Font("΢���ź�",0, 12));
	      menu1item4.setFont(new Font("΢���ź�",0, 12));
	      menu1item5.setFont(new Font("΢���ź�",0, 12));
	      menu1item1.setForeground(new Color(139,26,26));
	      menu1item2.setForeground(new Color(139,26,26));
	      menu1item3.setForeground(new Color(139,26,26));
	      menu1item4.setForeground(new Color(139,26,26));
	      menu1item5.setForeground(new Color(139,26,26));
	      
	      return menubar;
	  }
	//����LIST��ȡ����Ϣ��ȡ��ά���ݣ����ڽ���Զ��Ŀ¼�б�
	public void Analysis(String listinfo) {
		remoteData.removeAllElements();
		int lastpos= 0 ;
		int pos= 0 ;
		int data_num = 0;
		for(int m=0;m<listinfo.length();m++) {
			Vector<String> filedata = new Vector<String>();
			for(int i=m;i<listinfo.length();i++) {
				int flag2=0;
				if(listinfo.charAt(i)==' ')
					continue;
				else {
					lastpos = i;
					pos = listinfo.indexOf(' ',lastpos);
					String str = listinfo.substring(lastpos,pos);
					String message = transMessage(data_num,str);
					if(!message.equals("!")||data_num==7) {
						filedata.add(message);
						if(data_num==7)
						time = new StringBuffer("");
					}
					i=pos;
					data_num++;
				}
				if(data_num==8) {
					for(int j=i;j<listinfo.length();j++) {
						int flag=0;
						if(listinfo.charAt(j)!=' ') {
							lastpos=j;
							for(int k=j;k<listinfo.length();k++) {
								if(listinfo.substring(k,k+2).equals("\r\n")) {
									flag=1;
									pos=k;
									String str = listinfo.substring(lastpos,pos);
									filedata.add(str);
									i=pos+2;
									break;
								}
							}
						}
						if(flag==1)
							break;
					}
					data_num=0;
					remoteData.add(filedata);
					flag2=1;
				}
				if(flag2==1) {
					m=i;
					break;
				}
			}
		}
		remoteTable.updateUI();
	}
	//����LIST��ȡ����Ϣ��ȡ��ά���ݣ����ڽ���Զ��Ŀ¼�б�
	public void AnalysisLocal(String listinfo) {
		remoteData.removeAllElements();
		int lastpos= 0 ;
		int pos= 0 ;
		int data_num = 0;
		for(int m=0;m<listinfo.length();m++) {
			data_num = 0;
			Vector<String> filedata = new Vector<String>();
			for(int i=m;i<listinfo.length();i++) {
				int flag2=0;
				if(listinfo.charAt(i)==' ')
					continue;
				else {
					lastpos = i;
					pos = listinfo.indexOf(' ',lastpos);
					String str = listinfo.substring(lastpos,pos);
					
					String message = transMessage(data_num,str);
					System.out.println(message);
					if(!message.equals("!")) {
						System.out.println("a");
						if(message.equals("Ŀ¼")) {
							filedata.add(message);
							filedata.add(" ");
						} else if(message.indexOf("�ļ�")!=-1) {
							filedata.add("�ļ�");
							filedata.add(message.substring(2,message.length()));
						} else {
							filedata.add(message);
						}
						
					}
					i=pos;
					data_num++;	
					
				}
				if(data_num==3) {
					for(int j=i;j<listinfo.length();j++) {
						int flag=0;
						if(listinfo.charAt(j)!=' ') {
							lastpos=j;
							for(int k=j;k<listinfo.length();k++) {
								if(listinfo.substring(k,k+2).equals("\r\n")) {
									flag=1;
									pos=k;
									String str = listinfo.substring(lastpos,pos);
									System.out.println("1"+str+"1");
									filedata.add(str);
									i=pos+1;
									break;
								}
							}
						}
						if(flag==1)
							break;
					}
					data_num=0;
					remoteData.add(filedata);
					System.out.println("yes");
					flag2=1;
				}
				if(flag2==1) {
					m=i;
					break;
				}
			}
		}
		remoteTable.updateUI();
	}
	 //���ݻ�õĶ�ά���ݽ������Ӧ���б���Ϣ
	public String transMessage(int order,String Message) {
		if(order==1) {
			return new String("!");
		} else if(order==2) {
			if(Message.charAt(0)=='<')
				return new String("Ŀ¼");
			else 
				return new String("�ļ�"+Message);
		} else 
			return Message;
	}
	//�����˵�������

	public   void remotePopupMenu(){
        
		remoteFilePopup = new JPopupMenu();
		
        JMenuItem menuitem1 = new JMenuItem("����");
        JMenuItem menuitem2 = new JMenuItem("��ӵ�����");
        JMenuItem menuitem3 = new JMenuItem("����Ŀ¼");
        
        menuitem1.setMnemonic(KeyEvent.VK_D);
        menuitem2.setMnemonic(KeyEvent.VK_A);
        menuitem3.setMnemonic(KeyEvent.VK_I);
       
        menuitem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.CTRL_MASK));
        menuitem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
        menuitem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,ActionEvent.CTRL_MASK));

        
        menuitem1.setFont(new Font("΢���ź�",0, 12));
        menuitem2.setFont(new Font("΢���ź�",0, 12));
        menuitem3.setFont(new Font("΢���ź�",0, 12));
   
        menuitem1.setForeground(new Color(139,26,26));
        menuitem2.setForeground(new Color(139,26,26));
        menuitem3.setForeground(new Color(139,26,26));

        menuitem1.setActionCommand("download");
        menuitem1.addActionListener(this);
        menuitem2.setActionCommand("r_add to the queue");
        menuitem2.addActionListener(this);
        menuitem3.setActionCommand("in_to_remote_dir");
        menuitem3.addActionListener(this);
        
        remoteFilePopup.add(menuitem1);
        remoteFilePopup.add(menuitem2);
        remoteFilePopup.add(menuitem3);
    }
	//�������ص���ʽ�˵�
    public   void localPopupMenu(){

		localFilePopup = new JPopupMenu();
		
		JMenuItem menuitem1 = new JMenuItem("�ϴ�");
		JMenuItem menuitem2 = new JMenuItem("��ӵ�����");
		JMenuItem menuitem3 = new JMenuItem("�����Ŀ¼");
		
		menuitem1.setMnemonic(KeyEvent.VK_T);
		menuitem2.setMnemonic(KeyEvent.VK_C);
		menuitem3.setMnemonic(KeyEvent.VK_I);
         
		menuitem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,ActionEvent.CTRL_MASK));
		menuitem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.CTRL_MASK));
		menuitem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,ActionEvent.CTRL_MASK));

          
		menuitem1.setFont(new Font("΢���ź�",0, 12));
		menuitem2.setFont(new Font("΢���ź�",0, 12));
		menuitem3.setFont(new Font("΢���ź�",0, 12));
     
		menuitem1.setForeground(new Color(139,26,26));
		menuitem2.setForeground(new Color(139,26,26));
		menuitem3.setForeground(new Color(139,26,26));

		menuitem1.setActionCommand("upload");
		menuitem1.addActionListener(this);
		menuitem2.setActionCommand("l_add to the queue");
		menuitem2.addActionListener(this);
		menuitem3.setActionCommand("in_to_local_dir");
		menuitem3.addActionListener(this);
          
		localFilePopup.add(menuitem1);
		localFilePopup.add(menuitem2);
		localFilePopup.add(menuitem3);
    }
    //���������б����˵���
    public   void transPopupMenu(){

    	transPopup = new JPopupMenu();
			
    	JMenuItem menuitem1 = new JMenuItem("�������");
    	JMenuItem menuitem2 = new JMenuItem("�Ƴ�ѡ���ļ�");
    	JMenuItem menuitem3 = new JMenuItem("�Ƴ�����");
          
    	menuitem1.setMnemonic(KeyEvent.VK_Q);
    	menuitem2.setMnemonic(KeyEvent.VK_R);
    	menuitem3.setMnemonic(KeyEvent.VK_A);
         
    	menuitem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,ActionEvent.CTRL_MASK));
    	menuitem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK));
    	menuitem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));

          
    	menuitem1.setFont(new Font("΢���ź�",0, 12));
    	menuitem2.setFont(new Font("΢���ź�",0, 12));
    	menuitem3.setFont(new Font("΢���ź�",0, 12));
     
    	menuitem1.setForeground(new Color(139,26,26));
    	menuitem2.setForeground(new Color(139,26,26));
    	menuitem3.setForeground(new Color(139,26,26));

    	menuitem1.setActionCommand("handle_queue");
    	menuitem1.addActionListener(this);
    	menuitem2.setActionCommand("remove");
    	menuitem2.addActionListener(this);
    	menuitem3.setActionCommand("removeAll");
    	menuitem3.addActionListener(this);
          
    	transPopup.add(menuitem1);
    	transPopup.add(menuitem2);
    	transPopup.add(menuitem3);
    }
    //�����ɹ��б����˵���
    public   void successPopupMenu(){

    	successPopup = new JPopupMenu();
    	
    	JMenuItem menuitem1 = new JMenuItem("�Ƴ�ѡ��");
    	JMenuItem menuitem2 = new JMenuItem("�Ƴ�����");
    	JMenuItem menuitem3 = new JMenuItem("���ò���ѡ���ļ��������");
    	JMenuItem menuitem4 = new JMenuItem("���ò��������ļ��������");
      
    	menuitem1.setMnemonic(KeyEvent.VK_R);
    	menuitem2.setMnemonic(KeyEvent.VK_A);
    	menuitem3.setMnemonic(KeyEvent.VK_S);
    	menuitem4.setMnemonic(KeyEvent.VK_E);
     
    	menuitem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK));
    	menuitem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
    	menuitem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
    	menuitem4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK));
      
    	menuitem1.setFont(new Font("΢���ź�",0, 12));
    	menuitem2.setFont(new Font("΢���ź�",0, 12));
    	menuitem3.setFont(new Font("΢���ź�",0, 12));
    	menuitem4.setFont(new Font("΢���ź�",0, 12));
 
    	menuitem1.setForeground(new Color(139,26,26));
    	menuitem2.setForeground(new Color(139,26,26));
    	menuitem3.setForeground(new Color(139,26,26));
    	menuitem4.setForeground(new Color(139,26,26));
      
    	menuitem1.setActionCommand("s_remove");
    	menuitem1.addActionListener(this);
    	menuitem2.setActionCommand("s_removeAll");
    	menuitem2.addActionListener(this);
    	menuitem3.setActionCommand("s_remove_all_handle_this");
    	menuitem3.addActionListener(this);
    	menuitem4.setActionCommand("s_remove_all_handle_all");
    	menuitem4.addActionListener(this);
      
    	successPopup.add(menuitem1);
    	successPopup.add(menuitem2);
    	successPopup.add(menuitem3);
    	successPopup.add(menuitem4);
    } 
    //����ʧ���б����˵���
    public   void failPopupMenu(){

    	failPopup = new JPopupMenu();
	
    	JMenuItem menuitem1 = new JMenuItem("�Ƴ�ѡ��");
    	JMenuItem menuitem2 = new JMenuItem("�Ƴ�����");
    	JMenuItem menuitem3 = new JMenuItem("���ò���ѡ���ļ��������");
    	JMenuItem menuitem4 = new JMenuItem("���ò��������ļ��������");
      
    	menuitem1.setMnemonic(KeyEvent.VK_R);
    	menuitem2.setMnemonic(KeyEvent.VK_A);
    	menuitem3.setMnemonic(KeyEvent.VK_S);
    	menuitem4.setMnemonic(KeyEvent.VK_E);
     
    	menuitem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.CTRL_MASK));
    	menuitem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.CTRL_MASK));
    	menuitem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.CTRL_MASK));
    	menuitem4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,ActionEvent.CTRL_MASK));
      
    	menuitem1.setFont(new Font("΢���ź�",0, 12));
    	menuitem2.setFont(new Font("΢���ź�",0, 12));
    	menuitem3.setFont(new Font("΢���ź�",0, 12));
    	menuitem4.setFont(new Font("΢���ź�",0, 12));
 
    	menuitem1.setForeground(new Color(139,26,26));
    	menuitem2.setForeground(new Color(139,26,26));
    	menuitem3.setForeground(new Color(139,26,26));
    	menuitem4.setForeground(new Color(139,26,26));
      
    	menuitem1.setActionCommand("f_remove");
    	menuitem1.addActionListener(this);
    	menuitem2.setActionCommand("f_removeAll");
    	menuitem2.addActionListener(this);
    	menuitem3.setActionCommand("f_remove_all_handle_this");
    	menuitem3.addActionListener(this);
    	menuitem4.setActionCommand("f_remove_all_handle_all");
    	menuitem4.addActionListener(this);
      
    	failPopup.add(menuitem1);
    	failPopup.add(menuitem2);
    	failPopup.add(menuitem3);
    	failPopup.add(menuitem4);
    } 
    //���������б���������
    private void transMouseClicked(MouseEvent evt) {
    	//�ж��Ƿ�Ϊ�Ҽ�
    	if(evt.getButton()==MouseEvent.BUTTON3) {
    		//ͨ�����λ���ҵ����Ϊ����е���
    		transfocusedRowIndex = transtable.rowAtPoint(evt.getPoint());
    		if(transfocusedRowIndex == -1) {
    			return;
    		}
    		//�������ѡ����Ϊ��ǰ�Ҽ��һ�����
    		transtable.setRowSelectionInterval(transfocusedRowIndex, transfocusedRowIndex);
    		//�����˵�
    		
    		transPopup.show(transtable, evt.getX(), evt.getY());
    	} 
    }
    //�����ɹ��б���������
    private void successMouseClicked(MouseEvent evt) {
    	//�ж��Ƿ�Ϊ�Ҽ�
    	if(evt.getButton()==MouseEvent.BUTTON3) {
    		//ͨ�����λ���ҵ����Ϊ����е���
    		successfocusedRowIndex = successtable.rowAtPoint(evt.getPoint());
    		if(successfocusedRowIndex == -1) {
    			return;
    		}
    		//�������ѡ����Ϊ��ǰ�Ҽ��һ�����
    		successtable.setRowSelectionInterval(successfocusedRowIndex, successfocusedRowIndex);
    		//�����˵�
    		
    		successPopup.show(successtable, evt.getX(), evt.getY());
    	} 
    }
    //����ʧ���б���������
    private void failMouseClicked(MouseEvent evt) {
    	//�ж��Ƿ�Ϊ�Ҽ�
   	 	if(evt.getButton()==MouseEvent.BUTTON3) {
   	 		//ͨ�����λ���ҵ����Ϊ����е���
   	 		failfocusedRowIndex = failtable.rowAtPoint(evt.getPoint());
   	 		if(failfocusedRowIndex == -1) {
   	 			return;
   	 		}
   	 		//�������ѡ����Ϊ��ǰ�Ҽ��һ�����
   	 		failtable.setRowSelectionInterval(failfocusedRowIndex, failfocusedRowIndex);
   	 		//�����˵�
   	 		
   	 		failPopup.show(failtable, evt.getX(), evt.getY());
   	 	} 
    }
    //����Զ���б���������
    private void remoteMouseClicked(MouseEvent evt) {
   	 	//�ж��Ƿ�Ϊ�Ҽ�
   	 	if(evt.getButton()==MouseEvent.BUTTON3) {
   	 		//ͨ�����λ���ҵ����Ϊ����е���
   	 		remotefocusedRowIndex = remoteTable.rowAtPoint(evt.getPoint());
   	 		if(remotefocusedRowIndex == -1) {
   	 			return;
   	 		}
       	 //�������ѡ����Ϊ��ǰ�Ҽ��һ�����
   	 		remoteTable.setRowSelectionInterval(remotefocusedRowIndex, remotefocusedRowIndex);

   	 		remoteFile = remoteTable.getValueAt(remotefocusedRowIndex,6).toString();
   	 		String downloadURL = (remoteFile.substring(0, remoteFile.lastIndexOf('/')+1));
   
   	 		remoteFilePopup.show(remoteTable, evt.getX(), evt.getY());
   	 	}else if(evt.getClickCount()==2) {
   	 		remotefocusedRowIndex = remoteTable.rowAtPoint(evt.getPoint());
   	 		if(remotefocusedRowIndex == -1) {
   	 			return;
   	 		}
   	 		mulucount++;
   	 		lastRemotePath = lastRemotePath+1;
   	 		remoteTable.setRowSelectionInterval(remotefocusedRowIndex, remotefocusedRowIndex);
   		 //�����˵�

   	 		pw.write("CWD "+remoteTable.getValueAt(remotefocusedRowIndex, 6).toString()+"\r\n");
   	 		pw.flush();
   	 		String listinfo = null;
   	 		pw.write("PASV\r\n");
   	 		pw.flush();
   	 		try {
   	 			Thread.sleep(1000);
   	 		} catch (InterruptedException e1) {
       		 // TODO Auto-generated catch block
   	 			e1.printStackTrace();
   	 		}
   	 		pw.write("LIST \r\n");
   	 		pw.flush();
       	 	Socket dataSocket;
       	 	try {
       	 		dataSocket = new Socket(remoteUrl.getHost(),ID);
       	 		InputStream inputStream = dataSocket.getInputStream();
       	 		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
       	 		//	commendSocket.shutdownOutput();//�ر������
       	 		int offset;  
       	 		byte[] bytes = new byte[1024];  
       	 		while ((offset = inputStream.read(bytes)) != -1) { 
       	 			outputStream.write(bytes, 0, offset);  
       	 		}  
       	 		listinfo = new String(outputStream.toByteArray());
       	 		inputStream.close();  
       	 		outputStream.close();  
       	 		dataSocket.close();
       	 	} catch (IOException e) {
       	 		// TODO Auto-generated catch block
       	 		e.printStackTrace();
       	 	}
       	 //�����ѡ�ڵ�
       	 	lastRemoteTreeRow = lastRemoteTreeRow+remotefocusedRowIndex+1;

       	 	Rectangle path = remoteTree.getRowBounds(lastRemoteTreeRow);
       	 	TreePath paths = remoteTree.getClosestPathForLocation(path.x, path.y); 
       	 	remoteTree.expandPath(paths);
       	 	remoteTree.setSelectionPath(paths);
       	 
       	 	updataRemoteTree(listinfo,(DefaultMutableTreeNode) paths.getLastPathComponent());
   	 	}
    }
    //����Զ���ļ�
    public void downloadFile(String localPath,String remoteFile) {
		try {
			OutputStream os = commendSocket.getOutputStream();//�ֽ������
			pw = new PrintWriter(os);//���������װΪ��ӡ��
			pw.write("PASV\r\n");
			pw.flush();
			//��ȡ�˿ں���Ҫʱ�䣬�������½��������ݵ��׽���ʱ�������߳���ͣ
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Socket dataSocket = new Socket(remoteUrl.getHost(),ID);
			//�����ļ�
			pw.write("RETR "+remoteFile+"\r\n");
			pw.flush();
			File localFile = new File(localPath+File.separatorChar+remoteFile);
			InputStream inputStream = dataSocket.getInputStream();
			FileOutputStream fileOutputStream = new FileOutputStream(localFile);

			int offset;  
	        byte[] bytes = new byte[1024];  
	        while ((offset = inputStream.read(bytes)) != -1) { 
	            fileOutputStream.write(bytes, 0, offset);
	            //�ۼƶ�ȡ���ֽڣ����ڽ���������ʾ
	            lengthHaveRead +=1024;
	           
	        }  
	        //�ر���
	        inputStream.close();  
	        fileOutputStream.close();  
	        dataSocket.close();  
	
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    //�ϴ������ļ�
    public void uploadFile(File localFile) {
    	try {
			//2.��ȡ��������������������Ϣ
			OutputStream os = commendSocket.getOutputStream();//�ֽ������
			pw = new PrintWriter(os);//���������װΪ��ӡ��
			//�����������ָ���ȡ���ݶ˿�
			pw.write("PASV\r\n");
			pw.flush();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Socket dataSocket = new Socket(remoteUrl.getHost(),ID);

			//�ϴ��ļ�
			pw.write("STOR "+localFile.getName()+"\r\n");
			pw.flush();
			
	        OutputStream outputStream = dataSocket.getOutputStream();  
	        FileInputStream fileInputStream = new FileInputStream(localFile);   
	        int offset;  
	        byte[] bytes = new byte[1024];  
	        while ((offset = fileInputStream.read(bytes)) != -1) {  

	            lengthHaveRead +=1024;
	            outputStream.write(bytes, 0, offset);  
	        }  
	        // �ϴ��ļ�����ƺ���  
	        outputStream.close();  
	        fileInputStream.close();  
	        dataSocket.close();  
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //���������
	public class ProgressBarDemo extends JPanel implements PropertyChangeListener{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private   JProgressBar progressBar;
		private Task task;
		class Task extends SwingWorker<Void,Void>{
			@Override
			public Void doInBackground() {
				setProgress(0);
			
				while(((lengthHaveRead/queueFileLength)*100+0.5)<100) {
				setProgress((int)((lengthHaveRead/queueFileLength*100+0.5)));
				}
				return null;
			}
		@Override
		public void done() {
			setProgress(100);
			progressBar.updateUI();
			Toolkit.getDefaultToolkit().beep();
			setCursor(null); 
			}
	}
		public  ProgressBarDemo() {
			super(new FlowLayout());
			progressBar = new JProgressBar();
			progressBar.setPreferredSize(new Dimension(500,25));
			progressBar.setValue(0);
			progressBar.setStringPainted(true);
			this.add(progressBar);
		}
		public void propertyChange(PropertyChangeEvent evt) {
			// TODO Auto-generated method stub
			if("progress"==evt.getPropertyName()) {
				int progress = (Integer)evt.getNewValue();
			progressBar.setValue(progress);

			}
		}
		public void run()
		{
			task = new Task();
			task.addPropertyChangeListener((PropertyChangeListener) this);
			task.execute();
		}

	}
	//�Զ�������
	public class MyTable extends JTable {//��дJTable��Ĺ��췽��
		/*
		 * �����࣬����̳���JTable���Ϊ���
		 */
			     
		public MyTable(Vector<Vector<String>>data,Vector<String> columnNames){//Vector rowData,Vector columnNames
			super(data,columnNames);//���ø���Ĺ��췽��
			         
		}
		//��дJTable���getTableHeader()����
			     
		public JTableHeader getTableHeader(){//������ͷ
			JTableHeader tableHeader=super.getTableHeader();//��ñ��ͷ����
			tableHeader.setReorderingAllowed(false);//���ñ���в�������
			DefaultTableCellRenderer hr=(DefaultTableCellRenderer)tableHeader
				.getDefaultRenderer();//��ñ��ͷ�ĵ�Ԫ�����
			hr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);//��������������ʾ
			return tableHeader;
		}
		//��дJtbale���getDefaultRenderer(Class<?>columnClass)����
		public TableCellRenderer getDCellRenderer(Class<?> columnClass){//���嵥Ԫ��
			DefaultTableCellRenderer cr=(DefaultTableCellRenderer)super
					.getDefaultRenderer(columnClass);//��ñ��ĵ�Ԫ�����
			cr.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);//���õ�Ԫ�����ݾ�����ʾ
			return cr;         
		}
		//��дJtable���isCellEditable(int row,int column)����
		public boolean isCellEditable(int row,int column){//��񲻿ɱ༭
			return false;
		}	     
	}

}
