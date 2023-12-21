    package com.dausinvestama.eaterly;

    import androidx.appcompat.app.AppCompatActivity;

    import android.app.AlertDialog;
    import android.content.DialogInterface;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.net.Uri;
    import android.os.Bundle;
    import android.provider.MediaStore;
    import android.text.Editable;
    import android.text.TextWatcher;
    import android.view.View;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.Toast;

    import java.text.NumberFormat;
    import java.util.Locale;

    public class SellerInsertMenu extends AppCompatActivity {

        private EditText editPrice, editName, editDescription, editTime;
        private ImageButton btnInsert, btnBack;
        Button btnSubmit;
        private static final int REQUEST_IMAGE_CAPTURE = 1;
        private static final int PICK_IMAGE =2;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_seller_insert_menu);

            editName = findViewById(R.id.editName);
            editDescription = findViewById(R.id.editDescription);
            editPrice = findViewById(R.id.editPrice);
            editTime = findViewById(R.id.editTimeEstimation);
            btnBack = findViewById(R.id.btn_back);

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SellerInsertMenu.this, SellerActivity.class));
                    finish();
                }
            });

            editTime.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().isEmpty() && !s.toString().endsWith(" min") && !s.toString().endsWith(" hour")) {
                        int timeInMinutes;
                        try {
                            timeInMinutes = Integer.parseInt(s.toString());
                        } catch (NumberFormatException e) {
                            return;
                        }

                        int hours = timeInMinutes / 60;
                        int minutes = timeInMinutes % 60;

                        String timeFormatted;
                        if (hours > 0) {
                            timeFormatted = hours + " hour" + (hours > 1 ? "s" : "");
                            if (minutes > 0) {
                                timeFormatted += " " + minutes + " min";
                            }
                        } else {
                            timeFormatted = minutes + " min";
                        }

                        editTime.removeTextChangedListener(this);
                        editTime.setText(timeFormatted);
                        editTime.setSelection(timeFormatted.length());
                        editTime.addTextChangedListener(this);
                    }
                }
            });

            btnInsert = findViewById(R.id.btn_insert);
            btnInsert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImagePickDialog();
                }
            });

            btnSubmit = findViewById(R.id.btnSave);
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = editName.getText().toString();
                    String price = editPrice.getText().toString();

                    // Check if the fields are empty
                    if(name.isEmpty()) {
                        editName.setError("Name is required");
                        return;
                    }

                    if(price.isEmpty()) {
                        editPrice.setError("Price is required");
                        return;
                    }
                    //Add submit logic
                    Toast.makeText(getApplicationContext(), "Menu updated successfully", Toast.LENGTH_SHORT).show();
                }
            });

            editPrice.addTextChangedListener(new TextWatcher() {
                private String current = "";

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().equals(current)) {
                        editPrice.removeTextChangedListener(this);

                        String cleanString = s.toString().replaceAll("[Rp,.]", "");

                        double parsed;
                        try {
                            parsed = Double.parseDouble(cleanString);
                        } catch (NumberFormatException e) {
                            parsed = 0.00;
                        }

                        String formatted = NumberFormat.getNumberInstance(Locale.US).format(parsed);

                        current = formatted;
                        editPrice.setText(formatted);
                        editPrice.setSelection(formatted.length());

                        editPrice.addTextChangedListener(this);
                    }
                }
            });
        }

        private void showImagePickDialog() {
            CharSequence[] items = {"Take Photo", "Choose from Gallery"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Photo");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick (DialogInterface dialog, int which) {
                    if (which == 0) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                    } else if (which == 1) {
                        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhotoIntent, PICK_IMAGE);
                    }
                }
            });
            builder.show();
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode ==RESULT_OK) {
                if(requestCode == REQUEST_IMAGE_CAPTURE) {
                    //Handle camera result
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    btnInsert.setImageBitmap(imageBitmap);
                } else if(requestCode == PICK_IMAGE){
                    //Handle gallery result
                    Uri selectedImage = data.getData();
                    btnInsert.setImageURI(selectedImage);
                }
            }
        }

    }