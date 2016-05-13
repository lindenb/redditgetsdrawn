var RGD={
all_pages:[],
all_images: [],
current_week: null,
load_images : function(idx,week) {
		var self = this;
		if(idx >  this.all_images.length) return;
		if(this.current_week!=week) return;
		if( idx<0 || idx >= self.all_images.length) return;
		var d =  self.all_images[idx];
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
		    setTimeout(function(){ self.load_images(idx+1,week); }, 1);
			};
		
		offscreen.onerror = function() {
			setTimeout(function(){ self.load_images(idx+1,week); }, 1);
			};
		offscreen.src = d.artist_image;
	},
createAnchor : function(a)	
	{
	var x = document.createElement("a");
	if("href" in a)	{
		x.setAttribute("href",a.href);
		x.setAttribute("target","_blank");
		}
	else
		{
		x.setAttribute("href","#");
		}
	if("onclick" in a)	{
		x.setAttribute("onclick",a.onclick);
		}
	x.appendChild(document.createTextNode(a.text));
	if("title" in a) {
		x.setAttribute("title",a.title);
		}
	else if("href" in a) {
		x.setAttribute("title",a.href);
	}	
	return x;
	},
showModerator : function(a) {
	var self=this;
	self.current_week="";
	this.all_images = [];
	var content=document.getElementById("content");
	while(content.hasChildNodes()) content.removeChild(content.firstChild);
	var pages = this.all_pages;
	var page_index=0;
	var fun = function() {
		if(page_index>= pages.length)
			{
			self.load_images(0,"");
			return;
			}
		var xmlhttp = new XMLHttpRequest();
		var url = "json/"+pages[page_index].id +".json";
		xmlhttp.onreadystatechange = function() {
			if (!(xmlhttp.readyState == 4 && xmlhttp.status == 200)) return ;
			var myArr = JSON.parse(xmlhttp.responseText);
			var div = document.createElement("div");
			content.appendChild(div);
			var par = document.createElement("par");
			par.appendChild(document.createTextNode(pages[page_index].label));
			div.appendChild(par);
			var i=0;
			while(i<myArr.length)	{
				if( myArr[i].liked_by==a) {
					++i;
					}
				else
					{
					myArr.splice(i,1);
					}
				}
			for(i=0;i< myArr.length;++i)	{
				
				
				
				
				
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
				p1.appendChild(self.createAnchor({"href":"https://www.reddit.com/user/"+myArr[i].submitter,"text":myArr[i].submitter}));
				p1.appendChild(document.createTextNode(". Liked by "));
				var moderators=[];
				for(var j in myArr) {
				 if( myArr[j].artist_image != myArr[i].artist_image) continue;
				 moderators.push(myArr[j].liked_by);
				 }
				 
				for(var j =0;j< moderators.length;++j) {
				if(moderators.length>1 && j>0) p1.appendChild(document.createTextNode(j+1==moderators.length? " and ":", "));
				p1.appendChild(self.createAnchor({"onclick":"RGD.showModerator('"+moderators[j]+"');","title": moderators[j],"text":moderators[j]}));
				}
			
				p1.appendChild(document.createTextNode(". Drawn by "));
				p1.appendChild(self.createAnchor({"href":"javascript:RGD.showArtist(%27"+myArr[i].artist+"%27)%3B","text":myArr[i].artist}));
				p1.appendChild(document.createTextNode(". "));
				p1.appendChild(self.createAnchor({"href":myArr[i].permalink,"text":"[Permalink]"}));
				p1.appendChild(document.createTextNode("."));
			
				div.appendChild(d1);
				self.all_images.push({
					"canvas": c1,
					"artist_image": myArr[i].artist_image
					});
				
				
				
				
				
				
				
				
				
				
				}
			page_index++;
			setTimeout(fun,1);
			};
		
		xmlhttp.open("GET", url, true);
		xmlhttp.send();
		};
	fun();
	
	},
showArtist : function(a) {
	alert(a);
	},
loadWeek : function (w) {
	var self = this;
	this.current_week = w;
	var xmlhttp = new XMLHttpRequest();
	var url = "json/"+w+".json";
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
		    var myArr = JSON.parse(xmlhttp.responseText);
		    self.all_images = [];
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
				p1.appendChild(self.createAnchor({"href":"https://www.reddit.com/user/"+myArr[i].submitter,"text":myArr[i].submitter}));
				p1.appendChild(document.createTextNode(". Liked by "));
				var moderators=[];
				for(var j in myArr) {
				 if( myArr[j].artist_image != myArr[i].artist_image) continue;
				 moderators.push(myArr[j].liked_by);
				 }
				 
				for(var j =0;j< moderators.length;++j) {
				if(moderators.length>1 && j>0) p1.appendChild(document.createTextNode(j+1==moderators.length? " and ":", "));
				p1.appendChild(self.createAnchor({"onclick":"RGD.showModerator('"+moderators[j]+"');","title": moderators[j],"text":moderators[j]}));
				}
			
				p1.appendChild(document.createTextNode(". Drawn by "));
				p1.appendChild(self.createAnchor({"href":"javascript:RGD.showArtist(%27"+myArr[i].artist+"%27)%3B","text":myArr[i].artist}));
				p1.appendChild(document.createTextNode(". "));
				p1.appendChild(self.createAnchor({"href":myArr[i].permalink,"text":"[Permalink]"}));
				p1.appendChild(document.createTextNode("."));
			
				content.appendChild(d1);
				self.all_images.push({
					"canvas": c1,
					"artist_image": myArr[i].artist_image
					});
		    	}
		self.load_images(0,w);
		}
	};
	xmlhttp.open("GET", url, true);
	xmlhttp.send();
	},
loadPages : function () {
	var self = this;
	var xmlhttp = new XMLHttpRequest();
	var url = "json/pages.json";
	xmlhttp.onreadystatechange = function() {
		if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
		    self.all_pages = JSON.parse(xmlhttp.responseText).pages;
		    var weekSelector=document.getElementById("weekSelector");
		    var first_id = null;
		    while(weekSelector.hasChild) content.removeChild(content.firstChild);
		    for(var i in  self.all_pages) {
		    	var p =  self.all_pages[i];
		    	var E = document.createElement("option");
		    	
		    	E.setAttribute("value",p.id);
		    	if( first_id == null) E.setAttribute("selected","true");
		    	E.appendChild( document.createTextNode(p.label) );
		    	weekSelector.appendChild(E);
		    	if( first_id == null) first_id = p.id;
		    	}
		if( first_id != null) self.loadWeek(first_id);
		weekSelector.addEventListener("change",function(evt)
			{
			self.loadWeek(evt.target.value);
			});
		}
	};
	xmlhttp.open("GET", url, true);
	xmlhttp.send();
	}
};


window.addEventListener("load",function() {RGD.loadPages();},false);
