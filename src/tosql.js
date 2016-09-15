var root = load(arguments[0]);

function verify(test,msg)
	{
	if(!test) throw msg;
	}

function insertAuthor(name)
	{
	print("INSERT INTO USER(NAME) VALUES('"+name+"');");
	}


 {
                                            "height": 216,
                                            "url": "https://i.redditmedia.com/dMz4GS_iVZ7JMOmxCnR1e5THBge0d3azp5nD7ZxXvyM.jpg?fit=crop&amp;crop=faces%2Centropy&amp;arh=2&amp;w=216&amp;s=8cce593ab54378bba73adff6cc88b0cf",
                                            "width": 216
                                        }
function insertImage(node)
	{
	print("INSERT INTO IMAGE(width,height,url) VALUES("+node.width+","+node.height+"'"+name+"');");
	}

verify(Array.isArray(root),"root is array");
verify(root.length>0,"root.length>0");
for(var idx1 in root)
	{
	node1 = root[idx1];
	verify( typeof node1 === 'object',"node1 is object");
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
	
	}

