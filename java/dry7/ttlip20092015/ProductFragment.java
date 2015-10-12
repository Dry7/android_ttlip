package dry7.ttlip20092015;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ProductFragment extends Fragment {

    ImageView productImageView;
    TextView productNameView;
    TextView productPriceView;
    WebView productDescriptionView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product, null);

        productImageView = (ImageView)view.findViewById(R.id.productImage);
        productNameView = (TextView)view.findViewById(R.id.productName);
        productPriceView = (TextView)view.findViewById(R.id.productPrice);
        productDescriptionView = (WebView)view.findViewById(R.id.productDescription);

        new HttpRequestTask().execute(getArguments().getInt("product"));

        return view;
    }

    private void showProduct(Product product)
    {
        if (product.getImage() != null && !product.getImage().equals("")) {
            Picasso.with(ProductFragment.this.getActivity()).load(product.getImage()).resize(300, 300).into(productImageView);
        }

        productNameView.setText(product.getName());
        productPriceView.setText(product.getPrice() + " р.");
        productDescriptionView.loadData(product.getDescription(), "text/html; charset=UTF-8", null);
    }

    class HttpRequestTask extends AsyncTask<Integer, Void, String>
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";

        @Override
        protected String doInBackground(Integer... params) {
            try {
                Integer product;
                try {
                    product = params[0];
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
