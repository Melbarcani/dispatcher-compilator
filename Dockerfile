FROM alpine
ENV APP_PATH=/root/hello
WORKDIR $APP_PATH

ENV PATH=$APP_PATH/bin:$PATH

RUN apk add openjdk8
ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
ENV PATH $PATH:$JAVA_HOME/bin


