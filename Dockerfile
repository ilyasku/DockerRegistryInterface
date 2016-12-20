FROM java:8

ADD docker-registry-shell/build/install/docker-registry-shell /opt/docker-registry-shell/

CMD '/opt/docker-registry-shell/bin/docker-registry-shell'
