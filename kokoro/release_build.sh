#!/bin/bash

# Fail on any error.
set -e
# Display commands to stderr.
set -x

<<<<<<< HEAD
cd github/appengine-plugins-core

# Use GCP Maven Mirror
mkdir -p ${HOME}/.m2
cp settings.xml ${HOME}/.m2

mvn -Prelease -B -U verify -Dtest=!FilePermissionsTest -Dorg.slf4j.simpleLogger.showDateTime=true -Dorg.slf4j.simpleLogger.dateTimeFormat=HH:mm:ss:SSS
=======
sudo -E /opt/google-cloud-sdk/bin/gcloud components update --quiet
sudo -E /opt/google-cloud-sdk/bin/gcloud components install app-engine-java --quiet

cd github/app-maven-plugin
./mvnw -Prelease -B -U verify
>>>>>>> app-maven-plugin/master

# copy pom with the name expected in the Maven repository
ARTIFACT_ID=$(mvn -B help:evaluate -Dexpression=project.artifactId 2>/dev/null | grep -v "^\[")
PROJECT_VERSION=$(mvn -B help:evaluate -Dexpression=project.version 2>/dev/null| grep -v "^\[")
cp pom.xml target/${ARTIFACT_ID}-${PROJECT_VERSION}.pom

