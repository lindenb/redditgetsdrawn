```
curl -s "https://www.reddit.com/r/redditgetsdrawn/search?q=Mod+Favourites&sort=new&restrict_sr=on&t=all" | tr '"' '\n' | grep comments | grep "its_time" | grep "reddit.com" | cut -d '/' -f 7 | sort | uniq
```
