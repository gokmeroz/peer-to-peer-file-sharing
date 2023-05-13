import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    static ArrayList<MyFile> myFiles=new ArrayList<>();
    public static void main(String[] args) throws IOException {

        int fileId=0;
        JFrame jFrame=new JFrame("Kaybedenler Kulübü's Server");
        jFrame.setSize(400,400);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(),BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel=new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));

        JScrollPane jScrollPane=new JScrollPane();
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel jTitle=new JLabel("Kaybedenler Kulubü's File Receiver");
        jTitle.setFont(new Font("Arial",Font.BOLD,20));
        jTitle.setBorder(new EmptyBorder(20,0,10,0));
        jTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        jFrame.add(jTitle);
        jFrame.add(jScrollPane);
        jFrame.setVisible(true);

        ServerSocket serverSocket=new ServerSocket(1234);
        while (true){
            try {
                Socket socket=serverSocket.accept();
                DataInputStream dataInputStream=new DataInputStream(socket.getInputStream());

                int fileNameLength=dataInputStream.readInt();
                if(fileNameLength>0){
                    byte[] fileNameBytes=new byte[fileNameLength];
                    dataInputStream.readFully(fileNameBytes,0,fileNameBytes.length);
                    String fileName=new String(fileNameBytes);

                    int fileContentLength=dataInputStream.readInt();
                    if (fileContentLength>0){
                        byte[] fileContentBytes=new byte[fileContentLength];
                        dataInputStream.readFully(fileContentBytes,0,fileContentLength);

                        JPanel jPFileRow=new JPanel();
                        jPFileRow.setLayout(new BoxLayout(jPFileRow,BoxLayout.Y_AXIS));

                        JLabel jfileName=new JLabel(fileName);
                        jfileName.setFont(new Font("Arial",Font.BOLD,20));
                        jfileName.setBorder(new EmptyBorder(10,0,10,0));
                        jfileName.setAlignmentX(Component.CENTER_ALIGNMENT);

                        if(getFileExtension(fileName).equalsIgnoreCase("txt")){
                            jPFileRow.setName(String.valueOf(fileId));
                            jPFileRow.addMouseListener(getMymouseListener());

                            jPFileRow.add(jfileName);
                            jPanel.add(jPFileRow);
                            jFrame.validate();
                        }
                        else {
                            jPFileRow.setName(String.valueOf(fileId));
                            jPFileRow.addMouseListener(getMymouseListener());

                            jPFileRow.add(jfileName);
                            jPanel.add(jPFileRow);
                            jFrame.validate();
                        }

                    }
                    myFiles.add(new MyFile(fileId,fileName,fileNameBytes,getFileExtension(fileName)));
                    fileId++;
                }
            }
            catch (IOException error){
                error.printStackTrace();
            }
        }

    }
    public static MouseListener getMymouseListener(){
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel jPanel=(JPanel) e.getSource();
                int fileId=Integer.parseInt(jPanel.getName());
                for(MyFile myFile:myFiles){
                    if(myFile.getId()==fileId){
                        JFrame jfPreview=createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                        jfPreview.setVisible(true);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
    }
    public static JFrame createFrame(String fileName,byte[] FileData,String fileExtension){
        JFrame jFrame=new JFrame("Kaybedenler Kulubü's File Downloader");
        jFrame.setSize(400,400);

        JPanel jPanel=new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));

        JLabel jTitle=new JLabel("Kaybedenler Kulubü's File Downloader");
        jTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        jTitle.setFont(new Font("Arial",Font.BOLD,20));
        jTitle.setBorder(new EmptyBorder(20,0,10,0));

        JLabel jLabelPrompt=new JLabel("Are you sure you want to download "+fileExtension);
        jLabelPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        jLabelPrompt.setFont(new Font("Arial",Font.BOLD,20));
        jLabelPrompt.setBorder(new EmptyBorder(20,0,10,0));

        JButton jBYes=new JButton("Yes");
        jBYes.setPreferredSize(new Dimension(150,150));
        jBYes.setFont(new Font("Arial",Font.BOLD,20));

        JButton jBNo=new JButton("No");
        jBNo.setPreferredSize(new Dimension(150,150));
        jBNo.setFont(new Font("Arial",Font.BOLD,20));


        JLabel jBFileContent=new JLabel();
        jBFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jPButtons=new JPanel();
        jPButtons.setBorder(new EmptyBorder(20,0,10,0));
        jPButtons.add(jBYes);
        jPButtons.add(jBNo);

        if(fileExtension.equalsIgnoreCase("txt")){
            jBFileContent.setText("<html>"+new String(FileData)+"</html>");
        }else{
            jBFileContent.setIcon(new ImageIcon(FileData));
        }


        jBYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File fileToDownload=new File(fileName);
                try{
                    FileOutputStream fileOutputStream=new FileOutputStream(fileToDownload);
                    fileOutputStream.write(FileData);
                    fileOutputStream.close();

                    jFrame.dispose();
                }catch (IOException error){
                    error.printStackTrace();
                }
            }
        });

        jBNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrame.dispose();
            }
        });

        jPanel.add(jTitle);
        jPanel.add(jLabelPrompt);
        jPanel.add(jBFileContent);
        jPanel.add(jPButtons);

        jFrame.add(jPanel);
        return jFrame;



    }
    public static String getFileExtension(String fileName){
        int i=fileName.lastIndexOf(' ');
        if(i>0){
            return fileName.substring(i+1);
        }else {
            return "No extensions found!";
        }
    }
}
