package com.github.lindenb.rgd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

public class RgdToHtml {
	private static final Logger LOG=Logger.getLogger("RgdToHtml");
	private List<Submission> submissions=new ArrayList<>();
	private Map<User, Integer> user2count=new HashMap<>();
	
	private void write(XMLStreamWriter out,String url)throws Exception {
		out.writeEmptyElement("img");
		out.writeAttribute("src", url);
		out.writeAttribute("width", ""+ImageInfo.IMGUR_BIGSQUARE_SIZE);
		out.writeAttribute("height",""+ImageInfo.IMGUR_BIGSQUARE_SIZE);

	}
	
	private void run(String[] args) throws Exception {
		for(final String s : args) {
			final Submission sub = Submission.parse(s);
			if(sub.getBigSquareUrl()==null) continue;
			if(sub.getUser().getName().equals("AutoModerator")) continue;
			if(sub.getUser().getName().equals("[deleted]")) continue;
			List<Art> arts = new ArrayList<>(sub.getArts());
			int i=0;
			while(i<arts.size()) {
				final Art art = arts.get(i);
				User u = art.getUser();
				if( u.getName().equals("AutoModerator") ||
					u.getName().equals("[deleted]") ||
					art.getImgurPage()==null ||
					art.getBigSquareUrl()==null)
					{
					arts.remove(i);
					}	
				else
					{
					++i;
					}
				}
			if(arts.isEmpty()) continue;

			for(final Art art: arts) 
				{
				User u = art.getUser();
				Integer c = this.user2count.get(u);
				if(c==null) c=0;
				this.user2count.put(u,c+1);
				}
			this.submissions.add(sub);
			}
		Collections.sort(this.submissions,new Comparator<Submission>() {
			@Override
			public int compare(Submission o1, Submission o2) {
				return o2.getArts().size() - o1.getArts().size();
				}
			});
		
		List<User> users = new ArrayList<>(this.user2count.keySet());
		LOG.info("users="+users);
		Collections.sort(users,new Comparator<User>() {
			@Override
			public int compare(User o1, User o2) {
				return user2count.get(o2) - user2count.get(o1);
				}
			});
		XMLOutputFactory xof=XMLOutputFactory.newFactory();
		XMLStreamWriter w=xof.createXMLStreamWriter(System.out, "UTF-8");
		w.writeStartElement("html");
		w.writeStartElement("body");
		w.writeStartElement("table");
		
		w.writeStartElement("tr");
		w.writeStartElement("th");
		w.writeEndElement();
		for(User user:users) {
			w.writeStartElement("th");
			w.writeStartElement("a");
			w.writeAttribute("href", user.getUrlStr());
			w.writeCharacters(user.getName());
			w.writeEndElement();
			w.writeEndElement();
			}
		w.writeEndElement();//tr
		for(final Submission sub:this.submissions)
			{
			w.writeStartElement("tr");
			w.writeStartElement("th");
			w.writeStartElement("a");
			write(w,sub.getBigSquareUrl());
			w.writeEndElement();
			w.writeEndElement();//tr
			
			for(User user:users) {
				Art a=null;
				for(Art art:sub.getArts()) {

				if( art.getUser().getName().equals("AutoModerator") ||
                                        art.getUser().getName().equals("[deleted]") ||
                                        art.getImgurPage()==null ||
                                        art.getBigSquareUrl()==null) continue;

					if(art.getUser().equals(user)) {
						a=art;
						break;
					}
				}
				if(a==null) {
					w.writeStartElement("td");
					w.writeEndElement();
				} else
				{
					w.writeStartElement("td");
					w.writeStartElement("a");
					w.writeAttribute("href",""+ a.getImgurPage());
				write(w,a.getBigSquareUrl());
				w.writeEndElement();
				w.writeEndElement();
				}
				}
			
			w.writeEndElement();//tr
			}
		
		
		w.writeEndElement();
		w.writeEndElement();
		w.writeEndElement();
		w.flush();
		System.out.flush();
		}
	
	public static void main(String[] args) throws Exception {
		new RgdToHtml().run(args);
	}
}
