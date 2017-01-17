package reversi;

import java.awt.BorderLayout;
import java.awt.Graphics;

import javax.swing.JFrame;

import java.awt.*;

import javax.swing.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;

import javax.imageio.*; 

class Screen extends JPanel{
	Reversi mainPanel=new Reversi();
	static JLabel text;
	
	public Screen(){
		setLayout(new BorderLayout());
		text=new JLabel("Let's start Reversi!");
		add(mainPanel,BorderLayout.CENTER);
		add(text,BorderLayout.SOUTH);
	}
}

public class Reversi extends JPanel implements MouseListener{
	static int[][] disks= new int[8][8]; // -1なら黒、1なら白、0ならどちらも置かれていない
	static boolean[][] placeability=new boolean[8][8];
	boolean yourTurn=false; // trueならプレイヤーのターン
	boolean pass=false;
	static int color=-1; // -1なら現在黒のターン
	int blackDisks=0;
	int whiteDisks=0;

	Image board;
	Image black,white;
	
	public Reversi(){
		setLayout(new GridLayout(8,8));
		try{board= ImageIO.read(new File("src/reversi/image/back1.gif"));
		}catch(IOException e){
			System.out.println("Error!");
			board=null;
		}
		try{black= ImageIO.read(new File("src/reversi/image/black.png"));
		}catch(IOException e){
			System.out.println("Error!");
			black=null;
		}
		try{white= ImageIO.read(new File("src/reversi/image/white.png"));
		}catch(IOException e){
			System.out.println("Error!");
			white=null;
		}
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++)
				disks[i][j]=0;
		disks[3][3]=1;
		disks[4][4]=1;
		disks[3][4]=-1;
		disks[4][3]=-1;
		blackDisks=2;
		whiteDisks=2;


		setPreferredSize(new Dimension(640,640));
		invest();
		addMouseListener(this);
	}
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawImage(board,0,0,this);
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++)
				switch(disks[i][j]){
				case -1:
					g.drawImage(black,i*80,j*80,this);
					break;
				case 1:
					g.drawImage(white,i*80,j*80,this);
					break;
				}
		
				
	}

	public void mouseClicked(MouseEvent e){}
	public void mousePressed(MouseEvent e) {
		int x, y;
		if (yourTurn) {
			x = e.getX() / 80;
			y = e.getY() / 80;
		} else {
			int[] coordinate;
			MonteCarlo m = new MonteCarlo();
			coordinate = m.suggestion();

			x = coordinate[0];
			y = coordinate[1];


		}

		if (x < 0)
			x = 0;
		if (x > 7)
			x = 7;
		if (y < 0)
			y = 0;
		if (y > 7)
			y = 7;

		if (pass) {
			color *= -1;
			//yourTurn = !yourTurn;
			invest();
			if (pass) {
				judge();
				pass = false;
			} else {
				if (color == -1)
					Screen.text.setText("Black's Turn. Black:" + blackDisks + " White:" + whiteDisks);
				else
					Screen.text.setText("White's Turn. Black:" + blackDisks + " White:" + whiteDisks);
			}
		} else if (placeability[x][y]) {
			place(x, y);
			color *= -1;
			//yourTurn = !yourTurn;
			pass = true;
			invest();
			if (pass) {
				if (color == -1)
					Screen.text.setText("Black can't place a disk.");
				else
					Screen.text.setText("White can't place a disk.");
			} else {
				if (color == -1)
					Screen.text.setText("Black's Turn. Black:" + blackDisks + " White:" + whiteDisks);
				else
					Screen.text.setText("White's Turn. Black:" + blackDisks + " White:" + whiteDisks);
			}
			repaint();
		}

	}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}

	/** クリックされた座標(x,y)に石を置き、ひっくり返すメソッド */
	void place(int x,int y){
		/** 手番に応じて石を置く */
		if(color==-1) {
			disks[x][y] = -1;
			blackDisks++;
		}
		else {
			disks[x][y] = 1;
			whiteDisks++;
		}

		/** 8方向に対してflipメソッドを呼び出し、ひっくり返す */
		flip(x-1,y-1,-1,-1);
		flip(x-1,y,-1,0);
		flip(x-1,y+1,-1,1);
		flip(x,y-1,0,-1);
		flip(x,y+1,0,1);
		flip(x+1,y-1,1,-1);
		flip(x+1,y,1,0);
		flip(x+1,y+1,1,1);
	}

	/** 自分の石ではさんだ相手の石をひっくり返すメソッド
	 *  x,yは座標、dx,dyは方向(dx=dy=-1なら左上方向についてひっくり返せるか調べている) */
	boolean flip(int x,int y,int dx,int dy) {
		/** (x,y)の石を見る。(x,y)が盤面の外ならfalse(ひっくり返せない)を返す */
		if (x == -1 || y == -1 || x == 8 || y == 8)
			return false;

		if(disks[x][y]==0)
			return false;
		else {
			if (disks[x][y] == color)
				return true;
			else {
				if (flip(x + dx, y + dy, dx, dy)) {
					disks[x][y] = color;
					blackDisks -= color;
					whiteDisks += color;
					return true;
				} else
					return false;
			}
		}
	}

	void invest(){
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++){
				if(disks[i][j]==0){
					placeability[i][j] = fi(i-1,j-1,-1,-1)||fi(i-1,j,-1,0)
					||fi(i-1,j+1,-1,1)||fi(i,j-1,0,-1)||fi(i,j+1,0,1)
					||fi(i+1,j-1,1,-1)||fi(i+1,j,1,0)||fi(i+1,j+1,1,1);

					if(placeability[i][j])
						pass=false;
				}
				else {
					placeability[i][j] = false;
				}
			}
	}

	boolean fi(int x,int y,int dx,int dy){
		if (x == -1 || y == -1 || x == 8 || y == 8)
			return false;

		if(color==disks[x][y]*-1)
			return i(x + dx, y + dy, dx, dy);
		else
			return false;
	}

	boolean i(int x,int y,int dx,int dy){
		if (x == -1 || y == -1 || x == 8 || y == 8)
			return false;

		if(disks[x][y]==0)
			return false;
		else{
			if(color==disks[x][y])
				return true;
			else
				return i(x + dx, y + dy, dx, dy);
		}
	}

	void judge() {

		if (blackDisks > whiteDisks) {
			Screen.text.setText("Black:" + blackDisks + " White:" + whiteDisks + " Black won!");
		}
		if (blackDisks == whiteDisks) {
			Screen.text.setText("Black:" + blackDisks + " White:" + whiteDisks + " Draw Game.");
		}
		if (blackDisks < whiteDisks) {
			Screen.text.setText("Black:" + blackDisks + " White:" + whiteDisks + " White won!");
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Reversi");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Screen s=new Screen();
		frame.add(s,BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

	}

}
