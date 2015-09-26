package dry7.ttlip20092015;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import dry7.ttlip20092015.Models.Product;

public class ProductActivity extends Activity {

    ImageView productImageView;
    TextView productNameView;
    TextView productPriceView;
    WebView productDescriptionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product);

        productImageView = (ImageView)findViewById(R.id.productImage);
        productNameView = (TextView)findViewById(R.id.productName);
        productPriceView = (TextView)findViewById(R.id.productPrice);
        productDescriptionView = (WebView)findViewById(R.id.productDescription);

        new HttpRequestTask().execute();
    }

    private void showProduct(Product product)
    {
        if (product.getImage() != null && !product.getImage().equals("")) {
            Picasso.with(ProductActivity.this).load(product.getImage()).resize(300, 300).into(productImageView);
        }

        productNameView.setText(product.getName());
        productPriceView.setText(product.getPrice() + " р.");
        productDescriptionView.loadData(product.getDescription(), "text/html; charset=UTF-8", null);
    }

    class HttpRequestTask extends AsyncTask<Void, Void, String>
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Void... params) {
            try {
                Integer product;
                try {
                    product = Integer.valueOf(ProductActivity.this.getIntent().getStringExtra("product"));
                } catch (Exception e) {
                    product = 0;
                    e.printStackTrace();
                }

                URL url = new URL(String.format("http://ios.gifts48.ru/products/%s", product));
                Log.d("myLogs", String.format("http://ios.gifts48.ru/products/%s", product));

                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) { buffer.append(line); }

                resultJson = buffer.toString();
            } catch (Exception e) {
                Log.d("myLogs", "error " + e.getMessage());
                e.printStackTrace();
            }

            return resultJson;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject dataJsonObj = new JSONObject(s);

                Product product = new Product(dataJsonObj.getString("id_product"), dataJsonObj.getString("price"), dataJsonObj.getString("name"), dataJsonObj.getString("description"), dataJsonObj.getString("image"));

                showProduct(product);
            } catch (Exception e) {
                Log.d("myLogs", "error2 " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
