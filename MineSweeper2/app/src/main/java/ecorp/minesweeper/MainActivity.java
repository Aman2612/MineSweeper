package ecorp.minesweeper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    TableLayout tableLayout;
    private TextView liveScore;
    private MyButton buttons[][];

    private static int NO_OF_ROWS;
    private static int NO_OF_COLS;
    private static int NO_OF_MINES;

    private int size;
    private int score=0;
    private int board[][];
    private static final int neighbours[][]={{1,0},{-1,0},{0,1},{0,-1},{-1,1},{-1,-1},{1,-1},{1,1}};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = getIntent();
        int difficulty=intent.getIntExtra("difficulty",StartScreen.MEDIUM);

        switch (difficulty)
        {
            case StartScreen.MEDIUM:
                NO_OF_COLS = 8;
                NO_OF_ROWS = 10;
                NO_OF_MINES = 25;
                break;

            case StartScreen.EASY:
                NO_OF_COLS = 6;
                NO_OF_ROWS = 7;
                NO_OF_MINES = 5;
                break;

            case StartScreen.HARD:
                NO_OF_COLS = 12;
                NO_OF_ROWS = 15;
                NO_OF_MINES = 60;
                break;
        }
        tableLayout = (TableLayout) findViewById(R.id.table);
        liveScore = (TextView) findViewById(R.id.scoreTextView);

        final Button reset = (Button) findViewById(R.id.resetButton);

                    reset.setOnClickListener(new View.OnClickListener() {
                     @Override
                    public void onClick(View view) {
                     reset();

                         }
                                });

        setUpBoard();
        initialize();

    }
//// TODO: 01/02/17
       private void reset() {

           initialize();
           updateScore();
    }
    //// TODO: 01/02/17
    private void setUpBoard()
    {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width=displayMetrics.widthPixels;
        size=width/NO_OF_COLS;
        buttons=new MyButton[NO_OF_ROWS][NO_OF_COLS];
        board=new int[NO_OF_ROWS][NO_OF_COLS];

        for(int i=0;i<NO_OF_ROWS;i++)
        {
            TableRow row = new TableRow(MainActivity.this);
            for(int j=0;j<NO_OF_COLS;j++)
            {
                MyButton myButton = new MyButton(MainActivity.this,size);
                buttons[i][j]=myButton;
                myButton.setOnClickListener(this);
                myButton.setOnLongClickListener(this);
                row.addView(myButton);
            }
            tableLayout.addView(row);
        }

    }
    //// TODO: 01/02/17
    private void initialize()
    {
        score=0;
        for(int i=0;i<NO_OF_ROWS;i++)
        {
            for(int j=0;j<NO_OF_COLS;j++)
            {
                board[i][j]=0;
            }
        }
        setMines();
        refreshBoard();

    }

    private void setMines() {

        int MineCount=0;
        Random random = new Random();
        while(MineCount<NO_OF_MINES)
        {

            int randomNum = random.nextInt(NO_OF_ROWS*NO_OF_COLS);
            int row = randomNum/NO_OF_COLS;
            int col = randomNum%NO_OF_COLS;
            if(board[row][col]!=-1)
            {
                board[row][col]=-1;
                increaseNeighbourValues(row,col);
                MineCount++;
            }
        }
    }

    private void increaseNeighbourValues(int row,int col) {

        for(int i=0;i<neighbours.length;i++)
        {

                int neighbours2[]=neighbours[i];
               int neighbourRow=row+neighbours2[0];
               int neighbourCol=col+neighbours2[1];

                if(isInBounds(neighbourRow,neighbourCol) && board[neighbourRow][neighbourCol]!=-1)
                {
                    board[neighbourRow][neighbourCol]++;
                }
            }
        }


    private void refreshBoard() {
        //set value for each square
        for(int i = 0;i<NO_OF_ROWS;i++){
            for(int j = 0;j<NO_OF_COLS;j++){
                MyButton button = buttons[i][j];
                button.setUp(i,j,board[i][j]);
            }
        }
    }
    private boolean isInBounds(int row,int col){

        return  row >=0 && row < NO_OF_ROWS && col >=0 && col < NO_OF_COLS;
    }

    @Override
    public void onClick(View view) {

MyButton button = (MyButton) view;
        if(!button.isRevealed() && !button.isFlagged())
        {
            button.reveal();
        if(button.isMine())
        {
            Toast.makeText(this, "Game Over!", Toast.LENGTH_SHORT).show();
            revealAll();
            saveScore();
        }
            else
        {
            score++;
            if(button.isEmpty())
            {
                revealNeighbours(button);
            }
        }
            updateScore();
            checkIfGameComplete();

    }
    }

    private void revealNeighbours(MyButton button)
    {
        int row = button.getRow();
        int col=button.getColumn();

        for(int i=0;i<neighbours.length;i++)
        {
                int neighbour2[]=neighbours[i];
                int rowNeigh=row+neighbour2[0];
                int colNeigh=col+neighbour2[1];
                if(isInBounds(rowNeigh,colNeigh))
                {
                    MyButton button2=buttons[rowNeigh][colNeigh];
                    if(!button2.isRevealed())
                    {
                        button2.reveal();
                        score++;

                    if(button2.isEmpty())
                    {
                        revealNeighbours(button2);
                    }
                }
               //     board[rowNeigh][colNeigh]++;

                }
        }
    }

    private void updateScore() {
        liveScore.setText("Score : "+score);
    }

    @Override
    public boolean onLongClick(View view) {
        MyButton button = (MyButton) view;
        button.setFlag(!button.isFlagged());
        return  true;
    }


    private void checkIfGameComplete() {
        //if the no of unrevealed tiles is equal to no of mines, then the user wins
        if(NO_OF_MINES == NO_OF_COLS*NO_OF_ROWS - score){
            Toast.makeText(this,"You win",Toast.LENGTH_LONG).show();
            revealAll();
            saveScore();
        }
    }

    private void saveScore() {
        SharedPreferences sharedPreferences = getSharedPreferences("minesweeper",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("score",score);
        editor.commit();
    }

    private void revealAll() {

        for(int i=0;i<NO_OF_ROWS;i++)
        {
            for(int j=0;j<NO_OF_COLS;j++)
            {
                MyButton bt = buttons[i][j];
                if(!bt.isRevealed())
                {
                    bt.reveal();
                }
            }
        }
    }
    }
