# camunda-cas-sso

(Requires the normal CAS Tomcat authentication filters to work http://mvnrepository.com/artifact/org.jasig.cas/cas-client-core/3.1.10)

Camunda CAS SSO Application Server Filter with user injection for debugging. Injects users into Camunda who were already authenticated using CAS.

Currently makes all sections "available" to all users in the UI, but access rules are still in effect.

The user to log in for debugging can be changed at the top of the filter class.

The "webapp jar" consists of the zipped class files from the distribution's "camunda" webapp.

https://app.camunda.com/nexus/content/groups/public/org/camunda/bpm/webapp/camunda-webapp/7.4.0/

Installation:
1. Import into Eclipse with Maven support.
2. Add camunda engine and webapp jars to the build path
3. Build a library jar file.
4. Put the result in Tomcat's or the webapp's classpath
5. Put Apache commons-logging in Tomcat's classpath http://commons.apache.org/proper/commons-logging/download_logging.cgi

Activation in the webapp's web.xml:
* comment out the normal "Authentication filter"
* add the following filter description BEFORE the SecurityFilter

(CAS filters themselves are omitted)
```
  <filter>
      <filter-name>Camunda CAS SSO Filter</filter-name>
      <filter-class>de.hofuniversity.iisys.camunda.sso.CASSSOFilter</filter-class>
  </filter>
...
  <filter-mapping>
    <filter-name>Camunda CAS SSO Filter</filter-name>
    <url-pattern>/*</url-pattern>
    <dispatcher>REQUEST</dispatcher>
  </filter-mapping>
```