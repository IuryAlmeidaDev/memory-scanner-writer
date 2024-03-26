FROM docker.elastic.co/observability/apm-agent-java:1.34.1 as apm-agent
FROM jboss/wildfly:8.2.1.Final

ENV BIO_LIB_PATH /opt/jboss/wildfly/modules/system/layers/base/com/neurotec/main/lib/linux_x86_64/
ENV PATH=${PATH}:${BIO_LIB_PATH}
ENV LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:/usr/lib:/usr/lib64:${BIO_LIB_PATH}

ENV ELASTIC_APM_SERVER_URL=http://srv-gesis-elastic-apm-01:8200
ENV ELASTIC_APM_SERVICE_NAME="sigepen"
ENV ELASTIC_APM_ENABLED="true"
ENV ELASTIC_APM_ENVIRONMENT=DEV

ENV JAVA_OPTS="-Xms1g -Xmx4g -Djava.net.preferIPv4Stack=true -javaagent:/opt/jboss/wildfly/elastic-apm-agent.jar"
ENV AMBIENTE="desenvolvimento"

RUN rm -rf /opt/jboss/wildfly/bin/standalone.conf

COPY Resources/neurotec /opt/jboss/wildfly/modules/system/layers/base/com/neurotec
COPY Resources/oracle /opt/jboss/wildfly/modules/system/layers/base/com/oracle
COPY Resources/standalone.xml /opt/jboss/wildfly/standalone/configuration
COPY Resources/sejudh.p12 /opt/jboss/wildfly/standalone/configuration
COPY Resources/aplicativo /opt/jboss/wildfly/sigepen-arquivos/aplicativo
COPY Resources/standalone.conf /opt/jboss/wildfly/bin/standalone.conf

COPY --from=apm-agent /usr/agent/elastic-apm-agent.jar /opt/jboss/wildfly/
COPY target/sigepen.war /opt/jboss/wildfly/standalone/deployments

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]