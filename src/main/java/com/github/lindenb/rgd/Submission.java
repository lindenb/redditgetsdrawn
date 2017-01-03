package com.github.lindenb.rgd;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class Submission  extends AbstractImage
	{
	private static final Logger LOG=Logger.getLogger("Submission");
	private final JsonElement root;
	private String cacheimgur=null;
	public Submission(final JsonElement root) {
		this.root = root;
		}
	
	
	public Date getDate() {
			JsonElement o = this.root;

			if(o==null || !o.isJsonArray()) return null;
			if(o==null || !o.isJsonArray() ||  o.getAsJsonArray().size() <= 0 ) return null;
			o = o.getAsJsonArray().get(0);

			if(o==null || !o.isJsonObject()) return null;
			if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("data")) return null;
			o = o.getAsJsonObject().get("data");

			if(o==null || !o.isJsonObject()) return null;
			if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("children")) return null;
			o = o.getAsJsonObject().get("children");

			if(o==null || !o.isJsonArray()) return null;
			if(o==null || !o.isJsonArray() ||  o.getAsJsonArray().size() <= 0 ) return null;
			o = o.getAsJsonArray().get(0);

			if(o==null || !o.isJsonObject()) return null;
			if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("data")) return null;
			o = o.getAsJsonObject().get("data");

			if(o==null || !o.isJsonObject()) return null;
			if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("created_utc")) return null;
			o = o.getAsJsonObject().get("created_utc");

			if(o==null || !o.isJsonPrimitive() || !o.getAsJsonPrimitive().isNumber()) return null;
			Number n= o.getAsJsonPrimitive().getAsNumber(); /* 1471492095 */
			return new java.util.Date(n.longValue() * 1000L);
			}
	
	@Override
	public User getUser() {
		JsonElement o = this.root;

		if(o==null || !o.isJsonArray()) return null;
		if(o==null || !o.isJsonArray() ||  o.getAsJsonArray().size() <= 0 ) return null;
		o = o.getAsJsonArray().get(0);

		if(o==null || !o.isJsonObject()) return null;
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("data")) return null;
		o = o.getAsJsonObject().get("data");

		if(o==null || !o.isJsonObject()) return null;
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("children")) return null;
		o = o.getAsJsonObject().get("children");

		if(o==null || !o.isJsonArray()) return null;
		if(o==null || !o.isJsonArray() ||  o.getAsJsonArray().size() <= 0 ) return null;
		o = o.getAsJsonArray().get(0);

		if(o==null || !o.isJsonObject()) return null;
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("data")) return null;
		o = o.getAsJsonObject().get("data");
		return new User(o);
		}
	
	
	@Override
	protected String getImgurUrl() {
		if(this.cacheimgur!=null) return this.cacheimgur.isEmpty()?null:this.cacheimgur;
		this.cacheimgur="";
		JsonElement o = this.root;
		if(o==null || !o.isJsonArray() ||  o.getAsJsonArray().size() <= 0 ) return null;
		o = o.getAsJsonArray().get(0);
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("data")) return null;
		o = o.getAsJsonObject().get("data");
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("children")) return null;
		o = o.getAsJsonObject().get("children");
		if(o==null || !o.isJsonArray() ||  o.getAsJsonArray().size() <= 0 ) return null;
		o = o.getAsJsonArray().get(0);

		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("data")) return null;
		o = o.getAsJsonObject().get("data");
		
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("url")) return null;
		o = o.getAsJsonObject().get("url");
		
		if(o==null || !o.isJsonPrimitive() || !o.getAsJsonPrimitive().isString()) return null;
		String body= o.getAsJsonPrimitive().getAsString();
		this.cacheimgur = AbstractImage.fixImgurUrl(body);
		if(this.cacheimgur==null) this.cacheimgur="";
		return this.cacheimgur.isEmpty()?null:this.cacheimgur;
		}
	
	
	public List<Art> getArts() {
		final List<Art> L= new ArrayList<Art>();
		JsonElement o = this.root;

		if(o==null || !o.isJsonArray() ||  o.getAsJsonArray().size() <= 1 ) return L;
		o = o.getAsJsonArray().get(1);

		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("data")) return L;
		o = o.getAsJsonObject().get("data");

		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("children")) return L;
		o = o.getAsJsonObject().get("children");

		if(o==null || !o.isJsonArray()) return L;
		final JsonArray array = o.getAsJsonArray();
		for(int i=0;i< array.size();++i)  {
			final Art a=new Art(array.get(i));
			L.add(a);
			}
		return L;
		}
	
	public static Submission parse(final String  file) throws IOException {
		LOG.info("Read "+file);
		final Reader r= (file==null?new InputStreamReader(System.in): new FileReader(file));
		final JsonParser parser = new JsonParser();
		final JsonElement root=parser.parse(r);
		r.close();
		return new Submission(root);
		}

	@Override
	public String toString() {
		return "submission:"+getImageInfo()+" "+getUser()+" "+getDate()+" "+getArts();
		}
	
	public static void main(String[] args) throws IOException {
		for(final String s : args) {
			Submission sub = Submission.parse(s);
			LOG.info(sub.toString());
			}
		}
	}
