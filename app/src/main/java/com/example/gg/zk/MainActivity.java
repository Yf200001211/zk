package com.example.gg.zk;

import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import FocusBean.FocusBean;


public class MainActivity extends AppCompatActivity {
    private List<FocusBean.ResultBean.DataBean> resultBean;
    private ListView listview;
    public String url = "http://result.eolinker.com/k2BaduF2a6caa275f395919a66ab1dfe4b584cc60685573?uri=tt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new MyAsyn().execute();
    }
        class MyAsyn extends AsyncTask<Integer,Integer,String>{

            private View viewById;
            //耗时操作
            @Override
            protected String doInBackground(Integer... integers) {
                String messages="";

                try {
                    URL murl = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) murl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5 * 1000);
                    connection.setReadTimeout(5*1000);
                    if (connection.getResponseCode()==200){
                        InputStream inputStream = connection.getInputStream();
                        byte[] b = new byte[1024 * 512];
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int len=0;
                        while ((len=inputStream.read(b))!=-1){
                            baos.write(b,0,len);
                        }
                        messages = baos.toString();
                        inputStream.close();
                        connection.disconnect();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return messages;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s!=null){
                        Gson gson = new Gson();
                        FocusBean focusBean = gson.fromJson(s, FocusBean.class);
                    resultBean = focusBean.getResult().getData();
                    if (resultBean !=null){
                        listview.setAdapter(new BaseAdapter() {
                            public View getView(int i, View view, ViewGroup viewGroup) {
                                ViewHolper holper;
                                if (view==null){
                                    view=View.inflate(MainActivity.this,R.layout.listview,null);
                                    holper = new ViewHolper();
                                    TextView textView1 =holper.textView1=view.findViewById(R.id.textView1);
                                    TextView textView2 =holper.textView2=view.findViewById(R.id.textView2);
                                    TextView textView3 =holper.textView3=view.findViewById(R.id.textView3);
                                    ImageView imageView =holper.imageView = view.findViewById(R.id.imageView);
                                    view.setTag(holper);
                                }else{
                                    holper= (ViewHolper) view.getTag();
                                }
                                holper.textView1.setText(resultBean.get(i).getAuthor_name());
                                holper.textView2.setText(resultBean.get(i).getTitle());
                                holper.textView3.setText(resultBean.get(i).getDate());

                                return view;
                            }
                            class ViewHolper {
                                TextView  textView1,textView2,textView3;
                                ImageView imageView;
                            }

                    }
                }
            }
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                Toast.makeText(MainActivity.this, "加载中..."+values.toString(), Toast.LENGTH_LONG).show();
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                listview = findViewById(R.id.listview);
            }
        }
    }
    private class Myadapter1 extends BaseAdapter {

        @Override
        public int getCount() {
            return resultBean.size();
        }

        @Override
        public Object getItem(int i) {
            return resultBean.get(i);
        }

        @Override
        public long getItemId(int i) {

            return i;
        }
        @Override

    }

