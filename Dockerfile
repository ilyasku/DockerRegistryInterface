FROM java:8

ADD build/install/DockerRegistryInterface /opt/DockerRegistryInterface/

CMD '/opt/DockerRegistryInterface/bin/DockerRegistryInterface'
