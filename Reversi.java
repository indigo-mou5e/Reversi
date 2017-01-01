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
	int[][] disks= new int[8][8]; // -1なら黒、1なら白、0ならどちらも置かれていない
	boolean[][] placeability=new boolean[8][8];
	boolean yourTurn=true; // trueならプレイヤーのターン
	boolean color=true; // trueなら現在黒のターン

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
	public void mousePressed(MouseEvent e){
		if(yourTurn){
			int x=e.getX()/80;
			int y=e.getY()/80;

			if(x<0)
				x=0;
			if(x>7)
				x=7;
			if(y<0)
				y=0;
			if(y>7)
				y=7;

			if(placeability[x][y]){
				place(x,y,color);
				color=!color;
				invest();
//				yourTurn=!yourTurn;
				Screen.text.setText(x + ", "+ y);
				repaint();
			}
		}
	}
	public void mouseExited(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}

	void place(int x,int y,boolean c){
		if(color)
			disks[x][y]=-1;
		else
			disks[x][y]=1;

		flip(x-1,y-1,-1,-1,color);
		flip(x-1,y,-1,0,color);
		flip(x-1,y+1,-1,1,color);
		flip(x,y-1,0,-1,color);
		flip(x,y+1,0,1,color);
		flip(x+1,y-1,1,-1,color);
		flip(x+1,y,1,0,color);
		flip(x+1,y+1,1,1,color);
	}

	boolean flip(int x,int y,int dx,int dy,boolean c) {
		if (x == -1 || y == -1 || x == 8 || y == 8)
			return false;
		if (color) {
			switch (disks[x][y]) {
				case -1:
					return true;
				case 0:
					return false;
				case 1:
					if (flip(x + dx, y + dy, dx, dy, c)) {
						disks[x][y] = -1;
						return true;
					} else
						return false;
			}
		}
		else {
			switch (disks[x][y]) {
				case 1:
					return true;
				case 0:
					return false;
				case -1:
					if (flip(x + dx, y + dy, dx, dy, c)) {
						disks[x][y] = 1;
						return true;
					} else
						return false;
			}
		}
		return false;
	}

	void invest(){
		for(int i=0;i<8;i++)
			for(int j=0;j<8;j++){
				if(disks[i][j]==0){
					placeability[i][j] = fi(i-1,j-1,-1,-1,color)||fi(i-1,j,-1,0,color)
					||fi(i-1,j+1,-1,1,color)||fi(i,j-1,0,-1,color)||fi(i,j+1,0,1,color)
					||fi(i+1,j-1,1,-1,color)||fi(i+1,j,1,0,color)||fi(i+1,j+1,1,1,color);
				}
				else {
					placeability[i][j] = false;
				}
			}
	}

	boolean fi(int x,int y,int dx,int dy,boolean c){
		if (x == -1 || y == -1 || x == 8 || y == 8)
			return false;
		if(color) {
			switch (disks[x][y]) {
				case -1:
				case 0:
					return false;
				case 1:
					return i(x + dx, y + dy, dx, dy, c);
			}
		}
		else{
			switch (disks[x][y]) {
				case 1:
				case 0:
					return false;
				case -1:
					return i(x + dx, y + dy, dx, dy, c);
			}
		}
		return false;
	}

	boolean i(int x,int y,int dx,int dy,boolean c){
		if (x == -1 || y == -1 || x == 8 || y == 8)
			return false;
		if(color) {
			switch (disks[x][y]) {
				case -1:
					return true;
				case 0:
					return false;
				case 1:
					return i(x + dx, y + dy, dx, dy, c);
			}
		}
		else {
			switch (disks[x][y]) {
				case 1:
					return true;
				case 0:
					return false;
				case -1:
					return i(x + dx, y + dy, dx, dy, c);
			}
		}
		return false;
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Firefly Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Screen s=new Screen();
		frame.add(s,BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);	

	}

}
