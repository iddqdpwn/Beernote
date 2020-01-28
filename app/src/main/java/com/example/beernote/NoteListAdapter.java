package com.example.beernote;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class NoteListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Model> noteList;

    public NoteListAdapter(Context context, int layout, ArrayList<Model> noteList) {
        this.context = context;
        this.layout = layout;
        this.noteList = noteList;
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtBrewery, txtBeer, txtNote;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row==null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.txtBrewery = row.findViewById(R.id.txtBrewery);
            holder.txtBeer = row.findViewById(R.id.txtBeer);
            holder.txtNote = row.findViewById(R.id.txtNote);
            holder.imageView = row.findViewById(R.id.imgIcon);
            row.setTag(holder);
        }
        else{
            holder = (ViewHolder) row.getTag();
        }
        Model model = noteList.get(position);

        holder.txtBrewery.setText(model.getBreweryName());
        holder.txtBeer.setText(model.getBeerName());
        holder.txtNote.setText(model.getNote());

        byte[] recordImage = model.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(recordImage,0,recordImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;

    }
}
