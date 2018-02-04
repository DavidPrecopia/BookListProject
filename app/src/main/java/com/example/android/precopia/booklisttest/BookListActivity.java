package com.example.android.precopia.booklisttest;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>>, BookRecyclerAdapter.ItemClickListener {
	
	private static final String LOG_TAG = BookListActivity.class.getSimpleName();
	
	private BookRecyclerAdapter bookRecyclerAdapter;
	
	private ProgressBar progressBar;
	private TextView errorTextView;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);
		
		progressBar = findViewById(R.id.progress_bar);
		errorTextView = findViewById(R.id.tv_error);
		
		// TODO Move below into own method
		RecyclerView recyclerView = findViewById(R.id.recycler_view_layout);
		
		recyclerView.setHasFixedSize(true);
		
		bookRecyclerAdapter = new BookRecyclerAdapter(new ArrayList<Book>(), this);
		recyclerView.setAdapter(bookRecyclerAdapter);
		
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		recyclerView.setLayoutManager(linearLayoutManager);
		
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
		recyclerView.addItemDecoration(dividerItemDecoration);
		
		queryServer();
	}
	
	
	private void queryServer() {
		if (haveConnection()) {
			getLoaderManager().initLoader(0, null, this);
		} else {
			progressBar.setVisibility(View.GONE);
			errorTextView.setText(R.string.error_no_connection);
		}
	}
	
	private boolean haveConnection() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			return false;
		}
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}
	
	
	@Override
	public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
		return new BookAsyncLoader(this, getUrl());
	}
	
	private String getUrl() {
		Intent intent = getIntent();
		String general = intent.getStringExtra(getString(R.string.general_edit_text));
		String title = intent.getStringExtra(getString(R.string.title_edit_text));
		String author = intent.getStringExtra(getString(R.string.author_edit_text));
		return QueryUrlConcatenation.concatUrl(general, title, author, getMaxResults());
	}
	
	private String getMaxResults() {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		return sharedPreferences.getString(
				getString(R.string.settings_max_results_key),
				getString(R.string.settings_default_value)
		);
	}
	
	@Override
	public void onLoadFinished(Loader<List<Book>> loader, List<Book> bookList) {
		progressBar.setVisibility(View.GONE);
		if (bookList == null || bookList.isEmpty()) {
			errorTextView.setText(R.string.error_no_books_found);
		} else {
			bookRecyclerAdapter.swapData(bookList);
		}
	}
	
	@Override
	public void onLoaderReset(Loader<List<Book>> loader) {
		bookRecyclerAdapter.swapData(new ArrayList<Book>());
	}
	
	
	@Override
	public void onClick(String bookInfoUrl) {
		if (TextUtils.isEmpty(bookInfoUrl)) {
			Toast.makeText(BookListActivity.this, R.string.no_book_info_url_error, Toast.LENGTH_SHORT).show();
		} else {
			intentWebBrowser(bookInfoUrl);
		}
	}
	
	private void intentWebBrowser(String bookInfoUrl) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bookInfoUrl));
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		}
	}
}