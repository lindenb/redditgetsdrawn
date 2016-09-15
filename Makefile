SHELL=/bin/bash
POSTS=$(shell sort data/posts.txt | sort | uniq)

%.json :
	mkdir -p $(dir $@) && curl  -o "$(addsuffix .tmp,$@)" -H 'User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:47.0) Gecko/20100101 Firefox/47.0'  "https://www.reddit.com/r/redditgetsdrawn/comments/$(notdir $(basename $@)).json" && mv "$(addsuffix .tmp,$@)" "$@" && sleep 10

%.sql : %.json
	jjs src/tosql.js -- $< 

all: $(addprefix cache/,$(addsuffix .sql,${POSTS}))
