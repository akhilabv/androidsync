package com.example.androidsync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("NewApi")
public class MainActivity extends Activity {
private TextView tv;
private EditText et;
private Button btn;
private SQLiteDatabase db;
private DBahelper myDbHelper ;
private HttpClient httpc = new DefaultHttpClient();
private String insert= "INSERT INTO test_akhila (`key`,`name`) values ";
//httppost is the object which can make the POST request to any url mentioned in its constructor
private HttpPost httppost = new HttpPost("http://nammaapp.in/namma7817/scripts7817/test.php");
//private HttpResponse resp = null;
//private BufferedReader br=null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    myDbHelper = new DBahelper(this);
		tv = (TextView)findViewById(R.id.textView1);
		et =(EditText) findViewById(R.id.editText1);
		btn = (Button) findViewById(R.id.button1);
		//instance of DB
		 try {

		     	myDbHelper.createDataBase();

			} catch (IOException ioe) {

				throw new Error("Unable to create database");

			}

			try {

				myDbHelper.openDataBase();
				
				}catch(SQLException sqle){

				throw sqle;

				}
		btn.setOnClickListener(new View.OnClickListener(){
			public void onClick(View arg)
			{
				
				Intent i = new Intent(MainActivity.this,Async.class);
				startActivity(i);
				
				try
				{
				//new DownloadWebPageTask().execute();
					db = myDbHelper.getWritableDatabase();
					db.close();
				//httpcall();
				}
				catch(Exception e)
				{
					tv.setText(tv.getText().toString()+e.toString()+"1");
				}
			}
		});
		//instance of DB
		 
	}
	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
	    @Override
	    protected String doInBackground(String... urls) {
	      String response = "";
	      for (String url : urls) {
	        DefaultHttpClient client = new DefaultHttpClient();
	        HttpGet httpGet = new HttpGet("http://nammaapp.in/namma7817/scripts7817/test.php");
	        try {
	        	Cursor c = db.rawQuery("SELECT  max(`key`) as maxm FROM  `test_akhila`", null);
				String str1=null;
				if(c.moveToFirst())
				{
					str1=c.getString(c.getColumnIndex("maxm")).toString();
				}

			String query = String.format("key=%s", URLEncoder.encode(str1, "UTF-8"));
			
	          HttpResponse execute = client.execute(httpGet);
	          InputStream content = execute.getEntity().getContent();

	          BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
	          String s = "akhila";
	          while ((s = buffer.readLine()) != null) {
	            response += s;
	          }

	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	      return response;
	    }

	    @Override
	    protected void onPostExecute(String result) {
	      tv.setText(result);
	    }
	  }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@SuppressLint("NewApi")
	public void httpcall()
	{

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		httppost.setHeader("Connection", "keep-alive");
		//setting the header of the content-type (in MIME representation) of the data i am sending on this POST request
		httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		//string query becomes the POST body where u url-encode param=value pairs (n number of such pairs can be made)
		String query=null;
		try {
			Cursor c = db.rawQuery("SELECT  max(`key`) as maxm FROM  `test_akhila`", null);
			String str1=null;
			if(c.moveToFirst())
			{
				str1=c.getString(c.getColumnIndex("maxm")).toString();
			}

			query = String.format("key=%s", URLEncoder.encode(str1, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			tv.setText(tv.getText().toString()+e.toString()+"2");
		}
		//attaching the query string to the post request
		try {
			httppost.setEntity(new StringEntity(query));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			tv.setText(tv.getText().toString()+e.toString()+"3");
		}
		HttpResponse resp = null;
		//executing the request
		try {
			resp = httpc.execute(httppost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			tv.setText(tv.getText().toString()+e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			tv.setText(tv.getText().toString()+e.toString()+"4");
		}
		//getting an handle on the response that is being recieved from the server (echo 'xyz'; on server will reflect 'xyz' here)
		BufferedReader br=null;
		try {
			br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			tv.setText(tv.getText().toString()+e.toString()+"5");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			tv.setText(tv.getText().toString()+e.toString()+"6");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			tv.setText(tv.getText().toString()+e.toString()+"7");
		}
		String htmltext = new String();
		String str;
		try {
			while ((str = br.readLine()) != null) { 
							htmltext +=str;
			}
			try
			{
				
				if(htmltext.contentEquals("sync"))
				{
					tv.setText("already in sync");
				}
				else
				{
				//	ContentValues insertValues = new ContentValues();
					/*insertValues.put("key","4");
					insertValues.put("name","khkh");
					db.insert("test_akhila",null,insertValues);*/
					tv.setText(htmltext);
					StringTokenizer st = new StringTokenizer(htmltext, "||");
					while (st.hasMoreTokens()) {
					db.execSQL(insert+st.nextToken()+";");
				    }
					db.close();
				tv.setText("now in sync  " + htmltext);
				}
				 
					
			}
			catch(Exception e)
			{
				tv.setText(tv.getText().toString()+e.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			tv.setText(tv.getText().toString()+e.toString()+"8");
		}

	}}