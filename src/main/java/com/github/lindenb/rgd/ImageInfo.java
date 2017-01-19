package com.github.lindenb.rgd;

import java.io.IOException;

public interface ImageInfo
	{
	public String getUrl();
	public int getWidth();
	public int getHeight();
	public ImageInfo toBigSquareImageInfo();
	public boolean isValid();
	public String toBase64() throws IOException;
	}
