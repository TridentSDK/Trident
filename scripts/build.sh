echo "Currently on branch: $TRAVIS_BRANCH"

git clone -b revamp https://github.com/TridentSDK/TridentSDK.git
pushd TridentSDK
mvn clean install
popd

if [ "$TRAVIS_BRANCH" == "revamp" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ];
then
    echo "Compiling with deployment"
    mvn clean install deploy --settings travis/settings.xml
else
    echo "Compiling without deployment"
    mvn clean install --settings travis/settings.xml
fi
