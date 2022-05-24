package view;

import controller.ClickController;
import controller.GameController;
import dao.DAO;
import strategy.AIStrategy;
import strategy.GreedStrategy;
import strategy.RandomStrategy;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.plaf.LabelUI;
import javax.swing.text.AbstractDocument.Content;

import activity.ImageManager;
import activity.MusicRunnable;
import activity.Settings;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 */
public class ChessGameFrame extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            ChessGameFrame s= new ChessGameFrame(1000, 780);
            s.setVisible(true);
            s.action(0);
        });
    }

    //    public final Dimension FRAME_SIZE ;
    protected final int WIDTH;                     //整个界面的大小
    protected  final int HEIGHT;
    public final int CHESSBOARD_SIZE;            //棋盘的大小
    protected GameController gameController=null;

    protected Chessboard chessboard=null;

    JLabel bgLabel; //背景图片标签
    JLabel statusLabel;

    //按钮设置。也就是说按钮全局
    JButton resetButton;    //重新开始游戏
    JButton regretButton;
    JButton loadButton;
    JButton saveButton;

    //
    JPanel myContentPanel=null;

    public void action(int kind){
        
        //播放背景音乐，并
        if(Settings.bgOn){
            MusicRunnable.BG_SOUND.setIfCycle(true);
            MusicRunnable.BG_SOUND.setIfBreak(false);
            new Thread(MusicRunnable.BG_SOUND).start();
        }
        //添加背景图片
        if(Settings.gameGraphKind==0){
            System.out.println("使用背景图片一");
            bgLabel.setIcon(new ImageIcon(ImageManager.BACKGROUND_IMAGE));
            bgLabel.repaint();
        }else if(Settings.gameGraphKind==1){
            System.out.println("使用背景图片二");
            bgLabel.setIcon(new ImageIcon(ImageManager.BACKGROUND_IMAGE2));
            bgLabel.repaint();
        }
        else {
            System.out.println("使用背景图片三");
            bgLabel.setIcon(new ImageIcon(ImageManager.BACKGROUND_IMAGE3));
        }
        statusLabel.repaint();
        resetButton.repaint();
        loadButton.repaint();
        saveButton.repaint();
        //右边的各个按钮
        startGame(kind);
        this.setVisible(true);
    }





    //根据游戏类型来进行游戏
    public void startGame(int kind){
        System.out.println("开始游戏");
        switch(kind){
            case 0:
                addChessboard();
                break;
            case 1:
                addAIChessboard(new RandomStrategy());
                break;
            case 2:
                addAIChessboard(new GreedStrategy());
                break;
            default:
        }
        System.out.println("第"+kind);
    }



    public ChessGameFrame(int width, int height) {
       
        setTitle("2022 CS102A Project Demo"); //设置标题
        this.WIDTH = width;
        this.HEIGHT = height;
        this.CHESSBOARD_SIZE = HEIGHT * 4 / 5;

       
        setTitle("2022 CS102A Project Demo"); //设置标题
        setBounds(200,100,WIDTH,HEIGHT);
        setVisible(false);

        setLayout(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了

        myContentPanel=(JPanel)getContentPane();
        myContentPanel.setBounds(0,0,this.WIDTH,this.HEIGHT);
        myContentPanel.setLayout(null);
        // myContentPanel.setOpaque(true);

        //添加背景图片
        addBGImage();


        // //右边的各个按钮
        addChessboard();

        addLabel();
        addRegretButton();  
        addLoadButton();
        addResetButton();
        addStoreButton();

        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                // TODO Auto-generated method stub
                super.windowClosing(e);
                //设置关闭事件
                DAO.fputAccounts();
                //关闭背景音乐
                MusicRunnable.BG_SOUND.setIfBreak(true);
                MusicRunnable.BG_SOUND.setIfCycle(false);
            }
        });
        

    }

    //添加背景图片标签
    public void addBGImage(){
        bgLabel = new JLabel(new ImageIcon(ImageManager.BACKGROUND_IMAGE));
        bgLabel .setBounds(0, 0, WIDTH,HEIGHT);
        this.getLayeredPane().add(bgLabel);
    }



    /**
     * 在游戏面板中添加棋盘
     */
    public void addChessboard() {
        SwingUtilities.invokeLater(()->{
            System.out.println("添加普通棋盘");
            if(chessboard!=null) myContentPanel.remove(chessboard);
            chessboard = new Chessboard(CHESSBOARD_SIZE, CHESSBOARD_SIZE);
            gameController = new GameController(chessboard);
            chessboard.setLocation(HEIGHT / 10, HEIGHT / 10);
            myContentPanel.add(chessboard);
            chessboard.repaint();
        });
    }

    public void addAIChessboard(AIStrategy strategy){
        SwingUtilities.invokeLater(()->{
            System.out.println("添加ai棋盘");
            if(chessboard!=null) myContentPanel.remove(chessboard);
            chessboard = new Chessboard(CHESSBOARD_SIZE, CHESSBOARD_SIZE,strategy);
            gameController = new GameController(chessboard);
            chessboard.setLocation(HEIGHT / 10, HEIGHT / 10);
            myContentPanel.add(chessboard);
            chessboard.repaint();
        });
    
    }
   

   //TODO 该标签用于显示回合数
    public void addLabel() {
        SwingUtilities.invokeLater(()->{
            String a="RoundTime: "+gameController.getRoundTime();
            statusLabel = new JLabel(a);
            statusLabel.setLocation(HEIGHT, HEIGHT / 10);
            statusLabel.setSize(200, 60);
            statusLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
            myContentPanel.add(statusLabel);
            statusLabel.repaint();
        });
        // 增加一个线程一直计时！并更新游戏标签,直到游戏结束
        Runnable runnable=()->{
            while(!chessboard.getGameEnded()){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                statusLabel.setText(chessboard.getCurrentColor().getName()+":"+gameController.getRoundTime());
            }
        };
        new Thread(runnable).start();
        
    }



    /**
     * 在游戏面板中增加一个按钮，如果按下的话就会弹出一个输入窗口！让你输入悔棋步数
     */

    //悔棋按钮
    private void addRegretButton() {
        SwingUtilities.invokeLater(()->{
            JButton button = new JButton("regret");
            JFrame jFrame=this; //获得当前窗口的引用
            button.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    //悔棋操作
                    String string=JOptionPane.showInputDialog(jFrame,"请输入想悔棋的步数：" ,"悔棋弹窗！",JOptionPane.PLAIN_MESSAGE);
                    if(string==null){
                        return ;
                    }
                    try {
                        int n=Integer.parseInt(string);
                        if(!gameController.regretGame(n)){
                            //如果悔棋不成功！
                            JOptionPane.showMessageDialog(jFrame, "悔棋失败！请输入合理的数字！","提示窗口" , JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception exeception) {
                        //悔棋失败！可能是参数输入错误！
                        JOptionPane.showMessageDialog(jFrame, "悔棋失败！请输入合理的数字！","提示窗口" , JOptionPane.ERROR_MESSAGE);
                        //TODO: handle exception
                    }
                }
            });
            
            if(regretButton!=null) remove(regretButton);
            regretButton=button;

            button.setLocation(HEIGHT, HEIGHT / 10 + 120);
            button.setSize(200, 60);
            button.setFont(new Font("Rockwell", Font.BOLD, 20));
            myContentPanel.add(button);
            button.repaint();
        });
    }


    //载入游戏按钮！
    private void addLoadButton() {
        SwingUtilities.invokeLater(()->{
            JButton button = new JButton("Load");
            button.setLocation(HEIGHT, HEIGHT / 10 + 240);
            button.setSize(200, 60);
            button.setFont(new Font("Rockwell", Font.BOLD, 20));
            myContentPanel.add(button);
            JFrame jFrame=this;
            //在Button中添加内部类实现
            //TODO 实现加载游戏！
            button.addActionListener(e -> {
                System.out.println("Click load");
                try {
                    if(!gameController.loadGameFromFile(this)){
                        //如果载入游戏失败了！
                        JOptionPane.showMessageDialog(this, "棋局文件错误！载入棋局失败！","提示窗口",JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            });
            if(loadButton!=null) myContentPanel.remove(loadButton);
            loadButton=button;

            button.repaint();
        });
        
    }


    //重新开始！
    private void addResetButton(){
        SwingUtilities.invokeLater(()->{
            JButton button=new JButton("Reset");
            button.setLocation(HEIGHT,HEIGHT / 10+360);
            button.setSize(200,60);
            button.setFont(new Font("Rockwell", Font.BOLD, 20));
            myContentPanel.add(button);
            //TODO,重启游戏
            button.addActionListener(e -> {
                System.out.println("Reset chessBoard");
                gameController.resetGame();
                Runnable runnable=()->{
                    while(!chessboard.getGameEnded()){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        statusLabel.setText(chessboard.getCurrentColor().getName()+" :"+gameController.getRoundTime());
                    }
                };
                new Thread(runnable).start();
            });
            if(resetButton!=null) myContentPanel.remove(resetButton);
            resetButton=button;
            button.repaint();
        });
    }


    //保存棋局按钮！
    private void addStoreButton(){
        SwingUtilities.invokeLater(()->{
            JButton button=new JButton("Save");
            button.setLocation(HEIGHT,HEIGHT / 10+480);
            button.setSize(200,60);
            button.setFont(new Font("Rockwell", Font.BOLD, 20));
            myContentPanel.add(button);
            JFrame thisJFrame=this;
            //TODO,重启游戏
            button.addActionListener(e -> {
                System.out.println("save");
                gameController.storeGame(thisJFrame);

            });
            if(saveButton!=null) myContentPanel.remove( saveButton);
            saveButton=button;
            button.repaint();
        });

    }



}

