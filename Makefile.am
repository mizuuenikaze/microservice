#
# Custom makefile for a ampersand based frontend
#

epack:
	./gradlew epack -Penv=$(CONF_ENV)

build:
	./gradlew build -x test -x integrationtest -Penv=$(CONF_ENV)

eclipse:
	./gradlew clean cleanEclipse eclipse -Penv=$(CONF_ENV)

properties:
	./gradlew services:buildProperties webapp:buildProperties -Penv=$(CONF_ENV)

clean-local:
	./gradlew clean -Penv=$(CONF_ENV)

appRun:
	./gradlew appRun -Penv=$(CONF_ENV)

appDebug:
	./gradlew appDebug -Penv=$(CONF_ENV)


all-local: build
.PHONY: build eclipse properties epack
