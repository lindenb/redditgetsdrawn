package com.github.lindenb.rgd;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;


public class ImageInfo
	{
	private static final Logger LOG=Logger.getLogger("ImageInfo");
	public static final int IMGUR_BIGSQUARE_SIZE=160;
	String url=null;
	int width=-1;
	int height=-1;

	public String getBigSquareUrl() {
		if(url==null) return null;
		int dot = url.lastIndexOf('.');
		if(dot==-1) return null;
		return url.substring(0,dot)+"b"+url.substring(dot);
	}
	
	public boolean isValid() {
		return url!=null && width>1 && height>1;
	}
	
	@Override
	public String toString() {
		return "imageinfo:url="+url+" "+width+"x"+height+" valid:"+isValid();
		}
	
	private static String getImgurURL(final String s) {
		if( s==null) return null;
		for(final String u : s.split("[^\\:a-zA-Z\\/\\.0-9\\?]+"))
			{
	
			if(( u.startsWith("http://") || u.startsWith("https://")) && u.contains("imgur.com/") )	
				{
				return u;
				}
			}
		return null;
		}
	
	public static ImageInfo getImageInfo(final String u) {
		final ImageInfo img = new ImageInfo();
		if(u==null) return img;
		for(int i=0;i< 3;++i) {		
			ImageInputStream is=null;
			final String u2;
			switch(i)
				{
				case 1: u2= u+".jpg";break;
				case 2: u2= u+".png";break;
				default: u2 =u; break;
				}
			if(!(u2.endsWith(".png") || u2.endsWith(".jpg"))) continue;
			try {
				LOG.info(u2);
				is= ImageIO.createImageInputStream(new URL(u2).openStream());
				Iterator<ImageReader> iter = ImageIO.getImageReaders(is);
				if (iter.hasNext()) {
				ImageReader reader = iter.next();
			            reader.setInput(is);
			            img.width = reader.getWidth(0);
			            img.height = reader.getHeight(0);
			            img.url = u2;
			            } 
				LOG.info("got it "+img);
				break;
				}
			catch(IOException err) {
				LOG.info("err "+err);
				}
			finally
				{
				try { is.close();} catch(Exception err2) {}
				}
			}
		return img;	
		}
	
	}
