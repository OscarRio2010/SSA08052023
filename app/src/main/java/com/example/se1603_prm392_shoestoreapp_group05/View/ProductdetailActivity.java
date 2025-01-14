package com.example.se1603_prm392_shoestoreapp_group05.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.se1603_prm392_shoestoreapp_group05.Data.CartDBHelper;
import com.example.se1603_prm392_shoestoreapp_group05.Data.ProductsDBHelper;
import com.example.se1603_prm392_shoestoreapp_group05.Data.ProductsData;
import com.example.se1603_prm392_shoestoreapp_group05.Model.Cart;
import com.example.se1603_prm392_shoestoreapp_group05.Model.Product;
import com.example.se1603_prm392_shoestoreapp_group05.R;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ProductdetailActivity extends AppCompatActivity {
    private ImageView imageViewProduct;
    private TextView txtPdName;
    private TextView txtPdColor;
    private TextView txtPdSize;
    private TextView txtPdPrice;
    private TextView txtPdDescribe;
    private Spinner spinnerQuantity;
    private NotificationBadge notificationBadge;
    private CartDBHelper cartDBHelper;
    String CHANNEL_ID ="a";
    private List<Cart> cartList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetail);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        txtPdName = findViewById(R.id.txtPdName);
        txtPdColor = findViewById(R.id.txtPdColor);
        txtPdSize = findViewById(R.id.txtPdSize);
        txtPdPrice = findViewById(R.id.txtPdPrice);
        txtPdDescribe = findViewById(R.id.txtPdDescribe);


        notificationBadge = findViewById(R.id.menu_sl);
        List<Product> productList = ProductsData.getSampleProducts();
        // Nhận thông tin sản phẩm từ intent
        Intent intent = getIntent();
        if (intent != null) {
            int productID = intent.getIntExtra("productID", 0);
            String productImage = intent.getStringExtra("productImage");
            String productName = intent.getStringExtra("productName");
            String productPriceStr = intent.getStringExtra("productPrice");
            double productPrice = Double.parseDouble(productPriceStr);
            String productBrand = intent.getStringExtra("productBrand");
            String productDescribe = intent.getStringExtra("productDescribe");
            String productColor = intent.getStringExtra("productColor");
            String productSize = intent.getStringExtra("productSize");

            // Tạo đối tượng Product với các thông tin nhận được
            Product product = new Product(productID, productImage, productName, productPrice, productBrand, productDescribe, productColor, productSize);
            // Hiển thị thông tin sản phẩm
            Picasso.get().load(productImage).into(imageViewProduct);
            txtPdName.setText(productName);
            txtPdColor.setText(productColor);
            txtPdSize.setText(productSize);
            txtPdPrice.setText("$" + String.format("%.2f", productPrice));
            txtPdDescribe.setText(productDescribe);

            ImageView back = findViewById(R.id.backhome);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProductdetailActivity.this, HomeActivity.class);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            Button addToCartButton = findViewById(R.id.addtocart);
            Intent intent1 = new Intent(ProductdetailActivity.this, CartActivity.class);
            Intent intents = new Intent(ProductdetailActivity.this, HomeActivity.class);
            addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Add the product to the cart
                    Cart cartItem = new Cart(0, product, 1);

                    CartDBHelper cartDBHelper = new CartDBHelper(ProductdetailActivity.this);
                    cartDBHelper.insertCart(cartItem);


                    cartList.add(cartItem);


                    intent1.putExtra("cartItem", (Serializable) cartList);
                    intents.putExtra("cartItems", (Serializable) cartList);
                    startActivity(intents);
                    startActivity(intent1);

                    addNotification();
                    // Update the notification badge
                    int cartItemCount = cartDBHelper.getCartCount();
                    notificationBadge.setNumber(cartItemCount);

                    // Display a toast message
                    Toast.makeText(ProductdetailActivity.this, "Product added to cart", Toast.LENGTH_SHORT).show();

                }
            });
            ImageView cartImageView = findViewById(R.id.CartimageView);
            cartImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    intent1.putExtra("cartItem", (Serializable) cartList);
                    startActivity(intent1);
                }
            });
        }
    }
    private void addNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID, "Mascot Notification",
                            NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            NotificationCompat.Builder builder =


                    new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.abc)
                            .setContentTitle("Add To Cart")
                            .setContentText("Add to cart Success");



            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);

            // Add as notification

            manager.notify(0, builder.build());
        }else {return;}
    }
}