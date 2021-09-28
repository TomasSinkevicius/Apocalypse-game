
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class ClientServer extends JFrame {
    
    private int width, height;
    private Container contentPane;
    private PlayerSprite p1;
    private PlayerSprite p2;
    private DrawingComponent dc;
    private Timer animationTimer;
    private boolean up, down, left, right;
    private Socket socket;
    private int playerID;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;
    
    public ClientServer(int w, int h){
        width = w;
        height = h;
        up = false;
        down = false;
        left = false;
        right = false;
    }
    
    public void setUpGUI(){
        contentPane = this.getContentPane();
        this.setTitle("Player #" + playerID);
        contentPane.setPreferredSize(new Dimension(width, height));
        createSprites();
        dc = new DrawingComponent();
        contentPane.add(dc);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        
        setUpAnimationTimer();
        setUpKeyListener();
    }
    
    private void createSprites(){
        if(playerID == 1){
            p1 = new PlayerSprite(100, 400, 50, Color.GREEN); 
            p2 =  new PlayerSprite(600, 400, 50, Color.BLACK); 
        }else{
            p2 = new PlayerSprite(100, 400, 50, Color.GREEN); 
            p1 =  new PlayerSprite(600, 400, 50, Color.BLACK); 
        }          
        
    }
    
    private void setUpAnimationTimer(){
        int interval = 10;
        ActionListener al = new ActionListener(){
            public void actionPerformed(ActionEvent ar){
                double speed = 5;
                if(up){
                    p1.moveV(-speed);
                }else if(down){
                    p1.moveV(speed);
                }else if(left){
                    p1.moveH(-speed);
                }else if(right){
                    p1.moveH(speed);
                }
                dc.repaint();              
            }
        };
        animationTimer = new Timer(interval, al);
        animationTimer.start();
    }
    
    private void setUpKeyListener(){
        KeyListener kl = new KeyListener(){
            public void keyTyped(KeyEvent ke){
                
            }
            
            public void keyPressed(KeyEvent ke){
                int keyCode = ke.getKeyCode();
                
                switch(keyCode){
                    case KeyEvent.VK_UP :
                        up = true;
                        break;
                    case KeyEvent.VK_DOWN :
                        down = true;
                        break;
                    case KeyEvent.VK_RIGHT :
                        right = true;
                        break;
                    case KeyEvent.VK_LEFT :
                        left = true;
                        break;
                }
            }
            
            public void keyReleased(KeyEvent ke){
                int keyCode = ke.getKeyCode();
                
                switch(keyCode){
                    case KeyEvent.VK_UP :
                        up = false;
                        break;
                    case KeyEvent.VK_DOWN :
                        down = false;
                        break;
                    case KeyEvent.VK_RIGHT :
                        right = false;
                        break;
                    case KeyEvent.VK_LEFT :
                        left = false;
                        break;
                }
            }
        };
        contentPane.addKeyListener(kl);
        contentPane.setFocusable(true);
    }
    
    private void connectToServer(){
        try{
            socket = new Socket("localhost", 45371);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            playerID = in.readInt();
            System.out.println("You are player#" + playerID);
            if(playerID == 1){
                System.out.println("Waiting for Player #2 to connect...");
            }
            rfsRunnable = new ReadFromServer(in);
            wtsRunnable = new WriteToServer(out);
            rfsRunnable.waitForStartMsg();
        }catch(IOException e){
            System.out.println("IOException from connectToServer()");
        }
    }
    private class DrawingComponent extends JComponent{
        
        protected void paintComponent(Graphics g){
            Graphics2D g2d = (Graphics2D) g;
            p2.drawSprite(g2d);
            p1.drawSprite(g2d);           
        }
    }
    
    private class ReadFromServer implements Runnable{
        
        private DataInputStream dataIn;
        
        public ReadFromServer(DataInputStream in){
            dataIn = in;
            System.out.println("RFS Runnable created");
        }
        public void run(){
            try{
                while(true){
                    if(p2 != null){
                        p2.setX(dataIn.readDouble());
                        p2.setY(dataIn.readDouble());
                    }
                }
            }catch(IOException e){
                System.out.println("IOException from RFS run()");
            }
        }
        
        public void waitForStartMsg(){
            try{
                String startMsg = dataIn.readUTF();
                System.out.println("Message from server:" + startMsg);
                Thread readThread = new Thread(rfsRunnable);
                Thread writeThread = new Thread(wtsRunnable);
                readThread.start();
                writeThread.start();
            }catch(IOException e){
                System.out.println("IOException from waitForStartMsg()");
            }
        }
    }
    
    
    private class WriteToServer implements Runnable{
        
        private DataOutputStream dataOut;
        
        public WriteToServer(DataOutputStream out){
            dataOut = out;
            System.out.println("WTS Runnable created");
        }
        public void run(){
            try{
                
                while(true){
                    if(p1 != null){
                        dataOut.writeDouble(p1.getX());
                        dataOut.writeDouble(p1.getY());
                        dataOut.flush();
                }                   
                try{
                    Thread.sleep(25);
                }catch(InterruptedException e){
                    System.out.println("InterruptedException from WTS run()");
                }
            }
            }catch(IOException e){
                System.out.println("IOException from WTS run()");
            }
        }
    }
    
  
    public static void main(String[] args){
        ClientServer pf = new ClientServer(800, 600);
        pf.connectToServer();
        pf.setUpGUI();
    }
}
