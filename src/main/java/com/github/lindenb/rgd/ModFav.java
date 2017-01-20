package com.github.lindenb.rgd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ModFav {
	private static final Logger LOG=Logger.getLogger("ModFav");
	
	private User favedBy=null;
	private User submitter=null;
	private String submissionUrl=null;
	private User artist=null;
	private String artUrl=null;
	private String permalink=null;
	private String postId=null;
	
	private ModFav() {
		
		}
	
	public boolean hasSubmission(final Submission sub)
	{
		if(!this.getSubmitter().equals(sub.getUser())) return false;
		final String postId="/comments/"+sub.getPostId();
		if(!(getPermalink().contains(postId+"/") || getPermalink().endsWith(postId))) return false;
		return true;
	}

	public boolean hasArt(final Art art)
	{
		if(!hasSubmission(art.getSubmission())) return false;
		if(!this.getArtist().equals(art.getUser())) return false;
		return true;
	}

	
	public String getSubmissionId(){
		String s=getPermalink();
		final String comments="/comments/";
		int i=s.indexOf(comments);
		if(i==-1) {
			LOG.info("Cannot find "+comments+" in "+getPermalink()+" "+toString());
			return null;
		}
		s=s.substring(i+comments.length());
		i=s.indexOf("/");
		if(i==-1) {
			LOG.info("Cannot find "+comments+" in "+getPermalink()+" "+toString());
			return null;
		}
		return s.substring(0,i);
		}
	/*
	public String getArtId(){
		String s=getPermalink();
		final String comments="/comments/";
		int i=s.indexOf(comments);
		if(i==-1) throw new IllegalArgumentException("Cannot find "+comments+" in "+getPermalink());
		i=s.indexOf('/',i+comments.length());//after postid
		if(i==-1) throw new IllegalArgumentException("Cannot find n2 in "+getPermalink());
		i=s.indexOf('/',i+1);//after title
		if(i==-1) throw new IllegalArgumentException("Cannot find n3 in "+getPermalink());
		int j=s.indexOf('/',i+1);
		return j==-1?s.substring(i+1).trim():s.substring(i+1, j).trim();
		}*/
	
	public String getPostId() {
		return postId;
	}
	
	public User getFavedBy() {
		return favedBy;
	}




	public User getSubmitter() {
		return submitter;
	}




	public String getSubmissionUrl() {
		return submissionUrl;
	}




	public User getArtist() {
		return artist;
	}




	public String getArtUrl() {
		return artUrl;
	}




	public String getPermalink() {
		return permalink;
	}

	public String getFavPermalink() {
		return "https://www.reddit.com/r/redditgetsdrawn/comments/"+getPostId()+"/";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((postId == null) ? 0 : postId.hashCode());
		result = prime * result + ((artUrl == null) ? 0 : artUrl.hashCode());
		result = prime * result + ((artist == null) ? 0 : artist.hashCode());
		result = prime * result + ((favedBy == null) ? 0 : favedBy.hashCode());
		result = prime * result + ((permalink == null) ? 0 : permalink.hashCode());
		result = prime * result + ((submissionUrl == null) ? 0 : submissionUrl.hashCode());
		result = prime * result + ((submitter == null) ? 0 : submitter.hashCode());
		return result;
	}




	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ModFav [favedBy=");
		builder.append(favedBy);
		builder.append(", submitter=");
		builder.append(submitter);
		builder.append(", submissionUrl=");
		builder.append(submissionUrl);
		builder.append(", artist=");
		builder.append(artist);
		builder.append(", artUrl=");
		builder.append(artUrl);
		builder.append(", permalink=");
		builder.append(permalink);
		builder.append(", postId=");
		builder.append(postId);
		builder.append("]");
		return builder.toString();
	}




	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModFav other = (ModFav) obj;
		if (artUrl == null) {
			if (other.artUrl != null)
				return false;
		} else if (!artUrl.equals(other.artUrl))
			return false;
		if (artist == null) {
			if (other.artist != null)
				return false;
		} else if (!artist.equals(other.artist))
			return false;
		if (favedBy == null) {
			if (other.favedBy != null)
				return false;
		} else if (!favedBy.equals(other.favedBy))
			return false;
		if (permalink == null) {
			if (other.permalink != null)
				return false;
		} else if (!permalink.equals(other.permalink))
			return false;
		if (submissionUrl == null) {
			if (other.submissionUrl != null)
				return false;
		} else if (!submissionUrl.equals(other.submissionUrl))
			return false;
		if (submitter == null) {
			if (other.submitter != null)
				return false;
		} else if (!submitter.equals(other.submitter))
			return false;
		return true;
	}


	
	private static String _postId(JsonElement o) {

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
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("id")) return null;
		o = o.getAsJsonObject().get("id");

		if(o==null || !o.isJsonPrimitive() || !o.getAsJsonPrimitive().isString()) return null;
		return o.getAsJsonPrimitive().getAsString();
		}



	public static List<ModFav> parse(final String  file) throws IOException {
		LOG.info("Read "+file);
		final Reader r= (file==null?new InputStreamReader(System.in): new FileReader(file));
		final JsonParser parser = new JsonParser();
		final JsonElement root=parser.parse(r);
		r.close();
		
		final String _postId=_postId(root);

		
		JsonElement o =  root;
		if(o==null || !o.isJsonArray()) return Collections.emptyList();
		if(o==null || !o.isJsonArray() ||  o.getAsJsonArray().size() <= 0 ) return Collections.emptyList();
		o = o.getAsJsonArray().get(0);

		if(o==null || !o.isJsonObject()) return Collections.emptyList();
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("data")) return Collections.emptyList();
		o = o.getAsJsonObject().get("data");

		if(o==null || !o.isJsonObject()) return Collections.emptyList();
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("children")) return Collections.emptyList();
		o = o.getAsJsonObject().get("children");

		if(o==null || !o.isJsonArray()) return Collections.emptyList();
		if(o==null || !o.isJsonArray() ||  o.getAsJsonArray().size() <= 0 ) return Collections.emptyList();
		o = o.getAsJsonArray().get(0);

		if(o==null || !o.isJsonObject()) return Collections.emptyList();
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("data")) return Collections.emptyList();
		o = o.getAsJsonObject().get("data");
		
		if(o==null || !o.isJsonObject()) return Collections.emptyList();
		if(o==null || !o.isJsonObject() || !o.getAsJsonObject().has("selftext")) return Collections.emptyList();
		o = o.getAsJsonObject().get("selftext");

		if(o==null || !o.isJsonPrimitive() || !o.getAsJsonPrimitive().isString()) return Collections.emptyList();
		final String selftext= o.getAsJsonPrimitive().getAsString();
		try {
			final List<ModFav> favs = new ArrayList<>();
			final String selectionFrom="**Selections from ";
			BufferedReader rb = new BufferedReader(new StringReader(selftext));
			String line=null;
			User favBy = null;
			while((line=rb.readLine())!=null)
				{
				if(line.startsWith(selectionFrom))
					{
					favBy=null;
					line=line.substring(selectionFrom.length()).trim();
					int p0 = line.indexOf(':');
					if(p0==-1) p0=line.indexOf('*');
					if(p0<=0) continue;
					favBy=new User(line.substring(0,p0).trim());
					continue;
					}
				
				if(favBy!=null && line.toLowerCase().startsWith("submitter"))
					{
					final List<String> component= new ArrayList<>(6);
					int i=0;
					while(i<line.length())	
						{
						int x1 =line.indexOf('[',i);
						if(x1==-1) break;
						int x2 =line.indexOf("](",x1);
						if(x2==-1) break;
						int x3= line.indexOf(")",x2);
						if(x3==-1) break;
						component.add(line.substring(x1+1,x2));
						component.add(line.substring(x2+2,x3));
						
						i=x3;
						}
					if(component.size()==6)
						{
						if(_postId==null) throw new IOException("cannot get post id");
						final ModFav fav=new ModFav();
						fav.postId=_postId;
						fav.favedBy = favBy;
						fav.submitter = new User(component.get(0).trim());
						fav.submissionUrl = component.get(1);
						fav.artist = new User(component.get(2).trim());
						fav.artUrl = component.get(3);
						//ignore 4
						fav.permalink = component.get(5);
						favs.add(fav);
						}
					
					}
				}
			
			rb.close();
			return favs;
		} catch (final IOException e) {
			LOG.warning(e.getMessage());
			return Collections.emptyList();
		}
		
		}

}
