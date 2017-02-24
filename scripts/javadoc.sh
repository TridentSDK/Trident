#!/bin/bash
# This script was originally written by maxiaohao in the aws-mock GitHub project.
# https://github.com/treelogic-swe/aws-mock/

if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
    # Set it up
    git config --global user.email "woodyc40@gmail.com"
    git config --global user.name "AgentTroll"
    mkdir gh-pages

    # We're gonna make this a proper repo
    cd gh-pages
    git init
    cd ..

    # TridentSDK included with the javadoc
    git clone -b bleeding-edge https://github.com/TridentSDK/TridentSDK.git

    # Move the server directory into TridentSDK, else the last javadoc will not display the correct index
    cp -R src/main/java/net/tridentsdk/server TridentSDK/src/main/java/net/tridentsdk/

    cd TridentSDK
    mvn clean javadoc:javadoc
    mv gh-pages/* ../gh-pages
    cd ..

    # Push!
    cd gh-pages
    git add .
    git commit -m "Auto-publishing Javadoc from Travis CI"
    git push -fq https://AgentTroll:${DOC_PASS}@github.com/TridentSDK/javadocs.git HEAD:gh-pages >/dev/null 2>/dev/null
    echo "Published JavaDoc.\n" # Done!
    
    
    # Ping docker hub
    curl -H "Content-Type: application/json" --data \"{"docker_tag": "latest"}\" -X POST https://registry.hub.docker.com/u/tridentsdk/trident/trigger/${DOCKER_TRIGGER_TOKEN}/
    curl -H "Content-Type: application/json" --data \"{"docker_tag": "latest-alpine"}\" -X POST https://registry.hub.docker.com/u/tridentsdk/trident/trigger/${DOCKER_TRIGGER_TOKEN}/

    CURRENT_VERSION=$(grep -oPm1 "(?<=<version>)[^<]+" ../pom.xml)
    curl -H "Content-Type: application/json" --data \"{"docker_tag": "$CURRENT_VERSION"}\" -X POST https://registry.hub.docker.com/u/tridentsdk/trident/trigger/${DOCKER_TRIGGER_TOKEN}/
    curl -H "Content-Type: application/json" --data \"{"docker_tag": "$CURRENT_VERSION-alpine"}\" -X POST https://registry.hub.docker.com/u/tridentsdk/trident/trigger/${DOCKER_TRIGGER_TOKEN}/
fi
