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

gradlew clean build jar publishToMavenLocal
popd

if [ "$TRAVIS_BRANCH" == "revamp" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ];
then
    echo "Compiling with deployment"
    gradlew clean build jar jacocoTestReport uploadArchives

    # Ping docker hub
    curl -H "Content-Type: application/json" --data "'{\"build\": true}'" -X POST https://registry.hub.docker.com/u/tridentsdk/trident/trigger/${DOCKER_TRIGGER_TOKEN}/
else
    echo "Compiling without deployment"
    gradlew clean build jar
fi
