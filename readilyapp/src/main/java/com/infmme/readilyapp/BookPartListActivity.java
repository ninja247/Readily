package com.infmme.readilyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import com.infmme.readilyapp.util.BaseActivity;
import com.infmme.readilyapp.util.Constants;
import com.infmme.readilyapp.view.FabOnScrollBehavior;
import com.infmme.readilyapp.view.adapter.BookNavigationAdapter;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;

import java.util.ArrayList;
import java.util.List;

import static com.infmme.readilyapp.R.id.fab;
import static com.infmme.readilyapp.R.id.toolbar;

public class BookPartListActivity extends BaseActivity implements
    BookNavigationAdapter.OnItemClickListener {

  /**
   * Whether or not the activity is in two-pane mode, i.e. running on a tablet
   * device.
   */
  private boolean mTwoPane;

  private TableOfContents mTableOfContents;

  private Toolbar mToolbar;
  private FloatingActionButton mFab;
  private RecyclerView mRecyclerView;

  private BookPartDetailActivity.BookDetailOnFabClicked mCallback = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_bookpart_list);

    findViews();
    setSupportActionBar(mToolbar);
    // Show the Up button in the action bar.
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    mTwoPane = isTwoPane();

    Bundle bundle = getIntent().getExtras();
    mTableOfContents = (TableOfContents) bundle.getSerializable(
        Constants.EXTRA_TABLE_OF_CONTENTS);

    setupViews();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      NavUtils.navigateUpFromSameTask(this);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  protected void findViews() {
    mToolbar = (Toolbar) findViewById(toolbar);
    mFab = (FloatingActionButton) findViewById(fab);
    mRecyclerView = (RecyclerView) findViewById(R.id.bookpart_list);
  }

  protected void setupViews() {
    mToolbar.setTitle(getTitle());

    mFab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mCallback != null) {
          mCallback.onFabClicked();
        }
      }
    });
    if (mTwoPane) {
      CoordinatorLayout.LayoutParams p =
          (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
      p.setAnchorId(R.id.bookpart_frame_layout);
      p.setBehavior(new FabOnScrollBehavior(null, null));
      mFab.setLayoutParams(p);
      mFab.setVisibility(View.VISIBLE);
    } else {
      CoordinatorLayout.LayoutParams p =
          (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
      p.setAnchorId(View.NO_ID);
      p.setBehavior(null);
      mFab.setLayoutParams(p);
      mFab.setVisibility(View.GONE);
    }

    setupRecyclerView(mRecyclerView, mTableOfContents);
  }

  private boolean isTwoPane() {
    return findViewById(R.id.bookpart_detail_container) != null;
  }

  private void setupRecyclerView(@NonNull RecyclerView recyclerView,
                                 TableOfContents toc) {
    final List<TOCReference> tocReferences = toc.getTocReferences();

    List<BookNavigationAdapter.ParentPart> parentParts = new ArrayList<>();
    for (int i = 0; i < tocReferences.size(); i++) {
      parentParts.add(
          new BookNavigationAdapter.ParentPart(tocReferences.get(i)));
    }
    recyclerView.setAdapter(new BookNavigationAdapter(this, this, parentParts));
  }

  @Override
  public void onBookPartClicked(View v, TOCReference tocReference) {
    if (mTwoPane) {
      Bundle arguments = new Bundle();
      arguments.putSerializable(Constants.EXTRA_TOC_REFERENCE, tocReference);
      BookPartDetailFragment fragment = new BookPartDetailFragment();
      fragment.setArguments(arguments);
      getSupportFragmentManager()
          .beginTransaction()
          .replace(R.id.bookpart_detail_container, fragment)
          .commit();
      mCallback = fragment;
    } else {
      Context context = v.getContext();
      Intent intent = new Intent(
          context, BookPartDetailActivity.class);
      intent.putExtra(Constants.EXTRA_TOC_REFERENCE, tocReference);
      context.startActivity(intent);
    }
  }
}
