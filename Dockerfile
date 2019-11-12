FROM tomcat:7
USER root
copy context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml
copy tomcat-users.xml /usr/local/tomcat/conf/tomcat-users.xml
copy Locanda_target/*.war /opt/apache-tomcat-7.0.96/webapps
CMD ["catalina.sh", "run"]
