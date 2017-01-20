package com.github.lindenb.rgd;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.google.gson.JsonElement;

public class User
	{
	private final JsonElement root;
	public User(final JsonElement root){
		this.root = root;
		}
	
	public String getUrlStr() {
		return "https://www.reddit.com/user/"+getName();
	}
	
	public String getName() {
		JsonElement o = this.root;
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("author")) return "";
		o = o.getAsJsonObject().get("author");

		if(o==null || !o.isJsonPrimitive() || !o.getAsJsonPrimitive().isString()) return "";
		return o.getAsJsonPrimitive().getAsString();
		}	
	@Override
	public int hashCode() {
		return getName().hashCode();
		}
	
	@Override
	public boolean equals(Object obj) {
		if(obj==this) return true;
		if(obj==null || !(obj instanceof User)) return false;
		return User.class.cast(obj).getName().equals(this.getName());
		}
	
	@Override
	public String toString() {
		return "User:"+getName();
		}
	
	public void writeHtmlHyperlink(final XMLStreamWriter w) throws XMLStreamException{
		w.writeStartElement("a");
		w.writeAttribute("href", this.getUrlStr());
		w.writeAttribute("title", getName());
		w.writeCharacters(this.getName());
		w.writeEndElement();
		}
	
	}
