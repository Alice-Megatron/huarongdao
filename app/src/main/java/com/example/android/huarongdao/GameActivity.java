package com.example.android.huarongdao;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.example.android.huarongdao.R;
import com.example.android.huarongdao.MainActivity;

import java.util.Arrays;

public class GameActivity extends AppCompatActivity {
    private int[][] board=new int[5][4];
    private ConstraintLayout gameView;
    private int gameIndex = 0;
    private ImageView gameBoard;
    private float startx,starty;
    //曹操 0 竖将 1234 横将 5 兵 6789 空 -1

    private final int[][][] boardSet=
            {{{1,0,0,3},{1,0,0,3},{2,5,5,4},{2,6,7,4},{-1,8,9,-1}},//0-横刀立马
            {{1,0,0,3},{1,0,0,3},{6,5,5,7},{2,8,9,4},{2,-1,-1,4}},//1-指挥若定
            {{-1,0,0,-1},{1,0,0,3},{1,2,4,3},{6,2,4,7},{5,5,8,9}},//2-将拥曹营
            {{1,0,0,3},{1,0,0,3},{6,7,8,9},{2,5,5,4},{2,-1,-1,4}},//3-齐头并进
//            {{6,0,0,7},{1,0,0,3},{1,5,5,3},{2,8,9,4},{2,-1,-1,4}}};//4-兵分三路
            {{-1,0,0,-1},{-1,0,0,-1},{-1,5,5,-1},{2,-1,-1,4},{2,-1,-1,4}}};//4-测试用图

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if(gameIndex!=0)
                        gameIndex = gameIndex - 1;
                    gameView.removeViews(1,10);
                    generateBoard(gameIndex);
                    return true;
                case R.id.navigation_dashboard:
                    gameView.removeViews(1,10);
                    generateBoard(gameIndex);
                    return true;
                case R.id.navigation_notifications:
                    if(gameIndex!=boardSet.length-1)
                        gameIndex = gameIndex + 1;
                    gameView.removeViews(1,10);
                    generateBoard(gameIndex);
                    return true;
                case R.id.navigation_return:
                    Intent intent = new Intent(GameActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        gameView=findViewById(R.id.container);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        generateBoard(gameIndex);
    }

