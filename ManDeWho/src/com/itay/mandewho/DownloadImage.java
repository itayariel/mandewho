package com.itay.mandewho;

import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadImage extends AsyncTask<String, Void, Bitmap>{

	ImageView imgView;
	public DownloadImage(ImageView imgViewSource)
	{
		imgView=imgViewSource;
	}

	protected Bitmap doInBackground(String... params) {
		String urlstring = params[0];
		Bitmap bmp=null;
		try {
			URL url = new URL(urlstring);
			bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bmp;
	}
	protected void onPostExecute (Bitmap result) {
		imgView.setImageBitmap(result);
	}

}
