FROM anapsix/alpine-java

ADD build/install/docker-registry-cli /opt/docker-registry-cli/
ENV PATH="/opt/docker-registry-cli/bin:${PATH}"

CMD '/bin/bash'
