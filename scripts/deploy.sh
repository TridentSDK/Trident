echo "Currently on branch: $TRAVIS_BRANCH"

if [ "$TRAVIS_BRANCH" == "revamp" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ];
then
    ./gradlew uploadArchives

    # Ping docker hub
    curl -H "Content-Type: application/json" --data "'{\"build\": true}'" -X POST https://registry.hub.docker.com/u/tridentsdk/trident/trigger/${DOCKER_TRIGGER_TOKEN}/
fi