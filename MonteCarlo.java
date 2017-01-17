package reversi;

/**
 * Created by Kitami on 2017/01/10.
 */
interface strategy{
    int[] suggestion();
}

class MonteCarlo implements strategy{

    int color;
    int comColor;
    int[][] dCopy=new int[8][8];
    boolean[][] placeability=new boolean[8][8];
    boolean pass=false;

    public int[] suggestion(){
        int placeCount =0; //置ける場所がいくつあるかのカウンター
        int[][] coordinate =new int[50][2]; //置ける座標の位置記録用
        int[] winCount =new int[50]; //それぞれの場所で何回勝てたかのカウンター
        coordinate[0][0]=0;
        coordinate[0][1]=0;

        for(int i=0;i<8;i++) {
            for (int j = 0; j < 8; j++) {
                if (Reversi.placeability[i][j]) {
                    coordinate[placeCount][0] = i;
                    coordinate[placeCount][1] = j;
                    placeCount++;
                }
            }
        }

        int winMax=0;//勝ちの最大数
        int maxCount=0;//最大勝ち数の場所の数
        int maxPlace=0; //最大勝ち数の場所
        for(int i=0;i<placeCount;i++){
            for(int j=0;j<100;j++) {
                winCount[i] += estimate(coordinate[i][0], coordinate[i][1]);
            }
            System.out.println(coordinate[i][0]+","+coordinate[i][1]+" "+winCount[i]+"/100");
            if(winMax<winCount[i]) {
                maxPlace = i;
                maxCount=1;
                winMax=winCount[i];
            }
            else if(winMax==winCount[i]){
                maxCount++;
                if(maxCount*Math.random()<1)
                    maxPlace=i;
            }
        }
        System.out.println("return ("+coordinate[maxPlace][0]+","+coordinate[maxPlace][1]+")");
        return coordinate[maxPlace];
    }

    int estimate(int x,int y){
        for(int i=0;i<8;i++)
            for(int j=0;j<8;j++)
                dCopy[i][j]=Reversi.disks[i][j];

        color=Reversi.color;
        comColor=color;

        place(x,y);

        if(random()){
            return 1;
        }

        return 0;
    }

    boolean random() {
        pass=true;
        invest();
        if(pass){
            color*=-1;
            invest();
            if(pass){
                int b=0;
                int w=0;

                for (int i = 0; i < 8; i++)
                    for (int j = 0; j < 8; j++)
                        switch(dCopy[i][j]){
                            case -1:
                                b++;
                                break;
                            case 1:
                                w++;
                                break;
                        }

                return (comColor==-1&&b>w)||(comColor==1&&w>b);
            }
        }
        int count = 0;
        int[][] coordinate = new int[64][2]; //置ける座標の位置記録用

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                if (placeability[i][j]) {
                    coordinate[count][0] = i;
                    coordinate[count][1] = j;
                    count++;
                }

        count=(int)(Math.random()*count);
        place(coordinate[count][0],coordinate[count][1]);
        color*=-1;

        return random();
    }

    void place(int x,int y){
        /** 手番に応じて石を置く */
        if(color==-1) {
            dCopy[x][y] = -1;
        }
        else {
            dCopy[x][y] = 1;
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

        if(dCopy[x][y]==0)
            return false;
        else {
            if (dCopy[x][y] == color)
                return true;
            else {
                if (flip(x + dx, y + dy, dx, dy)) {
                    dCopy[x][y] = color;
                    return true;
                } else
                    return false;
            }
        }
    }

    void invest(){
        for(int i=0;i<8;i++)
            for(int j=0;j<8;j++){
                if(dCopy[i][j]==0){
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

        if(color==dCopy[x][y]*-1)
            return i(x + dx, y + dy, dx, dy);
        else
            return false;
    }

    boolean i(int x,int y,int dx,int dy){
        if (x == -1 || y == -1 || x == 8 || y == 8)
            return false;

        if(dCopy[x][y]==0)
            return false;
        else{
            if(color==dCopy[x][y])
                return true;
            else
                return i(x + dx, y + dy, dx, dy);
        }
    }
}