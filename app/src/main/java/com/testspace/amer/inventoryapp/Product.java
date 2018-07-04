package com.testspace.amer.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;

import com.testspace.amer.inventoryapp.db.ProductContract;
import com.testspace.amer.inventoryapp.utilities.ImageUtils;

import java.text.DecimalFormat;

public class Product {
    public static final Bitmap NO_PRODUCT_IMAGE = null;
    public static final String NO_SUPPLIER = "NO_SUPPLIER";
    public static final int UNKNOWN_PRODUCT_PRICE = 0;
    public static final String PRICE_CONCAT = " $";
    private long productId;
    private String productName;
    private Bitmap productImage;
    private double productPrice;
    private int productQuantity;
    private String supplierName;
    private String supplierContact;

    public Product(long productId, String productName, Bitmap productImage, double productPrice, int productQuantity, String supplierName, String supplierContact) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.supplierName = supplierName;
        this.supplierContact = supplierContact;
    }

    public Product(String productName, Bitmap productImage, double productPrice, int productQuantity, String supplierName, String supplierContact) {
        this.productName = productName;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
        this.supplierName = supplierName;
        this.supplierContact = supplierContact;
    }

    public Product(long productId, String productName, double productPrice, int productQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productQuantity = productQuantity;
    }

    public static String[] getBriefProductProjection() {
        return new String[]{ProductContract.ProductsEntry._ID,
                ProductContract.ProductsEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductsEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY};
    }

    public static boolean canQuantityDecremented(Product product) {
        return product.getProductQuantity() > 0;
    }

    public static ContentValues getValuesFromProduct(Product product) {
        ContentValues values = new ContentValues();
        if (product.getProductName() == null || product.getProductName().isEmpty())
            return null;
        else
            values.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_NAME, product.getProductName());
        if (product.getProductImage() != null)
            values.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_IMAGE, ImageUtils.fromBitmapToBlob(product.getProductImage()));
        if (product.getProductPrice() < 0)
            return null;
        else
            values.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_PRICE, product.getProductPrice());
        if (product.getProductQuantity() < 0)
            return null;
        values.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY, product.getProductQuantity());
        if (product.getSupplierName() != null || !product.getSupplierName().isEmpty() || !product.getSupplierName().equals(Product.NO_SUPPLIER))
            values.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_SUPPLIER_NAME, product.getSupplierName());
        if (product.getSupplierContact() != null || !product.getSupplierContact().isEmpty() || !product.getSupplierContact().equals(Product.NO_SUPPLIER))
            values.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT, product.getSupplierContact());
        return values;
    }

    public static Product getProductFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0)
            return null;
        long productId;
        String productName;
        Bitmap productImage;
        double productPrice;
        int productQuantity;
        String supplierName;
        String supplierContact;
        cursor.moveToFirst();
        if (cursor.getColumnIndex(ProductContract.ProductsEntry._ID) != -1)
            productId = cursor.getLong(cursor.getColumnIndex(ProductContract.ProductsEntry._ID));
        else
            return null;
        if (cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_NAME) != -1)
            productName = cursor.getString(cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_NAME));
        else
            return null;
        if (cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_IMAGE) != -1 && cursor.getBlob(cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_IMAGE)) != null)
            productImage = ImageUtils.fromBlobToBitmap(cursor.getBlob(cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_IMAGE)));
        else
            productImage = NO_PRODUCT_IMAGE;
        if (cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_PRICE) != -1)
            productPrice = cursor.getDouble(cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_PRICE));
        else
            return null;
        if (cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY) != -1)
            productQuantity = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY));
        else
            return null;
        if (cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_SUPPLIER_NAME) != -1)
            supplierName = cursor.getString(cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_SUPPLIER_NAME));
        else
            supplierName = NO_SUPPLIER;
        if (cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT) != -1)
            supplierContact = cursor.getString(cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_SUPPLIER_CONTACT));
        else
            supplierContact = NO_SUPPLIER;
        return new Product(productId, productName, productImage, productPrice, productQuantity, supplierName, supplierContact);
    }

    public static Product getBriefProductFromCursor(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0)
            return null;
        long productId;
        String productName;
        double productPrice;
        int productQuantity;
        if (cursor.getColumnIndex(ProductContract.ProductsEntry._ID) != -1)
            productId = cursor.getLong(cursor.getColumnIndex(ProductContract.ProductsEntry._ID));
        else
            return null;
        if (cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_NAME) != -1)
            productName = cursor.getString(cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_NAME));
        else
            return null;
        if (cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_PRICE) != -1)
            productPrice = cursor.getDouble(cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_PRICE));
        else
            return null;
        if (cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY) != -1)
            productQuantity = cursor.getInt(cursor.getColumnIndex(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY));
        else
            return null;
        return new Product(productId, productName, productPrice, productQuantity);
    }

    public long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Bitmap getProductImage() {
        return productImage;
    }

    public double getProductPrice() {
        return Double.parseDouble(new DecimalFormat("#.##").format(productPrice));
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getSupplierContact() {
        return supplierContact;
    }

    public void decProductQuantity() {
        if (this.productQuantity - 1 >= 0) {
            this.productQuantity--;
        } else {
            throw new IllegalStateException("Error: Trying to to set product quantity to negative value!");
        }
    }

    public void incProductQuantity() {
        this.productQuantity++;
    }
}