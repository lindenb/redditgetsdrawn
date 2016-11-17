var ImageIO = Java.type("javax.imageio.ImageIO");
var URL = Java.type("java.net.URL");

//load(filename);
function Post(root)
	{
	this.root= root;
	}

Post.getImgur = function(s)
	{
	var tokens = s.split(/[^\:a-zA-Z\/\.0-9]+/);
	for(var tok in tokens)
		{
		var u = tokens[tok];

		if(( u.startsWith("http://") || u.startsWith("https://")) && u.contains("imgur.com/") )	
			{
			return u;
			}
		}
	return null;
	};

Post.parseLinks = function(s)
	{
	var array=[];
	var i=0;
	while(i< s.length)
		{
		var x1= s.indexOf("[",i);
		if(x1==-1) break;
		var x2= s.indexOf("]",x1+1);
		if(x2==-1 || x2+1==s.length) break;
		var x3=x2+1;
		if(s.charAt(x3)!='(') { i=x2; continue;}
		var x4= s.indexOf(")",x3+1);
		if(x4==-1) break;
		array.push({
			beg:x1,
			end:x4+1,
			label: s.substring(x1+1,x2),
			url: s.substring(x3+1,x4)
			});
		i=x4+1;
		}
	return array;
	};



Post.getImageInfo= function(url) {
	var img  = { "url": url, "width":-1,"height":-1};
	var is = ImageIO.createImageInputStream(new URL(u).openStream());

	var iter = ImageIO.getImageReaders(is);
	if (iter.hasNext()) {
        var reader = iter.next();
            reader.setInput(is);
            img.width = reader.getWidth(0);
            img.height = reader.getHeight(0);
            } 
	is.close();
	return img;	
	}

Post.prototype.getSubmission = function() {
	var s = this.root[0].data.children[0].data;
	return new Submission(s);
	};



Post.prototype.getArts = function() {
	var s = this.root[1].data.children;
	var array = [];
	for( var i in s)
		{
		var a = new Art(this,s[i].data);
		array.push(a);
		}
	return array;
	};
	
Post.prototype.getFavorites = function() {
	var tokens = this.root[0].data.children[0].data.selftext.split(/\n/);
	for(var i in tokens)
		{
		var line = tokens[i];
		if(line.trim().length==0) continue;
		print(line);
		print(JSON.stringify(Post.parseLinks(line)));
		}
	var array = [];
	return array;
	};
	

function User(root)
	{
	this.root=root;

	}

User.prototype.getName = function() {
	return  this.root.author;
	};

User.prototype.toString = function() {
	return this.getName()
	};

function Submission( root)
	{
	this.root= root;
	}

Submission.prototype.getUser = function() {
	return new User(this.root);
	};

Submission.prototype.getId = function() {
	return this.root.name;
	};
	


Submission.prototype.getResolutions = function() {
	var array = [];
	if(this.root.preview.images )
		{
		for(var i in this.root.preview.images)
			{
			array.push(new Resolution(this,this.root.preview.images[i]));
			}
		}
	
	return array;
	};
Submission.prototype.getTitle = function() { return this.root.title;};
Submission.prototype.getDate = function() {
	return this.root.created_utc;
	};


Submission.prototype.toString = function() {
	return "submission " + this.getUser().toString()+" "+this.getDate()+" "+this.getId();
	};

function Resolution(submission,root)
	{
	this.submission=submission;
	this.root = root;
	}	

Resolution.prototype.getWidth = function() {
	return root.width;
	};

Resolution.prototype.getHeight = function() {
	return root.height;
	};

Resolution.prototype.getUrl = function() {
	return root.url;
	};




function Art(post,root)
	{
	this.post = post;
	this.root = root;
	}

Art.prototype.getUser = function() {
	return new User(this.root);
	};


Art.prototype.getDate = function() {
	return this.root.created_utc;
	};

Art.prototype.getImgur = function() {
	return Post.getImgur(this.root.body);
	};

Art.prototype.getId = function() {
	return this.root.link_id+"."+this.root.id;
	};


Art.prototype.toString = function() {
	var s= "art by " + this.getUser().toString()+" "+this.getImgur()+" "+this.getDate()+" "+this.getId();
	return s;
	};
