# drawings

I draw, sometimes 



## cron setup

````
#!/bin/bash

set -euf -o pipefail

touch dir1/dir2/posts.txt

curl -s -x "(proxy)"  -A 'Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:47.0) Gecko/20100101 Firefox/47.0' \
	 "https://www.reddit.com/r/redditgetsdrawn/new/" | tr "<>\"" "\n" | \
	  grep "/comment" | \
	  sed 's%^/r/reddit%https://www.reddit.com/r/reddit%' | cut -d '/' -f 7 | awk 'length($0)==6' | LC_ALL=C sort | uniq >> dir1/dir2/posts.txt

LC_ALL=C sort dir1/dir2/posts.txt | uniq > dir1/dir2/posts.tmp 
mv dir1/dir2/posts.txt
```
