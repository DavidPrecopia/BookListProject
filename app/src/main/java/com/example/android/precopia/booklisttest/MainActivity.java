package com.example.android.precopia.booklisttest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
	
	private EditText generalEditText;
	private EditText titleEditText;
	private EditText authorEditText;
	private Button button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		generalEditText = findViewById(R.id.general_edit_text);
		titleEditText = findViewById(R.id.title_edit_text);
		authorEditText = findViewById(R.id.author_edit_text);
		button = findViewById(R.id.book_search);
		
		searchButtonListener();
	}
	
	private void searchButtonListener() {
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				evaluateUserInput();
			}
		});
	}
	
	private void evaluateUserInput() {
		if (allTextFieldsAreEmpty()) {
			Toast.makeText(MainActivity.this, "All text fields are empty", Toast.LENGTH_SHORT).show();
		} else {
			Intent intent = new Intent(MainActivity.this, BookListActivity.class);
			intent.putExtra(getString(R.string.general_edit_text), generalEditText.getText().toString());
			intent.putExtra(getString(R.string.title_edit_text), titleEditText.getText().toString());
			intent.putExtra(getString(R.string.author_edit_text), authorEditText.getText().toString());
			startActivity(intent);
		}
	}
	
	private boolean allTextFieldsAreEmpty() {
		return TextUtils.isEmpty(generalEditText.getText().toString())
				&& TextUtils.isEmpty(titleEditText.getText().toString())
				&& TextUtils.isEmpty(authorEditText.getText().toString());
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.settings_item /* res/menu/menu */) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}