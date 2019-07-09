package com.rachelzhang.poc;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.rachelzhang.poc.Adapter2.MyListener;

public class Adapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public static int CAROUSEL_POS = 3;

  private static int CAROUSEL_TYPE = 0;
  private static int OTHER_TYPE = 1;

  private MyListener mMyListener;

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    if (viewType == CAROUSEL_TYPE) {
      Log.e("rachel", "calling onCreateViewHolder for carousel");
      View view = LayoutInflater.from(parent.getContext())
          .inflate(R.layout.carousel_item_view, parent, false);
      return new CarouselViewHolder(view);
    } else {
      Log.e("rachel", "calling onCreateViewHolder for others");
      TextView textView = (TextView) LayoutInflater.from(parent.getContext())
          .inflate(R.layout.text_item_view, parent, false);
      return new TextViewHolder(textView);
    }
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    // Create the child recycler view on the fly. Do this in onBind instead of onCreate because we
    // rely on "notifyDataSetChanged" which only triggers onBind.
    if (getItemViewType(position) == CAROUSEL_TYPE) {
      Log.e("rachel", "calling onBind for carousel");
      View view = holder.itemView;
      RecyclerView rv2 = view.findViewById(R.id.rv2);
      rv2.setLayoutManager(
          new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
      Adapter2 adapter = new Adapter2();
      adapter.setData(mMyListener.getData());
      adapter.setMyListener(mMyListener);
      rv2.setAdapter(adapter);
    }
  }

  @Override
  public int getItemCount() {
    return 10;
  }

  @Override
  public int getItemViewType(int position) {
    return position == CAROUSEL_POS ? CAROUSEL_TYPE : OTHER_TYPE;
  }

  public void setMyListener(MyListener mListener) {
    this.mMyListener = mListener;
  }

  public static class TextViewHolder extends RecyclerView.ViewHolder {

    public TextView mTextView;

    public TextViewHolder(@NonNull TextView itemView) {
      super(itemView);
      mTextView = itemView;
    }
  }

  public static class CarouselViewHolder extends RecyclerView.ViewHolder {

    public View mCarouselView;

    public CarouselViewHolder(@NonNull View itemView) {
      super(itemView);
      mCarouselView = itemView;
    }
  }
}

