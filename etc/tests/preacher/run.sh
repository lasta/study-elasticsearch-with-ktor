#!/usr/bin/env bash
docker pull ymoch/preacher
docker run -v $PWD:/work:rw -t ymoch/preacher preacher-cli --version
docker run -v $PWD:/work:rw -t ymoch/preacher preacher-cli -u http://host.docker.internal:9200 scenario.yaml
