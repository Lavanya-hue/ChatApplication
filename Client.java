import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class Client extends JFrame {

    Socket socket;
    BufferedReader br;
    PrintWriter out;

    //GUI Declare Component
    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);

    //Constructor

    public Client()
    {
        try {
            System.out.println("Sending request to server");
            socket = new Socket("127.0.0.1", 7778);
            System.err.println("Connection done");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();

            startReading();
            // startWriting();


        } catch (Exception e) {

        }

    }

    private void handleEvents() {

        messageInput.addKeyListener(new KeyListener() {
            
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //System.err.println("key released "+e.getKeyCode());
                if(e.getKeyCode()==10)
                {
                    //System.err.println("you have pressed enter button");
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me :"+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();         
                }
            }

        });
    }

    private void createGUI()
    {
        //GUI code..

        this.setTitle("Client Messanger[END]");
        this.setSize(500,500);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //coding for component
        //set font
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        //add icon and allignments
        heading.setIcon(new ImageIcon("Clogo.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        //set frame layout
        this.setLayout(new BorderLayout());
        
        //adding the components to frame
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);
        this.setVisible(true);
        
    }

    //Start reading [Method]

    public void startReading()
    {
        //thread creation to read the data

        Runnable r1 = () -> {

            System.out.println("reader started..");

            try {
                while (true) {
                    String msg = br.readLine();
                    if(msg == null || msg.equals("exit")) {
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this, "Server Terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    //System.out.println("Server : "+msg);
                    messageArea.append("Server : " + msg+"\n");

                } 
            } catch (IOException e) {
                System.out.println("Connection closed");
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(r1).start();

    }

    //Start writing [Method]

    public void startWriting()
    {
        //thread creation to write the data

        Runnable r2 = () -> {
            System.out.println("Writer started..");
            try {
                BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));

                while (!socket.isClosed()) {
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    
                    if (content.equals("exit")) {
                        socket.close();
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Connection closed");
            } finally {
                try {
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(r2).start();
    }



    public static void main(String[] args) {
        System.out.println("This is Client...");
        new Client();
    }
    
}
