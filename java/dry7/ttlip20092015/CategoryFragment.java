package dry7.ttlip20092015;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import dry7.ttlip20092015.Models.Category;
import dry7.ttlip20092015.Models.Product;

public class CategoryFragment extends Fragment {

    public GridView gridCategories;
    public GridView gridProducts;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.category, null);

        gridCategories = (GridView)view.findViewById(R.id.categories);
        gridProducts = (GridView)view.findViewById(R.id.products);

        gridCategories.setNumColumns(getGridSize());
        gridProducts.setNumColumns(getGridSize());

        Bundle bundle = getArguments();

        if (bundle != null) {
            new HttpRequestTask().execute(bundle.getInt("category"), 0);
        } else {
            ((MainActivity)getActivity()).setTitle("Главная");
            new HttpRequestTask().execute(0);
        }

        return view;
    }

    public void showCategories(ArrayList<Category> categories)
    {
        final CategoryAdapter adapter = new CategoryAdapter(getActivity().getApplicationContext(), categories);
        gridCategories.setAdapter(adapter);
        gridCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new CategoryFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("category", Integer.valueOf(adapter.getCategory(position).getId()));
                getActivity().setTitle(adapter.getCategory(position).getName());
                Activity activity = (MainActivity)getActivity();
                activity.setTitle("Сука((");
                Log.d("myLogs", getActivity().toString());
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(adapter.getCategory(position).getName())
                        .commit();
            }
        });

        if (categories.size() > 0) {
            gridCategories.setVisibility(View.VISIBLE);
        } else {
            gridCategories.setVisibility(View.GONE);
        }
    }

    public void showProducts(ArrayList<Product> products)
    {
        final ProductAdapter adapter = new ProductAdapter(getActivity().getApplicationContext(), products);
        gridProducts.setAdapter(adapter);
        gridProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment fragment = new ProductFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("product", Integer.valueOf(adapter.getProduct(position).getId()));
                getActivity().setTitle(adapter.getProduct(position).getName());
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, fragment)
                        .addToBackStack(adapter.getProduct(position).getName())
                        .commit();
            }
        });

        if (products.size() > 0) {
            gridProducts.setVisibility(View.VISIBLE);
        } else {
            gridProducts.setVisibility(View.GONE);
        }
    }

    /**
     * Считаем сколько показывать элементов в строке
     */
    public Integer getGridSize()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels / 300;
    }

    /**
     * Загрузка данных из REST
     */
    class HttpRequestTask extends AsyncTask<Integer, Void, String>
    {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        Integer category;

        @Override
        protected String doInBackground(Integer... params) {
            try {
                try {
                    this.category = params[0];
                } catch (Exception e) {
                    this.category = 0;
                    e.printStackTrace();
                }

                URL url;
                if (this.category > 0) {
                    url = new URL(String.format("http://ios.gifts48.ru/categories/%s", this.category));
                    Log.d("myLogs", String.format("http://ios.gifts48.ru/categories/%s", this.category));
                } else {
                    url = new URL("http://ios.gifts48.ru/categories");
                    Log.d("myLogs", "http://ios.gifts48.ru/categories");
                }

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

            JSONObject dataJsonObj = null;

            try {
                dataJsonObj = new JSONObject(s);
                JSONArray categories = dataJsonObj.getJSONArray("categories");
                JSONArray products = dataJsonObj.getJSONArray("products");

                ArrayList<Category> categoriesList = new ArrayList<Category>();
                ArrayList<Product> productsList = new ArrayList<Product>();

                for (int i = 0; i < categories.length(); i++) {
                    JSONObject category = categories.getJSONObject(i);
                    categoriesList.add(i, new Category(category.getString("id_category"), category.getString("id_parent"), category.getString("name"), category.getString("date_upd"), category.getString("image")));
                }

                showCategories(categoriesList);

                for (int i = 0; i < products.length(); i++) {
                    JSONObject product = products.getJSONObject(i);
                    productsList.add(i, new Product(product.getString("id_product"), product.getString("price"), product.getString("name"), product.getString("description"), product.getString("image")));
                }

                showProducts(productsList);
            } catch (Exception e) {
                Log.d("myLogs", "error2 " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public class CategoryAdapter extends BaseAdapter
    {
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<Category> categories;

        public CategoryAdapter(Context context, ArrayList<Category> categories)
        {
            this.context = context;
            this.categories = categories;
            this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public Object getItem(int position) {
            return categories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.category_item, parent, false);
            }

            Category category = getCategory(position);

            ((TextView)view.findViewById(R.id.categoryName)).setText(category.getName());

            ImageView categoryImage = (ImageView)view.findViewById(R.id.categoryImage);

            if (category.getImage() != null && !category.getImage().equals("")) {
                Picasso.with(context).load(category.getImage()).resize(300, 300).into(categoryImage);
            }

            return view;
        }

        Category getCategory(int position)
        {
            return ((Category)getItem(position));
        }
    }

    public class ProductAdapter extends BaseAdapter
    {
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<Product> products;

        public ProductAdapter(Context context, ArrayList<Product> products)
        {
            this.context = context;
            this.products = products;
            this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Object getItem(int position) {
            return products.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.product_item, parent, false);
            }

            Product product = getProduct(position);

            ((TextView)view.findViewById(R.id.productName)).setText(product.getName());
            ((TextView)view.findViewById(R.id.productPrice)).setText(String.format("%s р.", product.getPrice()));

            ImageView productImage = (ImageView)view.findViewById(R.id.productImage);

            if (product.getImage() != null && !product.getImage().equals("")) {
                Picasso.with(context).load(product.getImage()).resize(300, 300).into(productImage);
            }

            return view;
        }

        Product getProduct(int position)
        {
            return ((Product)getItem(position));
        }
    }
}
