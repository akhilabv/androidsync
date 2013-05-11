package com.example.androidsync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class Async extends Activity {
 Button btn;
 EditText etxt;
 private String insert= "INSERT INTO test_akhila (`key`,`name`) values ";
private SQLiteDatabase db;
private DBahelper myDbHelper ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_async);
		myDbHelper = new DBahelper(this);
		btn=(Button) findViewById(R.id.button1);
		etxt=(EditText) findViewById(R.id.editText1);
		
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
			
			
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				new AsyncTask<String,Void,String>(){

					@Override
					protected void onPreExecute()
					{
						etxt.setText("checking for synchronization....");
					}
					@Override
					protected String doInBackground(String... arg0) {
						// TODO Auto-generated method stub
						db = myDbHelper.getWritableDatabase();
						String content = httpcall();
						return content;
					}
					
					@Override
					protected void onPostExecute(String content)
					{
						etxt.setText(content);
					}
					
				}.execute();
			}
		});
		
	}
	public String httpcall()
	{

		HttpClient httpc = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://nammaapp.in/namma7817/scripts7817/test.php");
		
		
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
			etxt.setText(etxt.getText().toString()+e.toString()+"2");
		}
		//attaching the query string to the post request
		try {
			httppost.setEntity(new StringEntity(query));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			etxt.setText(etxt.getText().toString()+e.toString()+"3");
		}
		HttpResponse resp = null;
		//executing the request
		try {
			resp = httpc.execute(httppost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			etxt.setText(etxt.getText().toString()+e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			etxt.setText(etxt.getText().toString()+e.toString()+"4");
		}
		//getting an handle on the response that is being recieved from the server (echo 'xyz'; on server will reflect 'xyz' here)
		BufferedReader br=null;
		try {
			br = new BufferedReader(new InputStreamReader(resp.getEntity().getContent(),"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			etxt.setText(etxt.getText().toString()+e.toString()+"5");
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			etxt.setText(etxt.getText().toString()+e.toString()+"6");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			etxt.setText(etxt.getText().toString()+e.toString()+"7");
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
					//etxt.setText("already in sync");
					return("already in sync");
				}
				else
				{
				//	ContentValues insertValues = new ContentValues();
					/*insertValues.put("key","4");
					insertValues.put("name","khkh");
					db.insert("test_akhila",null,insertValues);*/
					//etxt.setText(htmltext);
					StringTokenizer st = new StringTokenizer(htmltext, "||");
					while (st.hasMoreTokens()) {
					db.execSQL(insert+st.nextToken()+";");
				    }
					db.close();
					return("now in sync"+htmltext);
					
				
				}
				 
					
			}
			catch(Exception e)
			{
				etxt.setText(etxt.getText().toString()+e.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			etxt.setText(etxt.getText().toString()+e.toString()+"8");
		}
		return "nothing :(";

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.async, menu);
		return true;
	}

}
