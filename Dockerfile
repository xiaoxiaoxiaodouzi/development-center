FROM registry.c2cloud.cn/library/tomcat8:1x
MAINTAINER Vurt <yilin.yan@chinacreator.com>

# CI会在编译Docker镜像之前把编译产物拷贝到target目录下
COPY target/*.war /opt/tomcat/webapps/ROOT.war

# Launch Tomcat
CMD ["/opt/tomcat/bin/catalina.sh", "run"]