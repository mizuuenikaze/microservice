#
# Custom makefile for a ampersand based frontend
#

jettydir = /usr/local/share/jetty-9
jetty_DATA = $(wildcard webapp/build/appLibs/*.jar)

epack:
	./gradlew epack -Penv=$(CONF_ENV) -Pv=$(VERSION)

build:
	./gradlew build -x test -x integrationtest -Penv=$(CONF_ENV) -Pv=$(VERSION)

eclipse:
	./gradlew clean cleanEclipse eclipse -Penv=$(CONF_ENV) -Pv=$(VERSION)

properties:
	./gradlew services:buildProperties webapp:buildProperties -Penv=$(CONF_ENV) -Pv=$(VERSION)

clean-local:
	./gradlew clean -Penv=$(CONF_ENV) -Pv=$(VERSION)

appRun:
	./gradlew appRun -Penv=$(CONF_ENV) -Pv=$(VERSION)

appDebug:
	./gradlew appDebug -Penv=$(CONF_ENV) -Pv=$(VERSION)

install-exec-local:
	$(am__untar) webapp/distributions/$(PACKAGE)-$(VERSION).tar -C $(DESTDIR)$(prefix)/
	$(LN_S) $(DESTDIR)$(prefix)/$(PACKAGE)-$(VERSION)/bin/$(PACKAGE) $(DESTDIR)$(bindir)/$(PACKAGE)
	$(SED) -i \
		-e "/^CLASSPATH/ s|\(^.*\)\$$APP_HOME|\1\$$REVERT_HOME|" \
		-e "/^CLASSPATH/ s|\$$APP_HOME/lib|$(jettydir)|g" \
		-e "/^CLASSPATH/ s|REVERT|APP|g" $(DESTDIR)$(prefix)/$(PACKAGE)-$(VERSION)/bin/$(PACKAGE)

all-local: build
.PHONY: build eclipse properties epack appRun appDebug
