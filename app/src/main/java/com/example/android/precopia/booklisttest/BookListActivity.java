package com.example.android.precopia.booklisttest;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

	private static final String LOG_TAG = BookListActivity.class.getSimpleName();
	
	private BookAdapter bookAdapter;
	private ProgressBar progressBar;
	private TextView errorTextView;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);
		
		progressBar = findViewById(R.id.progress_bar);
		errorTextView = findViewById(R.id.error_text);
		
		ListView listView = findViewById(R.id.list_view_layout);
		bookAdapter = new BookAdapter(BookListActivity.this, new ArrayList<Book>());
		listView.setAdapter(bookAdapter);
		listViewListener(listView);
		
		if (haveConnection()) {
			getLoaderManager().initLoader(0, null, this);
		} else {
			progressBar.setVisibility(View.GONE);
			errorTextView.setText(R.string.error_no_connection);
		}
	}
	
	
	private void listViewListener(ListView listView) {
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				openWebBrowser(position);
			}
		});
	}
	
	private void openWebBrowser(int position) {
		String infoUrl = bookAdapter.getItem(position).getBookInfoUrl();
		if (TextUtils.isEmpty(infoUrl)) {
			Toast.makeText(BookListActivity.this, R.string.no_book_info_url_error, Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(infoUrl));
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivity(intent);
			}
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
		return new BookAsyncLoader(BookListActivity.this, getUrl());
	}
	
	private String getUrl() {
		Intent intent = getIntent();
		String general = intent.getStringExtra(getString(R.string.general_edit_text));
		String title = intent.getStringExtra(getString(R.string.title_edit_text));
		String author = intent.getStringExtra(getString(R.string.author_edit_text));
		return QueryUrlConcatenation.concatUrl(general, title, author);
	}
	
	@Override
	public void onLoadFinished(Loader<List<Book>> loader, List<Book> bookList) {
		progressBar.setVisibility(View.GONE);
		if (bookList == null || bookList.isEmpty()) {
			errorTextView.setText(R.string.error_no_books_found);
		} else {
			bookAdapter.swapData(bookList);
		}
	}
	
	@Override
	public void onLoaderReset(Loader<List<Book>> loader) {
		bookAdapter.clear();
	}
}