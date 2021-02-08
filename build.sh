#!/bin/bash
SAVE_DIRECTORY_NAME=cloudjars
mkdir -p tmp_build/$DD2480_BUILD_COMMIT
cd tmp_build/$DD2480_BUILD_COMMIT
git clone --depth=50 -b $DD2480_BUILD_BRANCH git@github.com:DD2480-group18/dd2480-g18-Continuous-Integration.git
cd dd2480-g18-Continuous-Integration

# download dependencies, log to file, and save status in env variable
mvn dependency:resolve | sed 's/\x1B[@A-Z\\\]^_]\|\x1B\[[0-9:;<=>?]*[-!"#$%&'"'"'()*+,.\/]*[][\\@A-Z^_`a-z{|}~]//g' > .mvn_install.log
DD2480_STATUS_INSTALL=$(cat .mvn_install.log | grep BUILD | sed -r 's/^.* BUILD (.*)$/\1/g')
echo "Install status: $DD2480_STATUS_INSTALL"

# run compilation, log to file, and save status in env variable
mvn compile | sed 's/\x1B[@A-Z\\\]^_]\|\x1B\[[0-9:;<=>?]*[-!"#$%&'"'"'()*+,.\/]*[][\\@A-Z^_`a-z{|}~]//g' > .mvn_compile.log
DD2480_STATUS_COMPILE=$(cat .mvn_compile.log | grep BUILD | sed -r 's/^.* BUILD (.*)$/\1/g')
echo "Compile status: $DD2480_STATUS_COMPILE"

# run tests, log to file, and save status in env variable
mvn test | sed 's/\x1B[@A-Z\\\]^_]\|\x1B\[[0-9:;<=>?]*[-!"#$%&'"'"'()*+,.\/]*[][\\@A-Z^_`a-z{|}~]//g' > .mvn_test.log
DD2480_STATUS_TEST=$(cat .mvn_test.log | grep BUILD | sed -r 's/^.* BUILD (.*)$/\1/g')
echo "Test status: $DD2480_STATUS_TEST"

# makes sure build directory exists
mkdir -p ../../../$SAVE_DIRECTORY_NAME/$DD2480_BUILD_COMMIT
# copy built jar to build directory
cp ../../../target/ci-1.0.jar ../../../$SAVE_DIRECTORY_NAME/$DD2480_BUILD_COMMIT/$DD2480_BUILD_COMMIT.jar
# copy logs to build directory
cp .mvn*.log -t ../../../$SAVE_DIRECTORY_NAME/$DD2480_BUILD_COMMIT/

# cleanup
cd ../../..
rm -rf tmp_build
