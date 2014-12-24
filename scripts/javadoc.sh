#!/bin/bash
# This script was originally written by maxiaohao in the aws-mock GitHub project.
# https://github.com/treelogic-swe/aws-mock/

# Set it up
git config --global user.email "woodyc40@gmail.com"
git config --global user.name "AgentTroll"
mkdir gh-pages

# Let's do the TridentSDK jd first
git clone -b bleeding-edge https://github.com/TridentSDK/TridentSDK.git
cd TridentSDK
mvn clean javadoc:javadoc
mv gh-pages/ ../gh-pages
cd ..

# We're gonna make this a proper repo
cd gh-pages
git init

# Lets commit some files
cd ..
mvn clean javadoc:javadoc

# Push!
cd gh-pages
git add .
git commit -m "Auto-publishing Javadoc from Travis CI"
git push -fq https://AgentTroll:${DOC_PASS}@github.com/TridentSDK/Javadoc.git HEAD:gh-pages
echo "Published JavaDoc.\n" # Done!