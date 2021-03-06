#
# Custom makefile for gradle based java applications
#


epack:
	./gradlew epack --no-daemon --gradle-user-home="WORKDIR" -Penv=$(CONF_ENV) -Pv=$(VERSION) && touch epack

build:
	./gradlew build -x test -x integrationtest -Penv=$(CONF_ENV) -Pv=$(VERSION)

eclipse:
	./gradlew clean cleanEclipse eclipse -Penv=$(CONF_ENV) -Pv=$(VERSION)

properties:
	./gradlew services:buildProperties webapp:buildProperties -Penv=$(CONF_ENV) -Pv=$(VERSION)

clean-local:
	./gradlew clean -Penv=$(CONF_ENV) -Pv=$(VERSION)

appRun:
	exec ./gradlew appRun --no-daemon -Penv=$(CONF_ENV) -Pv=$(VERSION)

appDebug:
	exec ./gradlew appDebug --no-daemon -Penv=$(CONF_ENV) -Pv=$(VERSION)

install-exec-local:
	cat webapp/build/distributions/$(PACKAGE)-$(VERSION).tar | $(am__untar) -C $(DESTDIR)$(prefix:/%=%)/
	$(SED) -i \
		-e "/^CLASSPATH/ s|\(\$$APP_HOME/lib\)\(/muk-microservices-[0-9\.]*\.jar\)|REVERT\2|" \
		-e "/^CLASSPATH/ s|\$$APP_HOME/lib|$(jettydir)|g" \
		-e "/^CLASSPATH/ s|REVERT|\$$APP_HOME/lib|" $(DESTDIR)$(prefix:/%=%)/$(PACKAGE)-$(VERSION)/bin/$(PACKAGE)

if DEBUG
CONF_ENV = dev
all-local: build

else
CONF_ENV = prod
all-local: epack
jettydir = /usr/local/share/jetty-9
jetty_DATA = $(wildcard webapp/build/appLibs/*.jar)
endif

.PHONY: build eclipse properties appRun appDebug
