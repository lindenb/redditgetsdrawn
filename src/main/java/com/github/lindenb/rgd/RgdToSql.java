package com.github.lindenb.rgd;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RgdToSql {
	private Date minDate=null;
	private Date maxDate=null;
	private PrintStream out= System.out;
	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Logger LOG=Logger.getLogger("RgdToSql");
	
	private String quote(final Object o)
		{
		if(o==null) return "NULL";
		if(o instanceof Boolean) {
			boolean b=(Boolean)o;
			return b?"1":"0";
		}

		if(o instanceof Number) return Number.class.cast(o).toString();
		final StringBuilder sb=new StringBuilder("'");
		final String s=o.toString();
		for(int i=0;i<s.length();++i)
			{
			switch(s.charAt(i))
				{
				default:sb.append(s.charAt(i));break;
				}
			}
		sb.append("'");
		return sb.toString();
		}

	
	private void run(final String[] args) throws Exception {
			
			int optind=0;
			while(optind<args.length)
				{
				if(args[optind].equals("-h"))
					{
					System.err.println("Options: ");
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
		
		out.println("CREATE TABLE IF NOT EXISTS user(name TEXT,admin INTEGER);");
		out.println("CREATE UNIQUE INDEX IF NOT EXISTS user_name ON user(name);");
		
		
		
		out.println("CREATE TABLE IF NOT EXISTS submission(id TEXT,when TEXT,nsfw INTEGER,author TEXT,n_arts INTEGER);");
		out.println("CREATE UNIQUE INDEX IF NOT EXISTS submission_id ON submission(id);");
		
		out.println("CREATE TABLE IF NOT EXISTS art(id TEXT,submission TEXT,when TEXT,nsfw INTEGER,author TEXT);");
		out.println("CREATE UNIQUE INDEX IF NOT EXISTS art_id ON art(id);");
		
		
		out.println("CREATE TABLE IF NOT EXISTS fav(faved_by TEXT,submitter TEXT,artist TEXT,submission TEXT,art TEXT);");
		
		out.println("BEGIN TRANSACTION");
		while(optind< args.length) {
			final String s =args[optind++];
			
			final List<ModFav> favs=ModFav.parse(s);
			if(!favs.isEmpty())
				{
				for(final ModFav modfav:favs) {
					printModFav(modfav);
					}
				
				}
			else
				{
				printSubmission(Submission.parse(s));
				}
			}
		out.println("COMMIT;");
		}
	private void printUser(final User user)
		{
		out.println("insert or ignore into user(name,admin) values ("+quote(user.getName())+",0);");
	
		}
	
	private void printModFav(final ModFav fav)
		{
		if(fav.getSubmissionId()==null){
			LOG.info("cannot get submission id from "+fav);
			return;
		}
		printUser(fav.getFavedBy());
		out.println("update user set admin=1 where name= "+quote(fav.getFavedBy().getName())+";");
		printUser(fav.getSubmitter());
		printUser(fav.getArtist());
		
		out.print("insert into fav(faved_by,submitter,artist,submission,art) values (");
		out.print(String.join(",",
				quote(fav.getFavedBy().getName()),
				quote(fav.getSubmitter().getName()),
				quote(fav.getArtist().getName()),
				quote(fav.getSubmissionId()),
				quote(fav.getSubmissionId()+"."+fav.getArtist().getName())
				));
		out.println(");");	
		}
	
	
	private void printArt(final Art art)
		{
		printUser(art.getUser());
		out.print("insert into art(id,submission,when,nsfw,author) values (");
		out.print(String.join(",",
				quote(art.getSubmission().getPostId()+"."+art.getUser().getName()),
				quote(art.getSubmission().getPostId()),
				quote(this.simpleDateFormat.format(art.getDate())),
				quote(art.getSubmission().isNsfw()),
				quote(art.getUser().getName())
				));
		out.println(");");		
		}
	
	private void printSubmission(final Submission sub)
		{
		if(sub.getUser().getName().equals("AutoModerator")) return;
		if(this.minDate!=null && (sub.getDate()==null || sub.getDate().before(this.minDate))) 
			{
			LOG.info("ignoring submission before "+this.minDate+" (was "+sub.getDate()+")");
			return;
			}
		if(this.maxDate!=null && (sub.getDate()==null || sub.getDate().after(this.maxDate))) 
			{
			LOG.info("ignoring submission after "+this.maxDate+" (was "+sub.getDate()+")");
			return;
			}
		
		final List<Art> arts = sub.getArts().stream().
				filter(A-> !A.getUser().getName().equals("AutoModerator")).
				collect(Collectors.toList());
		
		out.print("insert into submission(id,when,nsfw,author,n_arts) values (");
		
		
		
		out.print(String.join(",",
				quote(sub.getPostId()),
				quote(this.simpleDateFormat.format(sub.getDate())),
				quote(sub.isNsfw()),
				quote(sub.getUser().getName()),
				quote(arts.size())
				));
		out.println(");");
		
		for(final Art art: arts)
			{
			printArt(art);
			}
		
		
		
		
		out.flush();
		}
	
	public static void main(String[] args) throws Exception {
		new RgdToSql().run(args);
	}
}
