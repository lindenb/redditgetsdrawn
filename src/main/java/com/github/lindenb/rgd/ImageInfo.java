package com.github.lindenb.rgd;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public interface ImageInfo
	{
	public String getUrl();
	public int getWidth();
	public int getHeight();
	public ImageInfo toBigSquareImageInfo();
	public boolean isValid();
	public String toBase64() throws IOException;
	
	
	default public void htmlImg(final XMLStreamWriter w) throws XMLStreamException
		{
		if(isValid()){
			w.writeEmptyElement("img");
			w.writeAttribute("src", getUrl());
			w.writeAttribute("width", String.valueOf(getWidth()));
			w.writeAttribute("height", String.valueOf(getHeight()));
			}
		else
			{
			w.writeComment("invalid image");
			}
		}
	}
