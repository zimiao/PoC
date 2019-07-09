package com.rachelzhang.poc;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class Adapter2 extends RecyclerView.Adapter<Adapter2.MyViewHolder> {

  private List<Uri> mDataset;
  private MyListener mMyListener;

  public Adapter2() {
  }

  public void setData(List<Uri> data) {
    mDataset = data;
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public MyViewHolder(ImageView v) {
      super(v);
      imageView = v;
    }
  }

  @Override
  public Adapter2.MyViewHolder onCreateViewHolder(ViewGroup parent,
      int viewType) {
    ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
        .inflate(R.layout.image_item_view, parent, false);
    MyViewHolder vh = new MyViewHolder(v);
    return vh;
  }

  @Override
  public void onBindViewHolder(MyViewHolder holder, int position) {
    Context context = holder.imageView.getContext();
    int size = (int) (200 * context.getResources().getDisplayMetrics().density);
    Glide.with(context).load(mDataset.get(position).toString()).centerCrop().override(size, size)
        .into(holder.imageView);
    holder.imageView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.e("rachel", "clicking index: " + position);
        // somehow let home listener know about this is being selected.
        mMyListener.showMenu(position);
      }
    });
  }

  @Override
  public int getItemCount() {
    return mDataset.size();
  }

  public void setMyListener(MyListener mListener) {
    this.mMyListener = mListener;
  }

  public interface MyListener {

    List<Uri> getData();

    void showMenu(int selectedIndexes);
  }
}
