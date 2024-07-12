package com.nrh.tictactoe;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

public class GridAdapter extends BaseAdapter {

    private final Context context;
    private String[] board;
    private final Typeface customFont;

    public GridAdapter(Context context, String[] board) {
        this.context = context;
        this.board = board;
        this.customFont = ResourcesCompat.getFont(context, R.font.archivo); // Replace with your font file name
    }

    @Override
    public int getCount() {
        return board.length;
    }

    @Override
    public Object getItem(int position) {
        return board[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;
        if (convertView == null) {
            textView = new TextView(context);
            textView.setLayoutParams(new ViewGroup.LayoutParams(200, 200)); // Set size of each cell
            textView.setGravity(android.view.Gravity.CENTER);
            textView.setTextSize(32);
            textView.setTypeface(customFont, Typeface.BOLD); // Set custom font and make text bold
            textView.setTextColor(context.getResources().getColor(R.color.black)); // Set text color
            textView.setBackgroundResource(R.drawable.grid_cell_background); // Set custom background
        } else {
            textView = (TextView) convertView;
        }

        textView.setText(board[position]);
        return textView;
    }

    public void updateBoard(String[] newBoard) {
        this.board = newBoard;
        notifyDataSetChanged();
    }
}
