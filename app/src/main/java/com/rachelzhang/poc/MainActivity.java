package com.rachelzhang.poc;

import android.Manifest.permission;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import com.rachelzhang.poc.Adapter2.MyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements MyListener {

  private static final String DESC_SORT_ORDER = " DESC";
  private static final int LIMIT = 5;

  private RecyclerView mRv1;
  private RecyclerView.Adapter mAdapter1;
  private List<Uri> mData;
  private List<Integer> mSelectedIndexes = new ArrayList<>();

  private Button mButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    checkPermission();

    mRv1 = findViewById(R.id.rv1);
    mRv1.setLayoutManager(new LinearLayoutManager(this));
    mAdapter1 = new Adapter1();
    ((Adapter1) mAdapter1).setMyListener(this);
    mRv1.setAdapter(mAdapter1);

    mButton = findViewById(R.id.button);
    mButton.setOnClickListener(view -> loadDevicePhotos(true));
    loadDevicePhotos(false);
  }

  private void loadDevicePhotos(boolean shouldAnimateDeletion) {
    ListeningExecutorService service = MoreExecutors
        .listeningDecorator(Executors.newFixedThreadPool(1));
    ListenableFuture<List<Uri>> listenableFuture =
        service.submit(() -> doInBackground(this));
    Futures.addCallback(listenableFuture, new FutureCallback<List<Uri>>() {
      @Override
      public void onSuccess(List<Uri> result) {
        mData = result;
        if (shouldAnimateDeletion) {
          // this is hard. because instead of re-drawing the second rv, you need to grab a hold of
          // it and update its content by calling removal. Fucking too hard, not worth it.
        } else {
          // just reload the data without animation.
          Log.e("rachel", "calling notifying item changed");
          // This must be run on ui thread like this, dunno why, but otherwise it complains. Can
          // double check with Ricardo why.
          runOnUiThread(() -> mAdapter1.notifyItemChanged(Adapter1.CAROUSEL_POS));
        }
      }

      @Override
      public void onFailure(Throwable t) {
        // Show toast, not able to load device image? Ask UX what to do there.
      }
    });
  }

  private List<Uri> doInBackground(Context context) {
    // This is all hack, not need for the real thing.
    int index = 0;

    ArrayList<Uri> list = Lists.newArrayList();
    String[] projection = new String[]{Media._ID, ImageColumns.DATE_TAKEN};
    String sortOrderWithLimit = ImageColumns.DATE_TAKEN + DESC_SORT_ORDER + " limit " + LIMIT;
    Cursor cursor = getContentResolver().query(
        Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null,
        sortOrderWithLimit
    );
    if (cursor == null || !cursor.moveToFirst()) {
      // No cursor here.
      // Must move cursor to first!
      return list;
    }
    while (!cursor.isAfterLast()) {
      int columnIndex = cursor.getColumnIndexOrThrow(Media._ID);
      int imageId = cursor.getInt(columnIndex);
      if (!mSelectedIndexes.contains(index)) {
        Uri uri =
            Uri.withAppendedPath(Media.EXTERNAL_CONTENT_URI, Integer.toString(imageId));
        list.add(uri);
        Log.e("loading uri", " " + uri.toString());
      } else {
        Log.e("rachel", "skipping index " + index);
      }
      cursor.moveToNext();
      index++;
    }
    // not sure if i should close it myself.
    cursor.close();
    return list;
  }

  private void checkPermission() {
    if (ContextCompat.checkSelfPermission(this, permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      // Permission is not granted, show dialog
      ActivityCompat.requestPermissions(this,
          new String[]{permission.READ_EXTERNAL_STORAGE},
          0);
    }
  }

  /**
   * implementation of MyListener
   */
  @Override
  public List<Uri> getData() {
    return mData;
  }

  @Override
  public void showMenu(int index) {
    mSelectedIndexes.add(index);
  }
}
