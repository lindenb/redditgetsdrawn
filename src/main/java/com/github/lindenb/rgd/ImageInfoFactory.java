package com.github.lindenb.rgd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageInfoFactory {
private static final ImageInfo NO_IMAGE= new ImageInfo()
		{
		@Override
		public ImageInfo toBigSquareImageInfo() {
			return this;
		}
		
		@Override
		public boolean isValid() {
			return false;
		}
		
		@Override
		public int getWidth() {
			return -1;
		}
		
		@Override
		public String getUrl() {
			return "";
		}
		
		@Override
		public int getHeight() {
			return -1;
		}
		@Override
		public String toString() {
				return "[invalid image info]";
			}
	};
	

private static final Logger LOG=Logger.getLogger("ImageInfoFactory");
public static final int IMGUR_BIGSQUARE_SIZE=160;

public ImageInfoFactory()
	{
	
	}



public static ImageInfo getNoImageInfo() {
	return NO_IMAGE;
}

private boolean isImageUrl( String url)
	{
	if(url==null) return false;
	int p0= url.lastIndexOf('?');
	url=p0==-1?url:url.substring(0,p0);
	if(!(url.startsWith("http://") || url.startsWith("https://")))
		{
		return false;
		}
	url=url.toLowerCase();
	return url.endsWith(".png") ||
		   url.endsWith("jpg") ||
		   url.endsWith("jpeg")
		   ;
	}

public ImageInfo get(String url) {
	if(url==null || url.isEmpty()) return null;
	LOG.info("get "+url);
	if(url.contains("imgur.com"))
		{
		if(isImageUrl(url)) {
			return getRealImageUrl(url);
			}
		else
			{
			return getHtmlUrl(url);
			}
		}
	else if(url.contains("tumblr.com"))
		{
		if(isImageUrl(url)) {
			return getRealImageUrl(url);
			}
		else
			{
			return getHtmlUrl(url);
			}
		}
	else
		{
		if(isImageUrl(url)) {
			return getRealImageUrl(url);
			}
		else
			{
			return getHtmlUrl(url);
			}
		}
	}


private ImageInfo getRealImageUrl(final String validImageUrl) {
	URLConnection con=null;
	ImageInputStream is=null;
	try {
		LOG.info("connecting "+validImageUrl);
		con = new URL(validImageUrl).openConnection();
		con.setConnectTimeout(5*1000);
		con.connect();
		is= ImageIO.createImageInputStream(con.getInputStream());
		Iterator<ImageReader> iter = ImageIO.getImageReaders(is);
		if (iter.hasNext()) {
				ImageReader reader = iter.next();
	            reader.setInput(is);
	            final DefaultImageInfo img = new DefaultImageInfo();
	            img.width = reader.getWidth(0);
	            img.height = reader.getHeight(0);
	            img.url = validImageUrl;
	            return img;
	            } 			
		
		}
	catch(IOException err) {
		LOG.info("err "+err);
		return null;	
		}
	finally
		{
		try { is.close();} catch(Exception err2) {}
		}
	return null;	
	}

private static String htmlProp(final String html,String...prop )
	{
	final Set<String> props=new HashSet<>();
	for(String x:prop) props.add("\""+x+"\"");
	for(String component :html.
		replaceAll("[ \n\r\t]+", " ").
		replaceAll("[\']", "\"").
		replace("/>", ">").
		 split("[<>]"))
		{
		if(!component.toLowerCase().startsWith("meta ")) continue;
		final String tokens[] = component.split("[ =]+");
		if(tokens.length<5 || !tokens[0].toLowerCase().equals("meta")) {
			continue;
		}
		boolean is_prop=false;
		for(int i=1;i+1 < tokens.length;++i)
			{
			if((tokens[i].equals("property") || tokens[i].equals("name")) &&
					props.contains(tokens[i+1]))
				{
				is_prop=true;
				break;
				}
			}
		if(!is_prop) continue;
		for(int i=1;i+1 < tokens.length;++i)
			{
			
			if(tokens[i].equals("content"))
				{
				String content= tokens[i+1];
				LOG.info("got "+tokens[i+1]);
				return content.substring(1,content.length()-1);
				}
			
			}
		}
	LOG.info("cannot get property "+ props+ " in HTML############" );
	return null;
	}

/* get meta info in html */
private ImageInfo getHtmlUrl(final String pageurl) {
	InputStream is=null;
	try {
		final String head="</head>";
		LOG.info("opening "+pageurl);
		URLConnection con=new URL(pageurl).openConnection();
		con.setConnectTimeout(5*1000);
		con.connect();
		is = con.getInputStream();
		Reader r = new InputStreamReader(is);
		StringBuilder sb=new StringBuilder();
		int c;
		while((c=r.read())!=-1) {
			sb.append((char)c);
			boolean end_head=false;
			if(sb.length()>head.length()){
				int i=0;
				for(i=0;i< head.length();++i)
					{
					if(sb.charAt((sb.length()-head.length())+i)!=head.charAt(i)) break;
					}
				if(i==head.length()) end_head=true;
			}
			if(end_head) break;
			
		}
		is.close();
		is=null;
		final String html=sb.toString();
		String og_image=htmlProp(html,"og:image","twitter:image"); 
		if(og_image==null || !isImageUrl(og_image)) {
			LOG.info("cannot get image url from "+pageurl);
			return null;
		}
		if(og_image.equals(pageurl)) {
			LOG.info("cannot get image url from "+pageurl);
			return null;
		}
		
		return getRealImageUrl(og_image);
		}
	catch(IOException err) {
		LOG.info("err "+err);
		return null;	
		}
	finally
		{
		try { if(is!=null) is.close();} catch(Exception err2) {}
		}
	}

public  String fixImageUrl(final String s) {
	if( s==null) return null;
	final String tokens[]=s.split("[^\\:a-zA-Z\\/\\.0-9?\\-]+");
	for(final String u : tokens)
		{
		if(isImageUrl(u)) return u;
		}
	for(String host : new String[]{"imgur.com","deviantart.com","tumblr.com"})
	for(final String url : tokens)
		{
		if(!(url.startsWith("http://") || url.startsWith("https://")))
			{
			continue;
			}
		if(host.contains("deviantart") && !url.contains("/art/")) continue;
		if(host.contains("tumblr") && !url.contains("/post/")) continue;
		if(url.contains(host)) return url;
		}
	
	/*
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
	*/
	return null;
	}

private static class DefaultImageInfo
	implements ImageInfo
	{
	private static final Logger LOG=Logger.getLogger("ImageInfo");
	String url=null;
	int x=0;
	int y=0;
	int width=-1;
	int height=-1;
	
	DefaultImageInfo() {}
	@Override
	public String getUrl() {
		return url;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	@Override
	public int getWidth() {
		return width;
	}
	@Override
	public int getHeight() {
		return height;
	}
	@Override
	public ImageInfo toBigSquareImageInfo() {
		if(!isValid()) return new DefaultImageInfo();
		DefaultImageInfo i2 = new DefaultImageInfo();
		
		
		if(url.contains("imgur.com NONONONONONONON"))
			{
			int dot = url.lastIndexOf('.');
			if(dot==-1) return null;
			i2.url= url.substring(0,dot)+"b"+url.substring(dot);
			i2.width=IMGUR_BIGSQUARE_SIZE;
			i2.height=IMGUR_BIGSQUARE_SIZE;
			}
		else
			{
			i2.url=this.url;
			if(getWidth()>getHeight())
				{
				i2.width=IMGUR_BIGSQUARE_SIZE;
				i2.height = (int)((this.height/(double)this.width)*i2.width);
				}
			else
				{
				i2.height=IMGUR_BIGSQUARE_SIZE;
				i2.width = (int)((this.width/(double)this.height)*i2.height);
				}
			}
		return i2;
		}
	
	public boolean isValid() {
		return url!=null && !url.isEmpty() &&  width>1 && height>1;
	}
	
	@Override
	public String toString() {
		return "imageinfo:url="+url+" "+width+"x"+height+" valid:"+isValid();
		}
	}
	
}
