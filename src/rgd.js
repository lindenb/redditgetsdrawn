var all_images=[];

var current_week = null;

function load_images(idx,week) {
		if(idx >  all_images.length) return;
		if(current_week!=week) return;
		if( idx<0 || idx >= all_images.length) return;
		var d =  all_images[idx];
		var ctx = d.canvas.getContext('2d');
		var offscreen = new Image();
		
		offscreen.onload = function(){
			//alert(img.src);
			ctx.rect(0,0,250,250);
		    ctx.fillStyle="white";
		    ctx.fill(); 
		    var w;
		    var h;
		    if(offscreen.width>offscreen.height)
		    	{
		    	w= d.canvas.width;
		    	h=  offscreen.height * ( d.canvas.width / offscreen.width );
		    	}
		    else
		    	{
		    	h = d.canvas.height;
		    	w=  offscreen.width * ( d.canvas.height / offscreen.height );
		    	}
		    ctx.drawImage(
		        offscreen,
		        0,0,offscreen.width,offscreen.height,
		    	(d.canvas.width-w)/2,(d.canvas.height-h)/2,w,h
		    	);
		    setTimeout(function(){ load_images(idx+1,week); }, 1);
			};
		
		offscreen.onerror = function() {
			setTimeout(function(){ load_images(idx+1,week); }, 1);
			};
		offscreen.src = d.artist_image;
		
		
	
	};

function createAnchor(a)	
	{
	var x = document.createElement("a");
	x.setAttribute("href",a.href);
	x.setAttribute("target","_blank");
	x.appendChild(document.createTextNode(a.text));
	if("title" in a) {
		x.setAttribute("title",a.title);
		}
	else {
		x.setAttribute("title",a.href);
	}	
	return x;
	}

function loadWeek(w) {
current_week = w;
var xmlhttp = new XMLHttpRequest();
var url = "json/"+w+".json";
xmlhttp.onreadystatechange = function() {
    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
        var myArr = JSON.parse(xmlhttp.responseText);
        all_images = [];
        var images_seen ={};
        var content=document.getElementById("content");
        while(content.hasChildNodes()) content.removeChild(content.firstChild);
        for(var i in myArr) {
        	if( myArr[i].artist_image in  images_seen) continue;
        	images_seen[myArr[i].artist_image]=1;
		
		var a0 = document.createElement("a");
		a0.setAttribute("target","_blank");
		a0.setAttribute("href",myArr[i].permalink);        	

        	var d1 = document.createElement("div");
			d1.setAttribute("class","scene");
			var c1 = document.createElement("canvas");
			c1.width= 250;
			c1.height=250;
			a0.appendChild(c1);
			d1.appendChild(a0);
		
			var p1 = document.createElement("p");
			p1.setAttribute("class","caption bottom");
			d1.appendChild(p1);
			p1.appendChild(document.createTextNode("Image Submitted by "));
			p1.appendChild(createAnchor({"href":"https://www.reddit.com/user/"+myArr[i].submitter,"text":myArr[i].submitter}));
			p1.appendChild(document.createTextNode(". Liked by "));
			var moderators=[];
			for(var j in myArr) {
			 if( myArr[j].artist_image != myArr[i].artist_image) continue;
			 moderators.push(myArr[j].liked_by);
			 }
			 
			for(var j =0;j< moderators.length;++j) {
			if(moderators.length>1 && j>0) p1.appendChild(document.createTextNode(j+1==moderators.length? " and ":", "));
			p1.appendChild(createAnchor({"href":"https://www.reddit.com/user/"+moderators[j],"text":moderators[j]}));
			}
			
			p1.appendChild(document.createTextNode(". Drawn by "));
			p1.appendChild(createAnchor({"href":"https://www.reddit.com/user/"+myArr[i].artist,"text":myArr[i].artist}));
			p1.appendChild(document.createTextNode(". "));
			p1.appendChild(createAnchor({"href":myArr[i].permalink,"text":"[Permalink]"}));
			p1.appendChild(document.createTextNode("."));
			
			content.appendChild(d1);
			all_images.push({
				"canvas": c1,
				"artist_image": myArr[i].artist_image
				});
        	}
    load_images(0,w);
    }
};
xmlhttp.open("GET", url, true);
xmlhttp.send();
}


function loadPages() {
var xmlhttp = new XMLHttpRequest();
var url = "json/pages.json";
xmlhttp.onreadystatechange = function() {
    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
        var myArr = JSON.parse(xmlhttp.responseText).pages;
        var weekSelector=document.getElementById("weekSelector");
        var first_id = null;
        while(weekSelector.hasChild) content.removeChild(content.firstChild);
        for(var i in myArr) {
        	var p = myArr[i];
        	var E = document.createElement("option");
        	
        	E.setAttribute("value",p.id);
        	if( first_id == null) E.setAttribute("selected","true");
        	E.appendChild( document.createTextNode(p.label) );
        	weekSelector.appendChild(E);
        	if( first_id == null) first_id = p.id;
        	}
    if( first_id != null) loadWeek(first_id);
    weekSelector.addEventListener("change",function(evt)
		{
		loadWeek(evt.target.value);
		});
    }
};
xmlhttp.open("GET", url, true);
xmlhttp.send();
}


window.addEventListener("load",function() {loadPages();},false);
