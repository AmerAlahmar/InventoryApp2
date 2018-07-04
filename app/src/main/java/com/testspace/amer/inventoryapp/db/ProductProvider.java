package com.testspace.amer.inventoryapp.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class ProductProvider extends ContentProvider {
    public static final String TAG = ProductProvider.class.getSimpleName();
    private static final int PRODUCTS_TABLE_URIMATCHER_CODE = 100;
    private static final int SINGLE_PRODUCT_URIMATCHER_CODE = 200;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.ProductsEntry.PATH_PRODUCTS, PRODUCTS_TABLE_URIMATCHER_CODE);
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.ProductsEntry.PATH_PRODUCTS.concat("/#"), SINGLE_PRODUCT_URIMATCHER_CODE);
    }

    private ProductDBHelper productDBHelper;

    @Override
    public boolean onCreate() {
        productDBHelper = new ProductDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = productDBHelper.getReadableDatabase();
        Cursor cursor;
        int matchCode = uriMatcher.match(uri);
        switch (matchCode) {
            case PRODUCTS_TABLE_URIMATCHER_CODE:
                cursor = database.query(ProductContract.ProductsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SINGLE_PRODUCT_URIMATCHER_CODE:
                selection = ProductContract.ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductContract.ProductsEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                Log.e(TAG, "query: ERROR, ", new IllegalArgumentException("Invalid URI: " + uri));
                return null;
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int matchCode = uriMatcher.match(uri);
        switch (matchCode) {
            case PRODUCTS_TABLE_URIMATCHER_CODE:
                if (isValuesValid(values)) {
                    SQLiteDatabase database = productDBHelper.getWritableDatabase();
                    long id = database.insert(ProductContract.ProductsEntry.TABLE_NAME, null, values);
                    if (id == -1) {
                        Log.e(TAG, "insert: WARNING, Failed to insert new row with URI: " + uri);
                        return null;
                    }
                    getContext().getContentResolver().notifyChange(uri, null);
                    return ContentUris.withAppendedId(uri, id);
                } else {
                    Log.e(TAG, "insert: ERROR, ", new IllegalArgumentException("Trying to insert invalid data with URI: " + uri));
                    return null;
                }
            default:
                Log.e(TAG, "insert: ERROR, ", new IllegalArgumentException("Invalid URI: " + uri));
                return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = productDBHelper.getWritableDatabase();
        int rowsDeleted;
        int matchCode = uriMatcher.match(uri);
        switch (matchCode) {
            case PRODUCTS_TABLE_URIMATCHER_CODE:
                rowsDeleted = database.delete(ProductContract.ProductsEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case SINGLE_PRODUCT_URIMATCHER_CODE:
                selection = ProductContract.ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductContract.ProductsEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                Log.e(TAG, "delete: ERROR, ", new IllegalArgumentException("Invalid URI: " + uri));
                return 0;
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int matchCode = uriMatcher.match(uri);
        SQLiteDatabase database = productDBHelper.getWritableDatabase();
        switch (matchCode) {
            case SINGLE_PRODUCT_URIMATCHER_CODE:
                selection = ProductContract.ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case PRODUCTS_TABLE_URIMATCHER_CODE:
                if (isValuesValid(values)) {
                    int rowsUpdated = database.update(ProductContract.ProductsEntry.TABLE_NAME, values, selection, selectionArgs);
                    if (rowsUpdated != 0) {
                        getContext().getContentResolver().notifyChange(uri, null);
                    }
                    return rowsUpdated;
                } else {
                    Log.e(TAG, "update: ERROR, ", new IllegalArgumentException("Trying to update invalid data with URI: " + uri));
                    return 0;
                }
            default:
                Log.e(TAG, "update: ERROR, ", new IllegalArgumentException("Invalid URI: " + uri));
                return 0;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int matchCode = uriMatcher.match(uri);
        switch (matchCode) {
            case PRODUCTS_TABLE_URIMATCHER_CODE:
                return ProductContract.ProductsEntry.CONTENT_LIST_TYPE;
            case SINGLE_PRODUCT_URIMATCHER_CODE:
                return ProductContract.ProductsEntry.CONTENT_ITEM_TYPE;
            default:
                Log.e(TAG, "getType: ERROR, ", new IllegalArgumentException("Invalid URI: " + uri));
                return null;
        }
    }

    private boolean isValuesValid(ContentValues values) {
        if (values == null || values.size() == 0) {
            Log.e(TAG, "isValuesValid: WARNING, No data provided!");
            return false;
        }
        if (values.containsKey(ProductContract.ProductsEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductContract.ProductsEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                Log.e(TAG, "isValuesValid: WARNING, missing name of product!");
                return false;
            }
        }
        if (values.containsKey(ProductContract.ProductsEntry.COLUMN_PRODUCT_PRICE)) {
            Double price = values.getAsDouble(ProductContract.ProductsEntry.COLUMN_PRODUCT_PRICE);
            if (price == null) {
                Log.e(TAG, "isValuesValid: WARNING, missing price of product!");
                return false;
            } else if (price < 0.0) {
                Log.e(TAG, "isValuesValid: WARNING, Invalid price of product!");
                return false;
            }
        }
        if (values.containsKey(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null) {
                Log.e(TAG, "isValuesValid: WARNING, missing quantity of product!");
                return false;
            } else if (quantity < 0) {
                Log.e(TAG, "isValuesValid: WARNING, Invalid quantity of product!");
                return false;
            }
        }
        return true;
    }
}