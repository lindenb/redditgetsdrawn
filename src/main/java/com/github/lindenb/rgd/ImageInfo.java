package com.github.lindenb.rgd;



public interface ImageInfo
	{
	public String getUrl();
	public int getWidth();
	public int getHeight();
	public ImageInfo toBigSquareImageInfo();
	public boolean isValid();
	}
