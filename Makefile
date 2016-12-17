SHELL=/bin/bash
POSTS=$(shell sort data/posts.txt | sort | uniq)
lib.dir?=lib
gson.jar = \
	$(lib.dir)/com/google/code/gson/gson/2.6.2/gson-2.6.2.jar
all_maven_jars = $(sort ${gson.jar})

.SECONDARY:

%.json :
	mkdir -p $(dir $@) && curl  -o "$(addsuffix .tmp,$@)" -H 'User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:47.0) Gecko/20100101 Firefox/47.0'  "https://www.reddit.com/r/redditgetsdrawn/comments/$(notdir $(basename $@)).json" && mv "$(addsuffix .tmp,$@)" "$@" && sleep 10

%.sql : %.json
	jjs src/tosql.js -- $< > $@

all: dist/xx.jar $(addprefix cache/,$(addsuffix .sql,${POSTS}))


dist/xx.jar : ${all_maven_jars}
	mkdir -p $(dir $@)  tmp
	javac -d tmp -cp lib/com/google/code/gson/gson/2.6.2/gson-2.6.2.jar -sourcepath src/main/java src/main/java/com/github/lindenb/rgd/Post.java
	jar cvf $@ -C tmp .
	rm -rf tmp
	

download_all_maven: ${all_maven_jars}
${all_maven_jars}  : 
	mkdir -p $(dir $@) && curl -Lk ${curl.proxy} -o "$@" "http://central.maven.org/maven2/$(patsubst ${lib.dir}/%,%,$@)"

