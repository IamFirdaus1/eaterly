package com.dausinvestama.eaterly;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SellerInsertMenu extends AppCompatActivity {

    private EditText editPrice, editName, editDescription, editTime;
    private ImageButton btnInsert, btnBack;
    Button btnSubmit;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;
    private static final String MENU_IMAGE_PATH = "menu_images/";
    private Integer canteenId = null;

    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_insert_menu);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        editName = findViewById(R.id.editName);
        editDescription = findViewById(R.id.editDescription);
        editPrice = findViewById(R.id.editPrice);
        editTime = findViewById(R.id.editTimeEstimation);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
                String name = editName.getText().toString().trim();
                String description = editDescription.getText().toString().trim();
                String price = editPrice.getText().toString().trim();
                String timeEstimation = (editTime.getText().toString() + " Min").trim();

                // Check if the fields are empty
                if (name.isEmpty() || price.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Name and Price are required", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Upload menu data to Firestore
                uploadMenuData(name, description, price, timeEstimation);
            }
        });
    }

    private void uploadMenuData(String name, String description, String price, String timeEstimation) {
        // Remove commas from the price string
        String cleanPrice = price.replaceAll(",", "");

        String sellerId = currentUser.getUid();

        Task<QuerySnapshot> canteens = db.collection("canteens")
                .whereEqualTo("seller", sellerId)
                .limit(1)
                .get();

        canteens.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // Retrieve the first document and get its ID
                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                    canteenId = Integer.parseInt(documentSnapshot.getId());

                    // Use an executor to perform the getNextDocumentName operation in the background
                    Executor executor = Executors.newSingleThreadExecutor();
                    Handler handler = new Handler(Looper.getMainLooper());

                    executor.execute(() -> {
                        try {
                            String menuId = getNextDocumentName();
                            Log.d(TAG, "NextDoc: " + menuId);

                            // Post the result back to the main thread to continue with Firestore update
                            handler.post(() -> {
                                // Create a map with menu data
                                Map<String, Object> menuData = new HashMap<>();
                                menuData.put("name", name);
                                menuData.put("description", description);
                                menuData.put("price", Double.parseDouble(cleanPrice)); // Use the cleaned price
                                menuData.put("timeEstimation", timeEstimation);
                                menuData.put("sellerId", currentUser.getUid());
                                menuData.put("canteen_id", canteenId);
                                menuData.put("url", "");

                                // Add the menu data to Firestore
                                db.collection("menus").document(menuId)
                                        .set(menuData)
                                        .addOnSuccessListener(aVoid -> {
                                            // Menu data added successfully, now upload the image
                                            uploadMenuImage(menuId);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getApplicationContext(), "Failed to add menu data", Toast.LENGTH_SHORT).show();
                                        });
                            });
                        } catch (InterruptedException | ExecutionException e) {
                            // Handle exceptions here
                            handler.post(() -> {
                                Toast.makeText(getApplicationContext(), "Failed to generate a new menu ID: ", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.getLocalizedMessage());
                            });
                        }
                    });
                }
            }
        });
    }

    private void uploadMenuImage(final String menuId) {
        btnInsert.setDrawingCacheEnabled(true);
        btnInsert.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) btnInsert.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Define the storage reference
        StorageReference storageRef = storage.getReference();
        StorageReference menuImageRef = storageRef.child(MENU_IMAGE_PATH + menuId + ".jpg");

        // Upload the image to Firebase Storage
        UploadTask uploadTask = menuImageRef.putBytes(data);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Image uploaded successfully, update Firestore with the image URL
                updateMenuImageUrl(menuId, menuImageRef);
            } else {
                Toast.makeText(getApplicationContext(), "Failed to upload menu image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMenuImageUrl(String menuId, StorageReference menuImageRef) {
        menuImageRef.getDownloadUrl().addOnSuccessListener(uri -> { // Update Firestore with the image URL
            db.collection("menus").document(menuId).update("url", uri.toString()).addOnSuccessListener(aVoid -> {
                Toast.makeText(getApplicationContext(), "Menu added successfully", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Failed to update menu image URL", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void showImagePickDialog() {
        CharSequence[] items = {"Take Photo", "Choose from Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo");
        builder.setItems(items, (dialog, which) -> {
            if (which == 0) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } else if (which == 1) {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhotoIntent, PICK_IMAGE);
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                //Handle camera result
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                btnInsert.setImageBitmap(imageBitmap);
            } else if (requestCode == PICK_IMAGE) {
                //Handle gallery result
                Uri selectedImage = data.getData();
                btnInsert.setImageURI(selectedImage);
            }
        }
    }

    public String getNextDocumentName() throws InterruptedException, ExecutionException {
        Query lastDocumentQuery = FirebaseFirestore.getInstance()
                .collection("menus")
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                .limit(1);

        // Wait for the query to complete and retrieve the result synchronously
        QuerySnapshot querySnapshot = Tasks.await(lastDocumentQuery.get());

        String nextDocumentName = "1"; // Default if no documents are found
        if (!querySnapshot.isEmpty()) {
            String highestDocumentName = querySnapshot.getDocuments().get(0).getId();
            int incrementedValue = Integer.parseInt(highestDocumentName) + 1;
            nextDocumentName = Integer.toString(incrementedValue);
        }

        return nextDocumentName;
    }
}