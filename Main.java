package Shootup;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

/*
 * working values:
 * Impuls = 0.07
 * gravity = 0.5
 * frameTime = 20
 * reboundCoefficient = 0.8
 */

public class Main{

    static JFrame gameWindow;
    static JLabel player;
    static double yImpuls = 0;
    static double xImpuls = 0;
    static int x;
    static int y;
    static double xVelocity = 0;
    static double yVelocity = 0;
    static double gravity = 0.5;
    static double reboundCoefficient = 0.8; //bouncieness
    static int frameTime = 20;
    static boolean debugMode = true;
    static JTextArea infos;
    static JTextField tf1 = new JTextField();
    static ArrayList<JLabel> platforms = new ArrayList<JLabel>();
    static int coins = 0;
    static JLabel coinLabel = new JLabel("Coins: " + coins);
    static boolean checkedLastFrame = false;
    public static void main(String[] args){
        gameWindow = openFrame(debugMode);
        startGame(gameWindow);
    }

    public static void startGame(JFrame gameWindow){
        x = player.getX();
        y = player.getY();
        while(true){
            performBoundChecks();
            performCollisionChecks();
            doPlayerMovement();
            player.setBounds(x, y, 50, 50);
            wait(frameTime);
            if(debugMode){
                updateDebug();
                try{
                    frameTime = Integer.parseInt(tf1.getText());
                }catch(NumberFormatException e){
                    frameTime = 20;
                }
            }
        }
    }

    public static JFrame openFrame(boolean debugMode){
        gameWindow = new JFrame();
        gameWindow.setLayout(null);
        gameWindow.setSize(800,800);
        gameWindow.setResizable(false);
        gameWindow.getContentPane().setBackground(Color.lightGray);
        player = getPlayer();
        gameWindow.add(player);
        gameWindow.add(coinLabel);
        coinLabel.setBackground(Color.MAGENTA);
        coinLabel.setBounds(100, 700, 125, 50);
        coinLabel.setFont(new Font(coinLabel.getFont().getName(), Font.PLAIN, 20));
        addListener(gameWindow);
        //JLabel ground = getGround();
        //gameWindow.add(ground);
        //ground.setBounds(0,675,800,100);
        addPlatforms(3);
        
        if(debugMode){
            infos = new JTextArea();
            infos.setBounds(0, 0, 100, 80);
            updateDebug();
            infos.setVisible(true);
            infos.setOpaque(false);
            gameWindow.add(infos);
            gameWindow.add(tf1);
            tf1.setBounds(0, 70, 100, 25);
        }
        
        gameWindow.setVisible(true);
        return gameWindow;
    }

    public static JLabel getGround(){
        JLabel ground = new JLabel(new ImageIcon("Shootup\\ground.png"));
        ground.setPreferredSize(new Dimension(800, 50));
        ground.setBackground(Color.lightGray);
        ground.setOpaque(true);
        ground.setVisible(true);
        return ground;
    }

    public static JLabel getPlayer(){
        player = new JLabel();
        player.setBackground(Color.blue);
        player.setPreferredSize(new Dimension(50, 50));
        player.setMaximumSize(new Dimension(50, 50));
        player.setBorder(new LineBorder(Color.black));
        player.setOpaque(true);
        player.setVisible(true);
        return player;
    }

    public static JLabel getPlatform(int pX, int pY, int pW, int pH){
        JLabel platform = new JLabel();
        platform.setBackground(Color.green);
        platform.setBounds(pX, pY, pW, pH);
        platform.setBorder(new LineBorder(Color.black));
        platform.setOpaque(true);
        platform.setVisible(true);
        platforms.add(platform);
        return platform;
    }

    public static void addPlatforms(int i){
        int sizeX = gameWindow.getWidth()-300;
        int sizeY = gameWindow.getHeight()-150;
        Random random = new Random();
        JLabel platform;
        for(; i > 0; i--){
            int pX = 50*(random.nextInt(sizeX/50)+1)+50;
            int pY = 50*(random.nextInt(sizeY/50)+1);
            int pW = 50*(random.nextInt(3)+1);
            int pH = 50*(random.nextInt(2)+1);
            platform = getPlatform(pX, pY, pW, pH);
            gameWindow.add(platform);
        }
        
    }

    public static void doPlayerMovement(){
        y += yVelocity;
        yVelocity = yVelocity + gravity - yImpuls;
        yImpuls = 0;
        x += xVelocity;
        xVelocity = xVelocity - xImpuls;
        xImpuls = 0;
    }

    public static void performBoundChecks(){
        if(y>670){
            yVelocity = -reboundCoefficient*(yVelocity-5*gravity);
            y = 650;
        }
        if(x>700) {
            xVelocity = -reboundCoefficient*xVelocity;
            x = 700;
        }
        if(x<100) {
            xVelocity = -reboundCoefficient*xVelocity;
            x = 100;
        }
    }

    public static void performCollisionChecks(){
        Rectangle playerBounds = player.getBounds();
        Rectangle platformBounds;
        for(JLabel platform : platforms){
            if(platform.getBounds().intersects(playerBounds)){
                platformBounds = platform.getBounds();
                Rectangle overlap = playerBounds.intersection(platformBounds);
                if(overlap.width < overlap.height){
                    player.setBackground(Color.green);
                    xVelocity *= -reboundCoefficient;
                    if((x == overlap.x)){
                        x = platformBounds.x + platformBounds.width;
                    }else{
                        x = platformBounds.x - 50;
                    }
                }else{
                    player.setBackground(Color.red);
                    yVelocity *= -reboundCoefficient;
                    if((y == overlap.y)){
                        y = platformBounds.y + platformBounds.height;
                    }else{
                        y = platformBounds.y - 50;
                    }
                }
            }
        }
    }

    public static void updateDebug(){
        infos.setText("x: "+ x + "\ny: " + y + "\nxVelocity: " + xVelocity + "\nyVelocity: " + yVelocity);
    }

    public static void wait(int ms){
        try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public static void addListener(JFrame frame){
        frame.addMouseListener(new MouseListener(){

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int x = player.getX();
                int y = player.getY();
                if(y < 0) return;
                yImpuls += 0.07*(e.getY() - y);
                xImpuls += 0.07*(e.getX() - x);
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
            }
        });
    }
}