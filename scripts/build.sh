echo "Currently on branch: $TRAVIS_BRANCH"

git clone -b revamp https://github.com/TridentSDK/TridentSDK.git
pushd TridentSDK
mvn clean install
popd

if [ "$TRAVIS_BRANCH" == "revamp" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ];
then
    echo "Compiling with deployment"
    mvn clean install deploy --settings travis/settings.xml

    # Ping docker hub
    curl -H "Content-Type: application/json" --data "'{\"build\": true}'" -X POST https://registry.hub.docker.com/u/tridentsdk/trident/trigger/${DOCKER_TRIGGER_TOKEN}/
else
    echo "Compiling without deployment"
    mvn clean install --settings travis/settings.xml
fi
