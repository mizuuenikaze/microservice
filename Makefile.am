#
# Custom makefile for a ampersand based frontend
#


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
	exec ./gradlew appRun --no-daemon -Penv=$(CONF_ENV) -Pv=$(VERSION)

appDebug:
	exec ./gradlew appDebug --no-daemon -Penv=$(CONF_ENV) -Pv=$(VERSION)

install-exec-local:
	cat webapp/build/distributions/$(PACKAGE)-$(VERSION).tar | $(am__untar) -C $(DESTDIR)$(prefix)/
	$(LN_S) $(DESTDIR)$(prefix)/$(PACKAGE)-$(VERSION)/bin/$(PACKAGE) $(DESTDIR)$(bindir)/$(PACKAGE)
	$(SED) -i \
		-e "/^CLASSPATH/ s|\(^.*\)\$$APP_HOME|\1\$$REVERT_HOME|" \
		-e "/^CLASSPATH/ s|\$$APP_HOME/lib|$(jettydir)|g" \
		-e "/^CLASSPATH/ s|REVERT|APP|g" $(DESTDIR)$(prefix)/$(PACKAGE)-$(VERSION)/bin/$(PACKAGE)

if DEBUG
CONF_ENV = dev
all-local: build

else
CONF_ENV = prod
all-local: epack
jettydir = /usr/local/share/jetty-9
jetty_DATA = $(wildcard webapp/build/appLibs/*.jar)
endif

.PHONY: build eclipse properties epack appRun appDebug
