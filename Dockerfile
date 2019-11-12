FROM tomcat:9-jdk11-openjdk
MAINTAINER 083411

RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY build/libs/recycling-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
