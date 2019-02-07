# RemoteRobot 
You can find documentation here: https://github.com/jaqat/remoterobot/wiki.

### Build
Full project build:
```
make full_build
```

### Versioning rules
If changes are occurred in **commons**/**server** modules you must:
 - increment minor version 
 - rebuild server executable JAR
 - create release in Github with attached server's JAR
 - build and push docker images with tag = maj.min  
 - deploy new client to Maven Cantral 
