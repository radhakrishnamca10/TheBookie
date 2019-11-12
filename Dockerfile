FROM tomcat:7
USER root
copy context.xml /opt/apache-tomcat-7.0.96/webapps/manager/META-INF/context.xml
copy tomcat-users.xml /opt/apache-tomcat-7.0.96/conf/tomcat-users.xml
copy /root/TheBookie_1/Locanda_target/*.war /opt/apache-tomcat-7.0.96/webapps
CMD ["catalina.sh", "run"]
