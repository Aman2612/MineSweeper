package ecorp.minesweeper;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.widget.Button;

/**
 * Created by Aman on 01/02/17.
 */

public class MyButton extends Button {

    public static int MINE=-1;
    private int row;
    private int column;
    private int value = 0;
    private boolean revealed = false;
    private boolean flagged = false;
    private int size;

    public MyButton(Context context, int size) {
        super(context);
        setPadding(0,0,0,0);
        this.size=size;
    }


    public void setUp(int row,int col,int value){
        this.row = row;
        this.column = col;
        this.value = value;
        revealed = false;
        flagged = false;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }


    public void reveal(){
        revealed = true;
        flagged = false;
        invalidate();
    }

    public void setFlag(boolean flag){
        if(!revealed) {
            flagged = flag;
            invalidate();
        }}
    
    public boolean isFlagged() {
        return flagged;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public boolean isMine(){
        return value == MINE;
    }

    public boolean isEmpty(){
        return value == 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(size,size);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (revealed) {
            setBackground(ContextCompat.getDrawable(getContext(), R.color.colorAccent));
            if (value == MINE) {
                setText("*");
                setBackgroundResource(R.color.purple);

            } else if (value == 0) {
                setText(" ");
            } else {
                setText(String.valueOf(value));
            }
        } else {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.normal));
            if (flagged) {
              //  setText("!");
                setBackgroundResource(R.drawable.flag);
            } else {
                setText(" ");
            }
        }
    }}