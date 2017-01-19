package com.github.lindenb.rgd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
	private boolean useBase64=false;
	private Date minDate=null;
	private Date maxDate=null;
	private boolean pack=false;
	
	private void write(XMLStreamWriter out,ImageInfo img)throws Exception {
		out.writeEmptyElement("img");
		String url= img.getUrl();
		if(this.useBase64)
			{
			try {
				url=img.toBase64();
				
				} 
			catch(Exception err)
				{
				url=img.getUrl();
				}
			}

		out.writeAttribute("src", url);
		out.writeAttribute("width", ""+img.getWidth());
		out.writeAttribute("height",""+img.getHeight());

	}
	
	private void run(final String[] args) throws Exception {
			final SimpleDateFormat sm = new SimpleDateFormat("yyyy-dd-mm");
			int optind=0;
			while(optind<args.length)
				{
				if(args[optind].equals("-h"))
					{
					System.err.println("Options: ");
					System.err.println(" -b     use base 64 for images.");
					System.err.println(" -p     pack vertical");
					System.err.println(" -m <s> min date");
					System.err.println(" -M <s> max date");
					return;
					}
				else if(args[optind].equals("-m") && optind+1 < args.length)
					{
					this.minDate = sm.parse( args[++optind]);
					}
				else if(args[optind].equals("-M") && optind+1 < args.length)
					{
					this.maxDate = sm.parse( args[++optind]);
					}
				else if(args[optind].equals("-b"))
					{
					this.useBase64=true;
					}
				else if(args[optind].equals("-p"))
					{
					this.pack=true;
					}
				else if(args[optind].equals("--"))
					{
					optind++;
					break;
					}
				else if(args[optind].startsWith("-"))
					{
					System.err.println("Unnown option: "+args[optind]);
					return;
					}
				else
					{
					break;
					}
				++optind;
				}
		
		
		while(optind< args.length) {
			final String s =args[optind++];
			final Submission sub = Submission.parse(s);
			
			if(this.minDate!=null && (sub.getDate()==null || sub.getDate().before(this.minDate))) 
				{
				LOG.info("ignoring submission before "+this.minDate+" (was "+sub.getDate()+")");
				continue;
				}
			if(this.maxDate!=null && (sub.getDate()==null || sub.getDate().after(this.maxDate))) 
				{
				LOG.info("ignoring submission after "+this.maxDate+" (was "+sub.getDate()+")");
				continue;
				}
			
			
			if(!sub.getImageInfo().toBigSquareImageInfo().isValid()) continue;
			if(sub.getUser().getName().equals("AutoModerator")) continue;
			if(sub.getUser().getName().equals("[deleted]")) continue;
			List<Art> arts = new ArrayList<>(sub.getArts());
			int i=0;
			LOG.info("scanning art N="+arts.size()+" sub="+this.submissions.size());
			while(i<arts.size()) {
				final Art art = arts.get(i);
				User u = art.getUser();
				if( u.getName().equals("AutoModerator") ||
					u.getName().equals("[deleted]") ||
					!art.getImageInfo().toBigSquareImageInfo().isValid())
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
		
		LOG.info("sort submissions N="+this.submissions.size());
		Collections.sort(this.submissions,new Comparator<Submission>() {
			@Override
			public int compare(Submission o1, Submission o2) {
				return o2.getArts().size() - o1.getArts().size();
				}
			});
		
		LOG.info("sort users");
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
		
		w.writeStartElement("thead");
		
		w.writeStartElement("caption");
		w.writeCharacters("RedditGetsDrawn");
		if(this.minDate!=null && this.maxDate!=null) {
			w.writeEmptyElement("br");
			w.writeCharacters("Between " + sm.format(this.minDate)+" and "+sm.format(this.maxDate));
			}
		else if(this.minDate!=null)
			{
			w.writeEmptyElement("br");
			w.writeCharacters("Between " + sm.format(this.minDate)+" and "+sm.format(new Date()));
			}
		else if(this.maxDate!=null)
			{
			w.writeEmptyElement("br");
			w.writeCharacters("Before " + sm.format(this.maxDate));
			}
		
		w.writeStartElement("tr");
		
		if(!this.pack) {
			w.writeStartElement("th");
			w.writeEndElement();
			}
		
		for(final Submission sub: this.submissions)
			{
			User user=sub.getUser();
			w.writeStartElement("th");
			write(w,sub.getImageInfo().toBigSquareImageInfo());
			w.writeEmptyElement("br");
			w.writeCharacters(" by ");			
			w.writeStartElement("a");
			w.writeAttribute("href", user.getUrlStr());
			w.writeCharacters(user.getName());
			w.writeEndElement();
			w.writeEndElement();
			}
		w.writeEndElement();//tr
		w.writeEndElement();//thread
		
		w.writeStartElement("tbody");
		
		if(this.pack)
			{
			int y=0;
			for(;;)
				{
				List<Art> row=new ArrayList<>();
				boolean foundOne=false;
				for(final Submission sub: this.submissions)
					{
					List<Art> arts=sub.getArts();
					if(y>=arts.size())
						{
						row.add(null);
						}
					else
						{
						row.add(arts.get(y));
						foundOne=true;
						}
					}
				if(!foundOne) break;
				w.writeStartElement("tr");
				for(int x=0;x<row.size();++x)
					{
					w.writeStartElement("td");					
					if(row.get(x)!=null) {
						w.writeStartElement("a");
						w.writeAttribute("title", row.get(x).getImagePage());
						w.writeAttribute("href",""+ row.get(x).getImagePage());
						write(w,row.get(x).getImageInfo().toBigSquareImageInfo());
						w.writeEndElement();
						}
					w.writeEndElement();//td
					}
				
				w.writeEndElement();
				++y;
				}
			}
		else 
			{
			for(final User user:users) 
				{
				w.writeStartElement("tr");
				w.writeStartElement("th");
				w.writeStartElement("a");
				w.writeCharacters(user.getName());
				w.writeEndElement();
				w.writeEndElement();//th
				for(final Submission sub: this.submissions)
					{
					Art a=null;
					for(final Art art:sub.getArts()) {
						if(!art.getUser().equals(user)) continue;
						a=art;
						break;
						}
					if(a==null) {
						w.writeStartElement("td");
						w.writeEndElement();
					} else
						{
						w.writeStartElement("td");
						w.writeStartElement("a");
						w.writeAttribute("href",""+ a.getImagePage());
						write(w,a.getImageInfo().toBigSquareImageInfo());
						w.writeEndElement();
						w.writeEndElement();
						}
					}
				w.writeEndElement();//tr
				}
			}
		w.writeEndElement();//tbody
		
		w.writeEndElement();//table
		w.writeEndElement();//body
		w.writeEndElement();//html
		w.flush();
		System.out.flush();
		}
	
	public static void main(String[] args) throws Exception {
		new RgdToHtml().run(args);
	}
}