    private void generateBoard(int gameIndex) {
        gameBoard = (ImageView) LayoutInflater.from(this).inflate(R.layout.board, null);
        gameView.addView(gameBoard);
        for (int i = 0; i < board.length; i++)
            board[i] = Arrays.copyOf(boardSet[gameIndex][i], boardSet[gameIndex][i].length);
        generatePiece(board);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void generatePiece(int[][] board){
        float margin=15;//gameBoard.getWidth()/(float)26.0;
        float top=15;//gameBoard.getY()+margin;
        float left=15;//gameBoard.getX()+margin;
        final float block=90;//(gameBoard.getHeight()-2*margin)/board.length;
        int[] pieceSet={R.layout.caocao,R.layout.zhangfei,R.layout.huangzhong,R.layout.machao,R.layout.zhaoyun,R.layout.guanyu,R.layout.bing1,R.layout.bing2,R.layout.bing3,R.layout.bing4};
        for(int m = 0;m < board.length;m++)
            for(int n=0;n<board[0].length;n++){
                final int tempNode=board[m][n];
                if(tempNode!=-1&&pieceSet[tempNode]!=0){
                    final ImageView piece =(ImageView) LayoutInflater.from(this).inflate(pieceSet[tempNode],null);
                    pieceSet[tempNode]=0;
                    piece.setX(getPixels((int)(left+n*block)));
                    piece.setY(getPixels((int)(top+m*block)));
                    piece.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                // 按下
                                case MotionEvent.ACTION_DOWN:
                                    startx=event.getX();
                                    starty=event.getY();
                                    Log.d("startposX",startx+"");
                                    Log.d("startposY",starty+"");
                                    break;
                                // 移动
                                case MotionEvent.ACTION_UP:
                                    float mCurrentPosX = event.getX();
                                    float mCurrentPosY = event.getY();
                                    Log.d("endposX",event.getX()+"");
                                    Log.d("endposY",event.getY()+"");
                                    Log.d("mCurrentPosX - v.getX()",mCurrentPosX - startx+"");
                                    Log.d("mCurrentPosY - v.getY()",mCurrentPosY - starty+"");
                                    //左-1 右-2 上-3 下-4
                                    if (mCurrentPosX - startx > 50 && Math.abs(mCurrentPosY - starty) < 200){
                                        if(move(tempNode,2)) {
                                            Log.d("direction","右");
                                            gameView.removeView(v);
                                            v.setX(v.getX() + getPixels(90));
                                            gameView.addView(v);
                                        }
                                    }
                                    else if (startx - mCurrentPosX > 50 && Math.abs(mCurrentPosY - starty) < 200){
                                        if(move(tempNode,1)) {
                                            Log.d("direction","左");
                                            gameView.removeView(v);
                                            v.setX(v.getX() - getPixels(90));
                                            gameView.addView(v);
                                        }
                                    }
                                    else if (mCurrentPosY - starty > 50 && Math.abs(mCurrentPosX - startx) < 200){
                                        if(move(tempNode,4)) {
                                            Log.d("direction","下");
                                            gameView.removeView(v);
                                            v.setY(v.getY() + getPixels(90));
                                            gameView.addView(v);
                                        }
                                    }
                                    else if (starty - mCurrentPosY > 50 && Math.abs(mCurrentPosX - startx) < 200){
                                        if(move(tempNode,3)) {
                                            Log.d("direction","上");
                                            gameView.removeView(v);
                                            v.setY(v.getY() - getPixels(90));
                                            gameView.addView(v);
                                        }
                                    }
                                    break;
                                default:
                                    break;
                            }
                            return true;
                        }
                    });
                    gameView.addView(piece);
                    if(board[4][1]==0&&board[4][2]==0){
                        Log.d("成功","恭喜完成这一关卡！");
                        AlertDialog.Builder builder = new AlertDialog.Builder( GameActivity.this);
                        builder.setMessage("胜利");
                        builder.setNeutralButton("恭喜您完成了这一关卡！", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }
            }
    }

    private int getPixels(int dipValue){
        Resources r = getResources();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue,   r.getDisplayMetrics());
        return px;
    }

    private boolean move(int index,int direction) {
        boolean flag = true;
        if (direction == 1) {
            for (int m = 0; m < board.length; m++)
                for (int n = 0; n < board[0].length; n++)
                    if (board[m][n] == index)
                        if (n == 0 || (board[m][n - 1] != index && board[m][n - 1] != -1))
                            flag = false;
            if (flag)
                for (int m = 0; m < board.length; m++)
                    for (int n = 1; n < board[0].length; n++)
                        if (board[m][n] == index) {
                            board[m][n - 1] = index;
                            board[m][n] = -1;
                        }
        } else if (direction == 2) {
            for (int m = 0; m < board.length; m++)
                for (int n = 0; n < board[0].length; n++)
                    if (board[m][n] == index)
                        if (n == board[0].length - 1 || (board[m][n + 1] != index && board[m][n + 1] != -1))
                            flag = false;
            if (flag)
                for (int m = 0; m < board.length; m++)
                    for (int n = board[0].length - 2; n >= 0; n--)
                        if (board[m][n] == index) {
                            board[m][n + 1] = index;
                            board[m][n] = -1;
                        }
        } else if (direction == 3) {
            for (int m = 0; m < board.length; m++)
                for (int n = 0; n < board[0].length; n++)
                    if (board[m][n] == index)
                        if (m == 0 || (board[m - 1][n] != index && board[m - 1][n] != -1))
                            flag = false;
            if (flag)
                for (int m = 1; m < board.length; m++)
                    for (int n = 0; n < board[0].length; n++)
                        if (board[m][n] == index) {
                            board[m - 1][n] = index;
                            board[m][n] = -1;
                        }
        } else if (direction == 4) {
            for (int m = 0; m < board.length; m++)
                for (int n = 0; n < board[0].length; n++)
                    if (board[m][n] == index)
                        if (m == board.length - 1 || (board[m + 1][n] != index && board[m + 1][n] != -1))
                            flag = false;
            if (flag)
                for (int m = board.length - 2; m >= 0; m--)
                    for (int n = 0; n < board[0].length; n++)
                        if (board[m][n] == index) {
                            board[m + 1][n] = index;
                            board[m][n] = -1;
                        }
        }
        if(board[4][1]==0&&board[4][2]==0){
            Log.d("成功","恭喜完成这一关卡！");
            AlertDialog.Builder builder = new AlertDialog.Builder( GameActivity.this);
            builder.setMessage("胜利");
            builder.setNeutralButton("恭喜您完成了这一关卡！", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return flag;
    }

    private boolean haveContent(int[][] array) {
        for (int i = 0; i < array.length; i++){
            for (int column : board[i]){
                if (column != 0)
                    return true;
            }
        }
        return false;
    }

}
