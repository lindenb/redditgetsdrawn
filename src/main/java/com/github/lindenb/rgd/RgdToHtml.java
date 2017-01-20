package com.github.lindenb.rgd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class RgdToHtml {
	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final Logger LOG=Logger.getLogger("RgdToHtml");
	private List<Submission> submissions=new ArrayList<>();
	private Map<User, Integer> user2count=new HashMap<>();
	private final Set<ModFav> modfavs=new HashSet<>();
	private boolean useBase64=false;
	private Date minDate=null;
	private Date maxDate=null;
	private boolean pack=false;
	private final Predicate<User> acceptUser= new Predicate<User>() {
		@Override
		public boolean test(final User t) {
			return t!=null && 
				!(t.getName().equals("AutoModerator") || t.getName().equals("[deleted]"));
		}
	};
	
	private void write(XMLStreamWriter out,ImageInfo img)throws Exception {
		out.writeEmptyElement("img");
		out.writeAttribute("title",img.getUrl()+" "+img.getWidth()+"x"+img.getHeight());

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
	private void writeFaved(XMLStreamWriter w,final Art art) throws XMLStreamException
		{
		boolean first=true;
		for(final ModFav fav:this.modfavs)
			{
			if(!fav.hasArt(art)) continue;
			if(first) {
				w.writeEmptyElement("br");
				w.writeCharacters("Faved by ");
				first=false;
				}
			else
				{
				w.writeCharacters(", ");
				}
			w.writeStartElement("a");
			w.writeAttribute("href", fav.getFavPermalink());
			w.writeCharacters(fav.getFavedBy().getName());
			w.writeEndElement();
			}
		}
	
	
	private void writeTitle(final XMLStreamWriter w) throws XMLStreamException
	{

		w.writeCharacters("RedditGetsDrawn");
		if(this.minDate!=null && this.maxDate!=null) {
			w.writeEmptyElement("br");
			w.writeCharacters("Between " + simpleDateFormat.format(this.minDate)+" and "+simpleDateFormat.format(this.maxDate));
			}
		else if(this.minDate!=null)
			{
			w.writeEmptyElement("br");
			w.writeCharacters("Between " + simpleDateFormat.format(this.minDate)+" and "+simpleDateFormat.format(new Date()));
			}
		else if(this.maxDate!=null)
			{
			w.writeEmptyElement("br");
			w.writeCharacters("Before " + simpleDateFormat.format(this.maxDate));
			}
	}
	
	
	
	private void run(final String[] args) throws Exception {
			
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
					this.minDate = simpleDateFormat.parse( args[++optind]);
					}
				else if(args[optind].equals("-M") && optind+1 < args.length)
					{
					this.maxDate = simpleDateFormat.parse( args[++optind]);
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
			
			final List<ModFav> favs=ModFav.parse(s);
			if(!favs.isEmpty())
				{
				this.modfavs.addAll(favs);
				continue;
				}
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
			final List<Art> arts = new ArrayList<>(sub.getArts());
			int i=0;
			LOG.info("scanning art N="+arts.size()+" sub="+this.submissions.size());
			while(i<arts.size()) {
				final Art art = arts.get(i);
				final User u = art.getUser();
				if( !acceptUser.test(u) ||
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
		final List<User> users = new ArrayList<>(this.user2count.keySet());
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
		w.writeStartElement("head");
		
		w.writeStartElement("title");
		writeTitle(w);
		w.writeEndElement();
		w.writeStartElement("style");
		w.writeCharacters(
		"table { border-collapse: collapse;}"+
		"table , th, td {border: 1px solid #ffdddd; }"+
		"tr:nth-child(even) {background-color: #f2f2f2;}"+
		"tr:nth-child(odd) {background-color: #e0e0e0;}"+
		"th, td , caption { text-align: center;vertical-align:middle;}"
		);
		w.writeEndElement();//style
		w.writeEndElement();//head
		
		w.writeStartElement("table");
		
		w.writeStartElement("caption");
		writeTitle(w);
		w.writeEndElement();//caption
		
		w.writeStartElement("thead");
		w.writeAttribute("style",  "min-height:500px;");
		
		
		
		w.writeStartElement("tr");
		
		if(!this.pack) {
			w.writeStartElement("th");
			w.writeEndElement();
			}
		
		for(final Submission sub: this.submissions)
			{
			w.writeStartElement("th");
			w.writeStartElement("a");
			w.writeAttribute("href", sub.getPermalink());
			write(w,sub.getImageInfo().toBigSquareImageInfo());
			w.writeEndElement();
			w.writeEmptyElement("br");
			w.writeCharacters(" by ");		
			sub.getUser().writeHtmlHyperlink(w);
			w.writeEndElement();//th
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
					List<Art> arts=sub.getArts().stream().
							filter(A->acceptUser.test(A.getUser())).
							filter(A->A.getImageInfo().isValid()).
							collect(Collectors.toList());
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
						w.writeAttribute("href",""+ row.get(x).getImageUrl());
						write(w,row.get(x).getImageInfo().toBigSquareImageInfo());
						w.writeEndElement();
						w.writeEmptyElement("br");
						w.writeCharacters(" by ");
						row.get(x).getUser().writeHtmlHyperlink(w);
						
						writeFaved(w,row.get(x));
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
				user.writeHtmlHyperlink(w);
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
						writeFaved(w,a);
						w.writeEndElement();//td
						}
					}
				w.writeEndElement();//tr
				}
			}
		w.writeEndElement();//tbody
		
		w.writeEndElement();//table
		w.writeEmptyElement("hr");
		w.writeCharacters("By ");
		w.writeStartElement("a");
		w.writeAttribute("href", "https://www.reddit.com/user/yokofakun");
		w.writeCharacters("yokofakun");
		w.writeEndElement();
		w.writeCharacters(". Date:" + this.simpleDateFormat.format(new Date())+".");
		
		w.writeEndElement();//body
		w.writeEndElement();//html
		w.flush();
		System.out.flush();
		}
	
	public static void main(String[] args) throws Exception {
		new RgdToHtml().run(args);
	}
}
