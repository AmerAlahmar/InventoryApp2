package com.testspace.amer.inventoryapp;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.testspace.amer.inventoryapp.db.ProductContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDetailsActivity extends AppCompatActivity {
    private static final String MODE = "Mode";
    private static final String EDIT_MODE_TYPE = "EditMode";
    @BindView(R.id.productImageImageView)
    ImageView productImageImageView;
    @BindView(R.id.productNamePlacerTextView)
    TextView productNamePlacerTextView;
    @BindView(R.id.productPricePlacerTextView)
    TextView productPricePlacerTextView;
    @BindView(R.id.productQuantityPlacerTextView)
    TextView productQuantityPlacerTextView;
    @BindView(R.id.productSupplierNamePlacerTextView)
    TextView productSupplierNamePlacerTextView;
    @BindView(R.id.productSupplierContactPlacerTextView)
    TextView productSupplierContactPlacerTextView;
    @BindView(R.id.productQuantityDecImageButton)
    ImageButton productQuantityDecImageButton;
    @BindView(R.id.productQuantityIncImageButton)
    ImageButton productQuantityIncImageButton;
    @BindView(R.id.productSupplierCallImageButton)
    ImageButton productSupplierCallImageButton;
    @BindView(R.id.editProductFAB)
    FloatingActionButton editProductFAB;
    private Product currentProduct;
    private Uri productUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        ButterKnife.bind(this);
        productUri = getIntent().getData();
        if (productUri == null) {
            Toast.makeText(this, getString(R.string.errorFindingProductDetails), Toast.LENGTH_SHORT).show();
            onBackPressed();
        } else {
            currentProduct = getCurrentProduct(productUri);
            setActivityTitle();
            setDetails();
            setOnClickListeners(currentProduct);
        }
    }

    private void setActivityTitle() {
        setTitle(currentProduct.getProductName());
    }

    private void setDetails() {
        if (currentProduct.getProductImage() != Product.NO_PRODUCT_IMAGE)
            productImageImageView.setImageBitmap(currentProduct.getProductImage());
        else
            productImageImageView.setImageResource(R.drawable.no_image);
        productNamePlacerTextView.setText(currentProduct.getProductName());
        productPricePlacerTextView.setText(String.valueOf(currentProduct.getProductPrice()).concat(Product.PRICE_CONCAT));
        productQuantityPlacerTextView.setText(String.valueOf(currentProduct.getProductQuantity()));
        if (!currentProduct.getSupplierName().equals(Product.NO_SUPPLIER) && !currentProduct.getSupplierName().isEmpty())
            productSupplierNamePlacerTextView.setText(currentProduct.getSupplierName());
        else{
            productSupplierNamePlacerTextView.setTextColor(getResources().getColor(R.color.colorAccent));
            productSupplierNamePlacerTextView.setText(getString(R.string.supplierNameEmptyCaseText));
        }
        if (!currentProduct.getSupplierContact().equals(Product.NO_SUPPLIER) && !currentProduct.getSupplierContact().isEmpty())
            productSupplierContactPlacerTextView.setText(currentProduct.getSupplierContact());
        else{
            productSupplierContactPlacerTextView.setTextColor(getResources().getColor(R.color.colorAccent));
            productSupplierContactPlacerTextView.setText(getString(R.string.supplierContactEmptyCaseText));
        }
    }

    private void setOnClickListeners(final Product currentProduct) {
        productQuantityIncImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY, currentProduct.getProductQuantity() + 1);
                int rowsEffected = getContentResolver().update(ContentUris.withAppendedId(ProductContract.ProductsEntry.CONTENT_URI, currentProduct.getProductId()), contentValues, null, null);
                if (rowsEffected == 1) {
                    currentProduct.incProductQuantity();
                    productQuantityPlacerTextView.setText(String.valueOf(currentProduct.getProductQuantity()));
                } else {
                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.errorChangingProductQuantity), Toast.LENGTH_SHORT).show();
                }
            }
        });
        productQuantityDecImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Product.canQuantityDecremented(currentProduct)) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ProductContract.ProductsEntry.COLUMN_PRODUCT_QUANTITY, currentProduct.getProductQuantity() - 1);
                    int rowsEffected = getContentResolver().update(ContentUris.withAppendedId(ProductContract.ProductsEntry.CONTENT_URI, currentProduct.getProductId()), contentValues, null, null);
                    if (rowsEffected == 1) {
                        currentProduct.decProductQuantity();
                        productQuantityPlacerTextView.setText(String.valueOf(currentProduct.getProductQuantity()));
                    } else {
                        Toast.makeText(ProductDetailsActivity.this, getString(R.string.errorChangingProductQuantity), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        productSupplierCallImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:".concat(currentProduct.getSupplierContact())));
                startActivity(intent);
            }
        });
        editProductFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this, AddEditProductActivity.class);
                intent.setData(productUri);
                intent.putExtra(MODE, EDIT_MODE_TYPE);
                startActivity(intent);
                finish();
            }
        });
    }

    private Product getCurrentProduct(Uri productUri) {
        Cursor cursor = getContentResolver().query(productUri, null, null, null, null);
        return Product.getProductFromCursor(cursor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_product:
                deleteProductAndCloseIfConfirmed();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_details_menu, menu);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        return true;
    }

    private void deleteProductAndCloseIfConfirmed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteConfirmingMsg);
        builder.setPositiveButton(R.string.deleteConfirmingYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int rowsEffected = getContentResolver().delete(productUri, null, null);
                if (rowsEffected == 1) {
                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.successDeleteProduct), Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    dialog.dismiss();
                    Toast.makeText(ProductDetailsActivity.this, getString(R.string.errorDeleteProduct), Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton(R.string.deleteConfirmingNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}