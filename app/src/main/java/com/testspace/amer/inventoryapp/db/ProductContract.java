package com.testspace.amer.inventoryapp.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import com.testspace.amer.inventoryapp.Product;

public final class ProductContract {
    public static final String CONTENT_AUTHORITY = "com.testspace.amer.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public ProductContract() {
    }

    public static final class ProductsEntry implements BaseColumns {
        public static final String PATH_PRODUCTS = "products";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_IMAGE = "image";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_PRODUCT_SUPPLIER_CONTACT = "supplier_contact";
        public static final String CREATE_PRODUCTS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PRODUCT_IMAGE + " BLOB, "
                + COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + COLUMN_PRODUCT_PRICE + " REAL NOT NULL, "
                + COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT " + String.valueOf(Product.UNKNOWN_PRODUCT_PRICE) + ", "
                + COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT, "
                + COLUMN_PRODUCT_SUPPLIER_CONTACT + " TEXT);";
        public static final String DROP_PRODUCTS_TABLE_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}