package com.testspace.amer.inventoryapp.adapters;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.testspace.amer.inventoryapp.Product;
import com.testspace.amer.inventoryapp.R;
import com.testspace.amer.inventoryapp.db.ProductContract;

public class BriefProductCursorAdapter extends CursorAdapter {
    public BriefProductCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_products_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        TextView productNamePlacerTextView = view.findViewById(R.id.productNamePlacerTextView);
        TextView productPricePlacerTextView = view.findViewById(R.id.productPricePlacerTextView);
        TextView productQuantityPlacerTextView = view.findViewById(R.id.productQuantityPlacerTextView);
        ImageButton productQuantityDecImageButton = view.findViewById(R.id.productQuantityDecImageButton);
        ImageButton productQuantityIncImageButton = view.findViewById(R.id.productQuantityIncImageButton);
        final Product product = Product.getBriefProductFromCursor(cursor);
        productNamePlacerTextView.setText(product.getProductName());
        productPricePlacerTextView.setText(String.valueOf(product.getProductPrice()).concat(Product.PRICE_CONCAT));
        productQuantityPlacerTextView.setText(String.valueOf(product.getProductQuantity()));
        productQuantityIncImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY, product.getProductQuantity() + 1);
                context.getContentResolver().update(ContentUris.withAppendedId(ProductContract.ProductsEntry.CONTENT_URI, product.getProductId()), contentValues, null, null);
            }
        });
        productQuantityDecImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Product.canQuantityDecremented(product)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY, product.getProductQuantity() - 1);
                    context.getContentResolver().update(ContentUris.withAppendedId(ProductContract.ProductsEntry.CONTENT_URI, product.getProductId()), contentValues, null, null);
                }
            }
        });
    }
}