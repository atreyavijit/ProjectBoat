/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectboat;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Vijit
 */
public class ProjectBoat extends JFrame implements ActionListener, KeyListener{

    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
    private final int WINDOW_WIDTH = dimension.width;
    private final int WINDOW_HEIGHT = 500;
    
    private static final int X_START = 75;
    private static final int Y_START = 260;
    private static final int POINT_SIZE_BIG = 25;
    private static final int POINT_SIZE_SMALL = 16;
    private static final int NUMBER_OF_OBJECTS = 4;
    
    private static final String BOAT_NAME = "VIJIT'S BOAT";
    private static final Font BIG_FONT = new Font("SansSerif", Font.PLAIN, POINT_SIZE_BIG);
    private static final Font SMALL_FONT = new Font("SansSerif", Font.BOLD, POINT_SIZE_SMALL);
    
    ArrayList<Integer> keysPressed = new ArrayList<>();
    int score = 0;
    int baseX = 0;
    int red;
    int green;
    int blue;
    int colorOffset = 1;
    String direction = "right";
    int speed = 10;
    boolean isPaused = false;
    int waterWaving = -1;
    int xObjects[] = {WINDOW_WIDTH/4 - 100, 2*WINDOW_WIDTH/4 - 100, 3*WINDOW_WIDTH/4 - 100, WINDOW_WIDTH - 100};
    int yObjects[] = {
        -100 - (int)(Math.random()*200), 
        -100 - (int)(Math.random()*200), 
        -100 - (int)(Math.random()*200), 
        -100 - (int)(Math.random()*200)
    };
    
    public static void main(String[] args) {
        ProjectBoat movingBoat = new ProjectBoat();
        movingBoat.setVisible(true);
    }
    
    public ProjectBoat(){
        super("Moving Boat");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width/2-this.getSize().width/2, dimension.height/2-this.getSize().height/2);
        setLayout(new BorderLayout());
        addKeyListener(this);
        setFocusable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(new Color(204,240,255));
        
        JPanel buttonPanel = new JPanel( );
        
        JButton btnSpeedUp = new JButton("Speed Up ↑");
        btnSpeedUp.setActionCommand("speedup");
        btnSpeedUp.addActionListener(this);
        btnSpeedUp.setFocusable(false);
        
        JButton btnSpeedDown = new JButton("Speed Down ↓");
        btnSpeedDown.setActionCommand("speeddown");
        btnSpeedDown.addActionListener(this);
        btnSpeedDown.setFocusable(false);
        
        JButton btnMoveLeft = new JButton("← Move Left");
        btnMoveLeft.setActionCommand("moveleft");
        btnMoveLeft.addActionListener(this);
        btnMoveLeft.setFocusable(false);
        
        JButton btnMoveRight = new JButton("Move Right →");
        btnMoveRight.setActionCommand("moveright");
        btnMoveRight.addActionListener(this);
        btnMoveRight.setFocusable(false);
        
        JButton btnPause = new JButton("Pause");
        btnPause.setActionCommand("pause");
        btnPause.addActionListener(this);
        btnPause.setFocusable(false);
        
        JButton btnResume = new JButton("Resume");
        btnResume.setActionCommand("resume");
        btnResume.addActionListener(this);
        btnResume.setFocusable(false);
        
        buttonPanel.add(btnSpeedDown);
        buttonPanel.add(btnSpeedUp);
        buttonPanel.add(btnMoveLeft);
        buttonPanel.add(btnMoveRight);
        buttonPanel.add(btnPause);
        buttonPanel.add(btnResume);
        
