FROM ubuntu:latest
LABEL authors="cuhun"

ENTRYPOINT ["top", "-b"]