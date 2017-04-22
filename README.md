# Camel as an api provider #


### What does this provide? ###

* Primarily a restful api in sync with a spa application.

### How do I get set up? ###
* This is a multi-project setup that can be built with
	* ./gradlew build -Penv=dev
	* ./gradelw build -x test -x integrationtest -Penv=dev # skip tests

* Building a production release
	* SOURCE_BUILD_NUMBER=N ./gradlew clean epack -Penv=prod


* How to run tests
	* The tests are junit tests.  In eclipse, you can run them in the IDE.  A full build will also run the tests.

### Eclipse setup ###

* Build your eclipse project by executing ./gradlew cleanEclipse eclipse
* \>\>\> Import eclipse preferences from /eclipse at the root of the repo. <<<
* Create default vm args in your preferences --> installed JREs
	* -D custom.logging.root=/tmp # this sets up the log file location on your file system.

### Oauth2 ###
* You can be authenticated and authorized to use the various endpoints by setting up a system that supports oauth2.  In this implementation, you call a special endpoint /tokenLogin as a client redirect_uri in uaa so that the service can request an access token and get your user info from the resource server.

* This implementation is done against the cloud foundry user account and authentication server (UAA).  Clone it at git@github.com:cloudfoudry/uaa.git. 
	* Create Users as needed.
	* Register a service for this application.
	* Put the secret in the keystore.
	* Profit...

### UAA ###
* The war is to be deployed to tomcat and can be configured with the provided webapp/src/main/webapp/resources/uaa.yml file in a development environment.

### keystores ###
* The gradle/config/buildConfig.groovy file has some configuration values for the location and password to a keystore that will hold sensitive values.
	* Depends on JCE, so download the correct policy files to update your jdk/jre.
	* Must have an existing JCEKS keystore file. For example:
		* keytool -keystore appkeystore -genkey -alias client -storetype jceks