        add(buttonPanel,BorderLayout.SOUTH);
    }
    
    @Override
    public void paint(Graphics g){
        super.paint(g);
        drawScene(g);
        checkKeyPressed();
        if(baseX >= WINDOW_WIDTH - 300)
        {
            direction = "left";
        }
        if(baseX <= 0)
        {
            direction = "right";
        }
        if(direction.equals("left"))
        {
            baseX = baseX - speed;
        }
        else
        {
            baseX = baseX + speed;
        }
        try
        {
            Thread.sleep(200);
        }
        catch(InterruptedException ex)
        {
            System.out.println("An exception occurred:\n"+ex.getMessage());
        }
        if(!isPaused)
        {
            repaint(); 
        }
    }
    
    private void drawScene(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(2));
        
        randomizeColor();
        g.setColor(new Color(255,70,5));
        g.setFont(BIG_FONT);
        
        for(int i = 0; i < NUMBER_OF_OBJECTS; i++)
        {
            g.fillOval(xObjects[i],yObjects[i],50,50);
            yObjects[i] += 5;
            if(yObjects[i] >= 280)
            {
                score = score + speed;
                yObjects[i] = -100 - (int)(Math.random()*100);
            }
            if(xObjects[i] >= baseX - 40 && xObjects[i] <= baseX + 290 && yObjects[i] >= 150 && yObjects[i] <= 280)
            {
                 pause();
                 Thread t = new Thread(() -> {
                    int option = JOptionPane.showConfirmDialog(null,"Game Over\nYour score is: " + score+"\nDo you want to restart the game?","MovingBoat",JOptionPane.YES_NO_OPTION);
                    if(option == JOptionPane.YES_OPTION)
                    {
                        resetGame();
                        resume();
                    }
                    else
                        System.exit(0);
                });
                t.start();                
            }
        }
        
        g.setColor(new Color(red,green,blue));
        g.drawString(BOAT_NAME, baseX + X_START, Y_START);
        
        g.drawArc(baseX + 10, 100, 80, 200, 180,59);  
        g.drawArc(baseX + 210, 100, 80, 200,0,-59);
        
        g.drawLine(baseX + 100, 200, baseX + 200, 200);
        
        g.drawArc(baseX + 50,100,50,200,0,90);
        g.drawArc(baseX - 40,100,240,200,0,91);
        
        g.drawArc(baseX + 10, 190, 280, 20, 69, -317);
        
        //BINDING RECTANGLE FOR DETECTING COLLISION
        //g.drawRect(baseX+10,190,280,100);
        
        //g.drawArc(baseX + 50, 290, 200, 20, 180, 180);
        
        g.setColor(new Color(64,164,223));
        
        waterWaving *= -1;
        if(waterWaving == -1)
        {
            for(int i = 0; i <= WINDOW_WIDTH; i=i+60)
            {
                for(int j = 280; j <= WINDOW_HEIGHT -50; j = j+20)
                {
                    g.fillArc(i, j, 30, 10, 0, 180);
                    g.fillArc(i+30, j, 30, 10, 180, 180);
                }
            }
        }
        else
        {
            for(int i = 0; i <= WINDOW_WIDTH; i=i+60)
            {
                for(int j = 280; j <= WINDOW_HEIGHT -50; j = j+20)
                {
                    g.fillArc(i, j, 30, 10, 180, 180);
                    g.fillArc(i+30, j, 30, 10, 0, 180);
                }
            }
        }
        g.setFont(SMALL_FONT);
        g.drawString("Speed: " + speed, 50,WINDOW_HEIGHT - 10);
        g.drawString("Score: " + score, WINDOW_WIDTH -150,WINDOW_HEIGHT - 10);
        g.setColor(new Color(red,green, blue));
        AffineTransform at = new AffineTransform();
        at.setToRotation(Math.PI / 2.2);
        g2d.setTransform(at);
        int xoff = (int)(baseX * Math.cos(Math.PI/2.20));
        int yoff = (int)(baseX * Math.sin(Math.PI/2.20));
        g2d.drawString("ATREYA",130 + xoff, - 80 - yoff);
    }
    
    private void resetGame() {
        score = 0;
        baseX = 0;
        direction = "right";
        speed = 10;
        isPaused = false;
        waterWaving = -1;
        for(int i = 0; i < NUMBER_OF_OBJECTS; i++)
        {
            yObjects[i] = -100 - (int)(Math.random()*100);
        }
    }

    private void speedUp() {
        if(speed >= 30)
        {
            return;
        }
        speed += 1;
    }
    
    private void speedDown() {
        if(speed == 1)
        {
            return;
        }
        speed -= 1;
    }
    
    private void moveLeft() {
        if(direction.equals("right"))
            {
                direction = "left";
                return;
            }
    }

    private void moveRight() {
        if(direction.equals("left"))
            {
                direction = "right";
                return;
            }
    }
    
    private void pause() {
        isPaused = true;
    }

    private void resume() {
        isPaused = false;
            repaint();
    }
    
    private void randomizeColor() {
        red = (int)(Math.random()*255);
        green = (int)(Math.random()*255);
        blue = (int)(Math.random()*255);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if(actionCommand.equals("speedup"))
        {
            speedUp();
        }
        else if(actionCommand.equals("speeddown"))
        {            
           speedDown();
        }
        else if(actionCommand.equals("pause"))
        {
           pause();
        }
        else if(actionCommand.equals("resume"))
        {
            resume();
        }
        else if(actionCommand.equals("moveleft"))
        {
            moveLeft();
        }
        else if(actionCommand.equals("moveright"))
        {
            moveRight();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }
    @Override
    public void keyPressed(KeyEvent e) {
        keysPressed.add(e.getKeyCode());
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void checkKeyPressed(){
        for(int i = 0; i < keysPressed.size(); i++)
        {
            int c = keysPressed.get(i);
            switch(c)
            {
                case 37:
                    moveLeft();
                    break;
                case 38:
                    speedUp();
                    break;
                case 39:
                    moveRight();
                    break;
                case 40:
                    speedDown();
                    break;
            }
            keysPressed.remove(i);
        }
    }
}
