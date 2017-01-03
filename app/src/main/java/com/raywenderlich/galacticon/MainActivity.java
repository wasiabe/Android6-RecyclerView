/*
 * Copyright (c) 2016 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.galacticon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ImageRequester.ImageRequesterResponse {

  private RecyclerView mRecyclerView;
  private LinearLayoutManager mLinearLayoutManager;
  private RecyclerAdapter mAdapter;
  private ArrayList<Photo> mPhotosList;
  private ImageRequester mImageRequester;


  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    mLinearLayoutManager = new LinearLayoutManager(this);
    mRecyclerView.setLayoutManager(mLinearLayoutManager);

    mPhotosList = new ArrayList<>();
    mImageRequester = new ImageRequester(this);

    mAdapter = new RecyclerAdapter(mPhotosList);
    mRecyclerView.setAdapter(mAdapter);

    setRecyclerViewScrollListener();
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (mPhotosList.size() == 0) {
      requestPhoto();
    }
  }

  private int getLastVisibleItemPosition() {
    return mLinearLayoutManager.findLastVisibleItemPosition();
  }

  private void setRecyclerViewScrollListener() {
    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();
        if (!mImageRequester.isLoadingData() && totalItemCount == getLastVisibleItemPosition() + 1) {
          requestPhoto();
        }
      }
    });
  }

  private void requestPhoto() {

    try {
      mImageRequester.getPhoto();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void receivedNewPhoto(final Photo newPhoto) {

    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        mPhotosList.add(newPhoto);
        mAdapter.notifyItemInserted(mPhotosList.size());
      }
    });
  }
}
