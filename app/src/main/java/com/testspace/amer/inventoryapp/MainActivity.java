package com.testspace.amer.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.testspace.amer.inventoryapp.adapters.BriefProductCursorAdapter;
import com.testspace.amer.inventoryapp.db.ProductContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String MODE = "Mode";
    private static final String ADD_MODE_TYPE = "AddMode";
    private static final int PRODUCT_LOADER_ID = 100;
    @BindView(R.id.productsListView)
    ListView productsListView;
    @BindView(R.id.emptyInventoryView)
    View emptyInventoryView;
    BriefProductCursorAdapter briefProductCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        productsListView.setEmptyView(emptyInventoryView);
        briefProductCursorAdapter = new BriefProductCursorAdapter(this, null);
        productsListView.setAdapter(briefProductCursorAdapter);
        getLoaderManager().initLoader(PRODUCT_LOADER_ID, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                return new CursorLoader(MainActivity.this, ProductContract.ProductsEntry.CONTENT_URI, Product.getBriefProductProjection(), null, null, null);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                briefProductCursorAdapter.swapCursor(data);
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                briefProductCursorAdapter.swapCursor(null);
            }
        });
        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
                intent.setData(ContentUris.withAppendedId(ProductContract.ProductsEntry.CONTENT_URI, id));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_product:
                Intent intent = new Intent(MainActivity.this, AddEditProductActivity.class);
                intent.putExtra(MODE, ADD_MODE_TYPE);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        setTitle(R.string.MainActivityTitle);
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }
}