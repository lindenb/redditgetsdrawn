package com.github.lindenb.rgd;

import java.util.Date;
import java.util.logging.Logger;

import com.google.gson.JsonElement;

public class Art extends AbstractImage
	{
	private static final Logger LOG=Logger.getLogger("rgd");
	private final JsonElement root;
	private String cacheimgur=null;
	private final Submission submission;
	Art(final JsonElement root,Submission sub)  {
		this.root=root;
		this.submission = sub;
	}
	
	public Submission getSubmission() {
		return submission;
	}
	
	public User getUser(){
		JsonElement o = this.root;
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("data")) return null;
		o = o.getAsJsonObject().get("data");
		return new User(o);
	}
	public Date getDate(){
		JsonElement o = this.root;
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("data")) return null;
		o = o.getAsJsonObject().get("data");
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("created_utc")) return null;
		o = o.getAsJsonObject().get("created_utc");

		if(o==null || !o.isJsonPrimitive() || !o.getAsJsonPrimitive().isNumber()) return null;
		Number n= o.getAsJsonPrimitive().getAsNumber(); /* 1471492095 */
		return new java.util.Date(n.longValue() * 1000L);
		}
	
	@Override
	protected String getImageUrl() {
		if(this.cacheimgur!=null) return this.cacheimgur.isEmpty()?null:this.cacheimgur;
		this.cacheimgur="";
		JsonElement o = this.root;
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("data")) {
			LOG.info("no data");
			return null;
		}
		o = o.getAsJsonObject().get("data");
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("body"))  {
			LOG.info("no body");
			return null;
		}
		o = o.getAsJsonObject().get("body");

		if(o==null || !o.isJsonPrimitive() || !o.getAsJsonPrimitive().isString())  {
			LOG.info("no body as string");
			return null;
		}
		String body= o.getAsJsonPrimitive().getAsString();
		
		this.cacheimgur = new ImageInfoFactory().fixImageUrl(body);
		if( this.cacheimgur==null) this.cacheimgur="";
		return this.cacheimgur.isEmpty()?null:this.cacheimgur;
		}
	@Override
	public String toString() {
		return "art by" +getUser()+ " date:"+getDate() +" image:"+this.getImageInfo();
		}
	
	@Override
	public  String getImagePage() {
		final String url = this.getImageUrl();
		if(url==null ) return null;
		if(!url.contains("imgur.com")) return url;
		final int dot = url.lastIndexOf('.');
		return url.substring(0,dot);
		}

	}
