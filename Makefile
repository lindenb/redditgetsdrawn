.PHONY=all all_json
SHELL=/bin/bash
SLEEP?=2
POSTS=$(shell sort data/posts.txt | sort | uniq | tac)
lib.dir?=lib
gson.jar = \
	$(lib.dir)/com/google/code/gson/gson/2.6.2/gson-2.6.2.jar
all_maven_jars = $(sort ${gson.jar})

.SECONDARY:

%.json :
	mkdir -p $(dir $@) && curl  -o "$(addsuffix .tmp,$@)" -H 'User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:47.0) Gecko/20100101 Firefox/47.0'  "https://www.reddit.com/r/redditgetsdrawn/comments/$(notdir $(basename $@)).json" && mv "$(addsuffix .tmp,$@)" "$@" && sleep ${SLEEP}

%.sql : %.json
	jjs src/tosql.js -- $< > $@

all: dist/rgd2html.jar $(addprefix cache/,$(addsuffix .json,${POSTS}))

all_json: $(addprefix cache/,$(addsuffix .json,${POSTS}))


dist/rgd2html.jar : ${all_maven_jars} ./src/main/java/com/github/lindenb/rgd/RgdToHtml.java \
				./src/main/java/com/github/lindenb/rgd/RgdToSql.java
	mkdir -p $(dir $@)  tmp
	javac -d tmp -cp lib/com/google/code/gson/gson/2.6.2/gson-2.6.2.jar -sourcepath src/main/java $(filter %.java,$^)
	jar cvf $@ -C tmp .
	rm -rf tmp
	

download_all_maven: ${all_maven_jars}
${all_maven_jars}  : 
	mkdir -p $(dir $@) && curl -Lk ${curl.proxy} -o "$@" "http://central.maven.org/maven2/$(patsubst ${lib.dir}/%,%,$@)"

