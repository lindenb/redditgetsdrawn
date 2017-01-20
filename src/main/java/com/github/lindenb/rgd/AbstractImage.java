package com.github.lindenb.rgd;

import java.util.logging.Logger;

public abstract class AbstractImage {
 private static final Logger LOG=Logger.getLogger("AbstractImage");

private ImageInfo imageInfo=null;
public abstract  User getUser();

protected abstract String getImageUrl() ;

public final  ImageInfo getImageInfo(){
	if(this.imageInfo!=null) {
		return this.imageInfo;
		}
	ImageInfoFactory factory=new ImageInfoFactory();
	this.imageInfo = factory.get(getImageUrl());
	if(this.imageInfo==null) this.imageInfo=ImageInfoFactory.getNoImageInfo();
	LOG.info("factory returned "+this.imageInfo+" from "+getImageUrl());
	return this.imageInfo;
	}
public abstract  String getImagePage();



}
