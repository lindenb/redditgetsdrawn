package com.github.lindenb.rgd;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Post
	{
	private enum Action {Undefined,TOXML};
	private static final Logger LOG=Logger.getLogger("rgd");
	final JsonElement root;
	
	public class Submission
		{
		private JsonElement root;
		public class Resolution
			{
			final JsonObject root;
			Resolution(final JsonObject root) {
				this.root = root;
				}
			public int getWidth() {
			return root.get("width").getAsInt();
			}

			public int getHeight() {
			return root.get("height").getAsInt();
			}

			public String getUrl() {
			return root.get("url").getAsString();
			}

			}
		
		Submission(JsonElement root){
			this.root = root;
			//parse arts
			}
		
		public List<Art> getArtList() {
			List<Art> arts = new ArrayList<>();
			final JsonArray array = this.root.getAsJsonArray().get(1).getAsJsonObject().get("data").getAsJsonObject().get("children").getAsJsonArray();
			for(int i=0;i< array.size();++i)
				{
				arts.add(new Art(array.get(i).getAsJsonObject()));
				}
			return arts;
			}
		
		
		private void xml(final XMLStreamWriter out) throws XMLStreamException {
			out.writeStartElement("submission");
			for(Art art:this.getArtList())
				{
				art.xml(out);
				}
			
			out.writeEndElement();
			}

		}
	public class User
		{
		final JsonObject root;
		User(final JsonObject root) {
			this.root = root;
			}
		public String getName() {
			return this.root.get("name").getAsString();
			}
		private void xml(final XMLStreamWriter out) throws XMLStreamException 
			{
			out.writeStartElement("user");
			out.writeEndElement();
			}

		}
	public class Art
		{
		private final JsonObject root;
		Art(final JsonObject root) {
			this.root = root;
			}
		public User getUser() {
			return new User(this.root);
			}
		public String getDate() {
			return this.root.get("created_utc").getAsString();
			}

		public String getId() {
			return this.root.get("link_id").getAsString()+"."+ this.root.get("id");
			}
		
		public String getImgurURL() {
			return Post.getImgurURL(this.root.get("body").getAsString());
			}
		
		private void xml(final XMLStreamWriter out) throws XMLStreamException 
			{
			out.writeStartElement("art");
			
			out.writeEndElement();
			}
		
		}
	
	private Post(final JsonElement root)
		{
		this.root = root;
		
		
		}
	
	
	
	private static String getImgurURL(final String s) {
		if( s==null) return null;
		for(final String u : s.split("[^\\:a-zA-Z\\/\\.0-9]+"))
			{
	
			if(( u.startsWith("http://") || u.startsWith("https://")) && u.contains("imgur.com/") )	
				{
				return u;
				}
			}
		return null;
		}
	
	private static class ImageInfo
		{
		String url;
		int width;
		int height;
		}
	
	private static ImageInfo getImageInfo(String u) throws IOException {
		ImageInputStream is = ImageIO.createImageInputStream(new URL(u).openStream());
		ImageInfo img = new ImageInfo();
		img.url = u;
		Iterator<ImageReader> iter = ImageIO.getImageReaders(is);
		if (iter.hasNext()) {
		ImageReader reader = iter.next();
	            reader.setInput(is);
	            img.width = reader.getWidth(0);
	            img.height = reader.getHeight(0);
	            } 
		is.close();
		return img;	
		}

	public Submission getSubmission() {
		return null;
		}
	
	public boolean isSubmission()  {
	return false;
	}
	
	public static Post parse(final File  file) throws IOException {
		final Reader r= (file==null?new InputStreamReader(System.in): new FileReader(file));
		final JsonParser parser = new JsonParser();
		final JsonElement root=parser.parse(r);
		r.close();
		return new Post(root);
		}
	
	
	private void xml(final XMLStreamWriter out) throws XMLStreamException {
		if( isSubmission()) {
			out.writeStartElement("submission");
			Submission submission = getSubmission();
			for(Art art:this.getArt())
				{
				art.xml(out);
				}
			
			out.writeEndElement();
			}
		
		}
	public static void main(final String[] args) throws Exception
		{
		Action action =Action.Undefined;
		int optind=0;
		while(optind< args.length)
				{
				if(args[optind].equals("-h") ||
				   args[optind].equals("-help") ||
				   args[optind].equals("--help"))
					{
					System.err.println("Pierre Lindenbaum PhD. 2016");
					System.err.println("Options:");
					System.err.println(" -h help; This screen.");
					System.err.println(" -L (leval) log level");
					System.err.println(" -a (action) "+Action.TOXML);
					return;
					}
				else if(args[optind].equals("-L") && optind+1< args.length)
					{
					LOG.setLevel(Level.parse(args[++optind]));
					}
				else if(args[optind].equals("-a") && optind+1< args.length)
					{
					action = Action.valueOf(args[++optind].toUpperCase());
					}
				else if(args[optind].equals("--"))
					{
					optind++;
					break;
					}
				else if(args[optind].startsWith("-"))
					{
					System.err.println("Unknown option "+args[optind]);
					return;
					}
				else 
					{
					break;
					}
				++optind;
				}
			switch(action)
				{
				case TOXML:
					{
					XMLOutputFactory xof=XMLOutputFactory.newFactory();
					XMLStreamWriter out=xof.createXMLStreamWriter(System.out, "UTF-8");
					out.writeStartDocument("UTF-8", "1.0");
					out.writeStartElement("rgd");
					if(optind==args.length) {
						Post post = Post.parse(null);
						post.xml(out);
						}
					else
						{
						while(optind < args.length) {
							final File jsonFile = new File(args[optind++]);
							LOG.info("parse :"+ jsonFile);
							Post post = Post.parse(jsonFile);
							post.xml(out);
							}
						}
					out.writeEndElement();
					out.writeEndDocument();
					out.flush();
					out.close();
					break;
					}
				default:
					{
					System.err.println("Cannot do that "+action);
					return;
					}
				}
		
			
 			}
		
		}
