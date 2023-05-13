import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {

        final  File[] fileToSend=new File[1];
        JFrame jFrame=new JFrame("Mert's Client");
        jFrame.setSize(450,450);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(),BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel JLtitle=new JLabel("Kaybedenler Kulübü's File Sender");
        JLtitle.setFont(new Font("Arial", Font.BOLD,25));
        JLtitle.setBorder(new EmptyBorder(20,0,0,10));
        JLtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jlfileName=new JLabel("Chose file to send");
        jlfileName.setFont(new Font("Arial", Font.BOLD,20));
        jlfileName.setBorder(new EmptyBorder(20,0,0,10));
        jlfileName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpbutton=new JPanel();
        jpbutton.setBorder(new EmptyBorder(75,0,10,0));

        JButton jbsend=new JButton("Send File");
        jbsend.setPreferredSize(new Dimension(150,150));
        jbsend.setFont(new Font("Arial",Font.BOLD,20));

        JButton jbChoose=new JButton("Choose File");
        jbChoose.setPreferredSize(new Dimension(150,150));
        jbChoose.setFont(new Font("Arial",Font.BOLD,20));

        jpbutton.add(jbsend);
        jpbutton.add(jbChoose);

        jbChoose.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser=new JFileChooser();
                jFileChooser.setDialogTitle("Choose a file to send");

                if (jFileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
                    fileToSend[0]=jFileChooser.getSelectedFile();
                    jlfileName.setText("The file you want to send is: "+fileToSend[0].getName());


                }
            }
        });
        jbsend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(fileToSend[0]==null){
                    jlfileName.setText("Please choose a file first ");
                }
                else {
                    try {

                        FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
                        Socket socket = new Socket("localhost", 1234);
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

                        String filename = fileToSend[0].getName();
                        byte[] fileNameBytes = filename.getBytes();

                        byte[] fileContentBytes = new byte[(int) fileToSend[0].length()];
                        fileInputStream.read(fileContentBytes);

                        dataOutputStream.writeInt(fileNameBytes.length);
                        dataOutputStream.write(fileNameBytes);

                        dataOutputStream.write(fileContentBytes.length);

                        dataOutputStream.write(fileContentBytes);
                    }catch (IOException error){
                        error.printStackTrace();

                    }
                }
            }
        });

        jFrame.add(JLtitle);
        jFrame.add(jlfileName);
        jFrame.add(jpbutton);
        jFrame.setVisible(true);



    }
}
