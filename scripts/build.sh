#!/bin/bash

echo "Currently on branch: $TRAVIS_BRANCH"
echo "Pull request: $TRAVIS_PULL_REQUEST"

ORIGIN_BRANCH=$TRAVIS_BRANCH

if [ "$TRAVIS_PULL_REQUEST" != "false" ];
then
    FULL_SLUG=$TRAVIS_PULL_REQUEST_SLUG
    FULL_SLUG+="SDK"
    
    echo $FULL_SLUG
    
    OWN_REPO=$(curl --write-out %{http_code} --silent --output /dev/null "https://github.com/$FULL_SLUG")
    
    echo "Own SDK Repo: $OWN_REPO"
    
    if [ "$OWN_REPO" != "404" ];
    then
        git clone -b revamp "https://github.com/$FULL_SLUG.git"
    else
        git clone -b revamp https://github.com/TridentSDK/TridentSDK.git
    fi
    
    ORIGIN_BRANCH=$TRAVIS_PULL_REQUEST_BRANCH
else
    git clone -b revamp https://github.com/TridentSDK/TridentSDK.git
fi

pushd TridentSDK

REMOTE_REPO=$(git remote -v | grep fetch | awk '{print $2}')
BRANCH_EXISTS=$(git ls-remote --heads "$REMOTE_REPO" "$ORIGIN_BRANCH")

if [ "$BRANCH_EXISTS" ];
then
    git checkout $ORIGIN_BRANCH
fi

./gradlew clean install
popd

./gradlew clean install
