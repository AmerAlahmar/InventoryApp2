package com.testspace.amer.inventoryapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.testspace.amer.inventoryapp.db.ProductContract;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddEditProductActivity extends AppCompatActivity {
    public static final int PICK_IMAGE_REQUEST_CODE = 105;
    private static final String MODE = "Mode";
    private static final String ADD_MODE = "AddMode";
    private static final String EDIT_MODE = "EditMode";
    private static final String ACTIVITY_TITLE_ADD = "Add Product";
    private static String CURRENT_MODE;
    @BindView(R.id.productImageImageView)
    ImageView productImageImageView;
    @BindView(R.id.productNamePlacerEditText)
    EditText productNamePlacerEditText;
    @BindView(R.id.productPricePlacerEditText)
    EditText productPricePlacerEditText;
    @BindView(R.id.productQuantityPlacerEditText)
    EditText productQuantityPlacerEditText;
    @BindView(R.id.productSupplierNamePlacerEditText)
    EditText productSupplierNamePlacerEditText;
    @BindView(R.id.productSupplierContactPlacerEditText)
    EditText productSupplierContactPlacerEditText;
    @BindView(R.id.productImageAdderImageButton)
    ImageButton productImageAdderImageButton;
    @BindView(R.id.confirmProductFAB)
    FloatingActionButton confirmProductFAB;
    private boolean CHECKED_OUT = false;
    private boolean NEED_FOR_UPDATE = false;
    private boolean HAVE_PROBLEM = false;
    private Product currentProduct = null;
    private Uri productUri;
    private Bitmap currentProductImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_product);
        ButterKnife.bind(this);
        productNamePlacerEditText.requestFocus();
        setActivityMode(getIntent());
        setActivityTitle();
        setOnTouchListeners();
        if (CURRENT_MODE.equals(EDIT_MODE))
            fillFields();
        else if (CURRENT_MODE.equals(ADD_MODE))
            invalidateOptionsMenu();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        productImageAdderImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }
        });
        confirmProductFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (CURRENT_MODE) {
                    case EDIT_MODE:
                        editModeCheckout();
                        break;
                    case ADD_MODE:
                        addModeCheckout();
                        break;
                    default:
                        HAVE_PROBLEM = true;
                        onBackPressed();
                        break;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                giveUpCheckout();
                break;
            case R.id.delete_product:
                deleteCheckout();
                break;
        }
        return true;
    }

    private void addModeCheckout() {
        Product product = saveAbleProduct();
        if (product == null) {
            Toast.makeText(this, getString(R.string.errorInsertingProduct), Toast.LENGTH_SHORT).show();
            return;
        }
        Uri newRow = getContentResolver().insert(ProductContract.ProductsEntry.CONTENT_URI, Product.getValuesFromProduct(product));
        if (newRow == null) {
            Toast.makeText(this, getString(R.string.errorInsertingProduct), Toast.LENGTH_SHORT).show();
            return;
        }
        productUri = newRow;
        CHECKED_OUT = true;
        Toast.makeText(this, getString(R.string.successInsertingProduct), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private void editModeCheckout() {
        Product product = saveAbleProduct();
        if (product == null) {
            Toast.makeText(this, getString(R.string.errorUpdatingProduct), Toast.LENGTH_SHORT).show();
            return;
        }
        int rowsEffected = getContentResolver().update(productUri, Product.getValuesFromProduct(product), null, null);
        if (rowsEffected != 1) {
            Toast.makeText(this, getString(R.string.errorUpdatingProduct), Toast.LENGTH_SHORT).show();
            return;
        }
        CHECKED_OUT = true;
        Toast.makeText(this, getString(R.string.successUpdatingProduct), Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private void giveUpCheckout() {
        if (!NEED_FOR_UPDATE) {
            CHECKED_OUT = true;
            onBackPressed();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.giveUpConfirmingMsg);
        builder.setPositiveButton(R.string.giveUpConfirmingYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                CHECKED_OUT = true;
                onBackPressed();
            }
        });
        builder.setNegativeButton(R.string.giveUpConfirmingNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteCheckout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.deleteConfirmingMsg);
        builder.setPositiveButton(R.string.deleteConfirmingYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int rowsEffected = getContentResolver().delete(productUri, null, null);
                if (rowsEffected == 1) {
                    Toast.makeText(AddEditProductActivity.this, getString(R.string.successDeleteProduct), Toast.LENGTH_SHORT).show();
                    CHECKED_OUT = true;
                    productUri = null;
                    onBackPressed();
                } else {
                    dialog.dismiss();
                    Toast.makeText(AddEditProductActivity.this, getString(R.string.errorDeleteProduct), Toast.LENGTH_SHORT).show();
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

    private Product saveAbleProduct() {
        if (!NEED_FOR_UPDATE) {
            productNamePlacerEditText.setError(getString(R.string.errorRequiredField));
            productPricePlacerEditText.setError(getString(R.string.errorRequiredField));
            productQuantityPlacerEditText.setError(getString(R.string.errorRequiredField));
            return null;
        }
        if (TextUtils.isEmpty(productNamePlacerEditText.getText())) {
            productNamePlacerEditText.setError(getString(R.string.errorRequiredField));
            return null;
        }
        if (TextUtils.isEmpty(productPricePlacerEditText.getText())) {
            productPricePlacerEditText.setError(getString(R.string.errorRequiredField));
            return null;
        }
        if (TextUtils.isEmpty(productQuantityPlacerEditText.getText())) {
            productQuantityPlacerEditText.setError(getString(R.string.errorRequiredField));
            return null;
        }
        if (Double.valueOf(productPricePlacerEditText.getText().toString()) < 0.0) {
            productPricePlacerEditText.setError(getString(R.string.errorRequiredPositiveField));
            return null;
        }
        if (Integer.valueOf(productQuantityPlacerEditText.getText().toString()) < 0) {
            productQuantityPlacerEditText.setError(getString(R.string.errorRequiredPositiveField));
            return null;
        }
        String name = productNamePlacerEditText.getText().toString().trim();
        Bitmap image = currentProductImage;
        String price = productPricePlacerEditText.getText().toString().trim();
        String quantity = productQuantityPlacerEditText.getText().toString().trim();
        String supplierName = productSupplierNamePlacerEditText.getText().toString().trim();
        String supplierContact = productSupplierContactPlacerEditText.getText().toString().trim();
        if (supplierName.equals(getString(R.string.supplierNameEmptyCaseText)))
            supplierName = "";
        if (supplierContact.equals(getString(R.string.supplierContactEmptyCaseText)))
            supplierContact = "";
        return new Product(name, image, Double.valueOf(price), Integer.valueOf(quantity), supplierName, supplierContact);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        currentProductImage = bitmap;
                        productImageImageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.e(ProductDetailsActivity.class.getSimpleName(), "Error: Couldn't get image: ", e);
                        Toast.makeText(this, getString(R.string.errorGettingImageFromGallery), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchListeners() {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                NEED_FOR_UPDATE = true;
                if (view.getId() != R.id.productImageAdderImageButton) {
                    EditText editText = (EditText) view;
                    editText.setError(null);
                }
                return false;
            }
        };
        productImageAdderImageButton.setOnTouchListener(onTouchListener);
        productSupplierNamePlacerEditText.setOnTouchListener(onTouchListener);
        productPricePlacerEditText.setOnTouchListener(onTouchListener);
        productQuantityPlacerEditText.setOnTouchListener(onTouchListener);
        productSupplierNamePlacerEditText.setOnTouchListener(onTouchListener);
        productSupplierContactPlacerEditText.setOnTouchListener(onTouchListener);
    }

    private void fillFields() {
        if (currentProduct.getProductImage() != Product.NO_PRODUCT_IMAGE) {
            productImageImageView.setImageBitmap(currentProduct.getProductImage());
        } else
            productImageImageView.setImageResource(R.drawable.no_image);
        productNamePlacerEditText.setText(currentProduct.getProductName());
        productPricePlacerEditText.setText(String.valueOf(currentProduct.getProductPrice()));
        productQuantityPlacerEditText.setText(String.valueOf(currentProduct.getProductQuantity()));
        if (!currentProduct.getSupplierName().equals(Product.NO_SUPPLIER))
            productSupplierNamePlacerEditText.setText(currentProduct.getSupplierName());
        else
            productSupplierNamePlacerEditText.setText("");
        if (!currentProduct.getSupplierContact().equals(Product.NO_SUPPLIER))
            productSupplierContactPlacerEditText.setText(currentProduct.getSupplierContact());
        else
            productSupplierContactPlacerEditText.setText("");
    }

    private void setActivityMode(Intent intent) {
        if (!intent.hasExtra(MODE)) {
            Log.e(AddEditProductActivity.class.getSimpleName(), "Error: Can't start this Activity without MODE Extra!!.");
            HAVE_PROBLEM = true;
            onBackPressed();
        }
        CURRENT_MODE = intent.getStringExtra(MODE);
        if (CURRENT_MODE.equals(EDIT_MODE)) {
            productUri = intent.getData();
            if (productUri == null) {
                Log.e(AddEditProductActivity.class.getSimpleName(), "Error: Can't Edit this Product");
                HAVE_PROBLEM = true;
                onBackPressed();
            } else {
                currentProduct = Product.getProductFromCursor(getContentResolver().query(productUri, null, null, null, null));
                if (currentProduct == null) {
                }
            }
        } else if (!CURRENT_MODE.equals(ADD_MODE)) {
            Log.e(AddEditProductActivity.class.getSimpleName(), "Error: Unknown MODE Extra!.");
            HAVE_PROBLEM = true;
            onBackPressed();
        }
    }

    private void setActivityTitle() {
        if (CURRENT_MODE.equals(EDIT_MODE))
            setTitle(currentProduct.getProductName());
        else setTitle(ACTIVITY_TITLE_ADD);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (CURRENT_MODE.equals(ADD_MODE)) {
            menu.findItem(R.id.delete_product).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_details_menu, menu);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_button);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!CHECKED_OUT) {
            giveUpCheckout();
        } else {
            if (productUri == null || HAVE_PROBLEM)
                finish();
            else {
                Intent intent = new Intent(AddEditProductActivity.this, ProductDetailsActivity.class);
                intent.setData(productUri);
                startActivity(intent);
                finish();
            }
        }
    }
}