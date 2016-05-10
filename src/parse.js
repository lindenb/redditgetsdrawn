var stderr = java.lang.System.err;
var stdout = java.lang.System.out;

function readFile(f) {
    // Using JavaImporter to resolve classes
    // from specified java packages within the
    // 'with' statement below
 
    with (new JavaImporter(java.io)) {
        // more or less regular java code except for static types
        var reader = null;
        try {
            reader = new BufferedReader( new FileReader(f));
            var buf = '', line = null;
            while ((line = reader.readLine()) != null) {
            if(line.startsWith("Submitter") && line.endsWith("<br/>")) line="<p>"+line+"</p>";
                buf += line;
            }
        } finally {
            if(reader!=null) reader.close();
        }
        return buf;
    }
}

function parse_anchors(s) {
var HREF_EQ="href=\"";
var anchors=[];
var idx=0;
while((idx=s.indexOf("<a",idx))!=-1)
	{
	var anchor={};
	var x1 = s.indexOf(HREF_EQ,idx);
	if(x1== -1) return;
	x1+=HREF_EQ.length;
	var x2= s.indexOf("\"",x1);
	if(x2==-1) return;
	anchor.href= s.substring(x1,x2);
	anchors.push(anchor);
	var x3 = s.indexOf(">",x2);
	if(x3==-1) return;
	x3+=1;
	var x4 = s.indexOf("</a",x3);
	if(x4==-1) return;
	anchor.text= s.substring(x3,x4);
	x4+=3;
	
	idx= x4;
	}
return anchors;
}


if(arguments.length==0) {
stderr.println("Usage jrunscript -f rgd.js file*.html. Exiting");
java.lang.System.exit(-1);
}

var submissions=[];



try {
var SELECTION_FROM="<p><strong>Selections from ";
var SUBMITTER = "<p>Submitter ";
for(var i in  arguments) {
stderr.println("[INFO] opening "+arguments[i]);
var html= readFile(arguments[i]);
html = html.replace(/Selection from /g,"Selections from ");
var selection_from =  html.indexOf(SELECTION_FROM,0);
while( selection_from != -1)
	{
	selection_from += SELECTION_FROM.length;
	var  next_selection_from = html.indexOf(SELECTION_FROM,selection_from);
	var colon = html.indexOf(":</strong>",selection_from);
	if(colon==-1) {
		stderr.println("BOUMMM");
		}
	var moderator = html.substring(selection_from,colon).trim();

	
	var submitter = html.indexOf(SUBMITTER,colon);
	while(submitter!=-1)
		{
		if(  next_selection_from!=-1 &&  submitter >= next_selection_from) break;
		var end_p = html.indexOf("</p>",submitter);
		var paragraph = html.substring(submitter,end_p).trim();
		var anchors = parse_anchors(paragraph);
		if(anchors.length==3) {
			var user_submit =  anchors[0].text;
			var artist =  anchors[1].text;

			submissions.push({
					"liked_by" : moderator,
					"submitter": anchors[0].text,
					"submitted_image": anchors[0].href,
					"artist": anchors[1].text,
					"artist_image": anchors[1].href,
					"permalink": anchors[2].href,
					"nsfw":  paragraph.contains("[NSFW]")
					});
			}
		submitter = html.indexOf(SUBMITTER,end_p);
		}

	selection_from = next_selection_from;
	}

}

stdout.println(JSON.stringify(submissions));
} catch(error) {
error.printStackTrace();
java.lang.System.exit(-1);
}

