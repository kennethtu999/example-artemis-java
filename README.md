# Test access artemis by failover

```sh
podman pull apache/activemq-artemis:latest
podman run -d --name artemis -p 8161:8161 -p 61616:61616 docker.io/apache/activemq-artemis:latest

mvn clean build test
```
