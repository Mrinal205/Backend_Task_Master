
#### Running Code Locally ####

```
pg_ctl -D /usr/local/var/postgres start

mvn clean install -DskipTests && mvn -pl server spring-boot:run
```


