var ImageIO = Java.type("javax.imageio.ImageIO");
var URL = Java.type("java.net.URL");

var root = load(arguments[0]);

function verify(test,msg)
	{
	if(!test) throw msg;
	}

function getImageInfo(url) {
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



function insertAuthor(data)
	{
	verify( "author" in data, "missing data in node2 ");
	var name  = data.author;
	if(name == "AutoModerator") return  null;
	
	print("INSERT INTO USER(NAME) VALUES('"+name+"');");
	return name;
	}


function insertImage(node)
	{
	print("INSERT INTO IMAGE(width,height,url) VALUES("+node.width+","+node.height+"'"+name+"');");
	}


function redditObject(node,depth)
	{
	verify( "kind" in node, "missing kind in node1");
	var kind = node["kind"];
	verify( "data" in node, "missing data in node");
	var data = node["data"];
	if( kind == "Listing")
		{
		verify( "children" in data, "missing children in data");
		var children = data["children"];
		verify(Array.isArray(children),"children is array");
		for(var idx1 in children)
			{
			var child = children[idx1];
			verify( typeof child === 'object',"child is object");
			redditObject(child,depth+1);
			}
		
		}
	else if( kind == "t3")
		{
		var name = insertAuthor(data);
		var created= data.created;
		var title= data.title;
		var over_18 = data.over_18;
		var url = data.url;
		var thumbnail = data.thumbnail;
		print("insert into submission title="+title+",author="+name+",created="+
			created+",nsw="+over_18+",url="+url+",thumbnail="+thumbnail);
			
		if("preview" in data) {

			if("images" in data.preview) {
			for(var i=0;i< data.preview.images.length;++i)
				{

				var o2 = data.preview.images[i];
				if(!("source" in o2)) continue;
				o2 = o2.source;
				print("insert into image width="+o2.width+",height="+o2.height+",src="+o2.url);
				}
			}
			}	
			
		}
	else if( kind == "t1")
		{
		var name=insertAuthor(data);
		var created= data.created;
		var url = data.body;
		
		var tokens = data.body.split(/[^\:a-zA-Z\/\.0-9]+/);

		for(var tok in tokens)
			{
			var u = tokens[tok];

			if(( u.startsWith("http://") || u.startsWith("https://")) && u.contains("imgur.com/") )	
				{
				print(">> "+ u );
				}
			}
                
		print("insert into art author="+name+",created="+
			created+",nsw="+over_18+",url="+url);
		
		}
	else
		{
		throw "undefined type "+kind;
		}
	}



function redditArray(root,depth)
	{
	verify(Array.isArray(root),"root is array");
	verify(root.length>0,"root.length>0");
	for(var idx1 in root)
		{
		var node1 = root[idx1];
		verify( typeof node1 === 'object',"node1 is object");
		redditObject(node1,depth+1);
		}
	}
	
redditArray(root,0);
/*
verify(Array.isArray(root),"root is array");
verify(root.length>0,"root.length>0");
for(var idx1 in root)
	{
	node1 = root[idx1];
	verify( typeof node1 === 'object',"node1 is object");
	redditObject(node1);
	verify( "data" in node1, "missing data in node1");
	verify( "children" in node1.data, "missing data.children in node1");
	verify(Array.isArray(node1.data.children),"node1.data.children is array");
	
	for(var idx2 in node1.data.children) {
		var node2 = node1.data.children[idx2];
		verify( "data" in node2, "missing data in node1");
		node2 = node2.data;
		
		verify( "author" in node2, "missing author in node2 ");
		if(node2.author == "AutoModerator") continue;
		insertAuthor(node2.author);
		}
	
	}*/

