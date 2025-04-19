# Use SHA to guarantee the exact version of the Docker image is used between runs
FROM jenkins/jnlp-agent-docker@sha256:ae2a5320c217c645c2eb164cf03feb72dc9c782b6b20f15ee731e67da98d850c

USER root

RUN curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl" && \
    curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl.sha256" && \
    install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

USER jenkins