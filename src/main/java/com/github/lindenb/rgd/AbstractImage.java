package com.github.lindenb.rgd;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public abstract class AbstractImage {
 private static final Logger LOG=Logger.getLogger("AbstractImage");

protected ImageInfo imageInfo=null;
public abstract  User getUser();

protected abstract String getImgurUrl() ;

public final  ImageInfo getImageInfo(){
	if(this.imageInfo!=null) return this.imageInfo;
	this.imageInfo=new ImageInfo();
	String urlstr = getImgurUrl();
	if( urlstr == null) return this.imageInfo;
	this.imageInfo = ImageInfo.getImageInfo(urlstr);
	return this.imageInfo;
	}
public final  String getImgurPage() {
	String url = this.getImgurUrl();
	if(url==null) return null;
	int dot = url.lastIndexOf('.');
	return url.substring(0,dot);
	}

public final String getBigSquareUrl() {
	String url = this.getImgurUrl();
	if(url==null) return null;
	int dot = url.lastIndexOf('.');
	if(dot==-1) return null;
	return url.substring(0,dot)+"b"+url.substring(dot);
}

public static String fixImgurUrl(final String s) {
	if( s==null) return null;
	for(final String u : s.split("[^\\:a-zA-Z\\/\\.0-9?]+"))
		{

		if(( u.startsWith("http://") || u.startsWith("https://")) && u.contains("imgur.com/") )	
			{
			LOG.info("Got url "+u);
			if(u.endsWith(".jpg") || u.endsWith(".png")) 
				{
				LOG.info("A2");
				return u;
				}
			for(int i=0;i< 2;++i)
				{
				String ext=(i==0?".jpg":".png");
				HttpURLConnection huc=null;
				try {
				    URL url2 = new URL(u+ext); 
				    LOG.info("Trying "+url2);
				    huc =  (HttpURLConnection) url2.openConnection(); 
				    huc.setRequestMethod("GET"); 
				    huc.connect(); 
				    if( huc.getResponseCode() == HttpURLConnection.HTTP_OK) return u+ext;
				} catch (Exception e) {
					
				} finally
					{
					if(huc!=null) huc.disconnect();
					}
				}
			return null;
			}
		}
	
	return null;
	}


}
