#!/bin/bash

VERSION=${1}
GIT_TAG=v${VERSION}
BASEDIR=$(dirname $(readlink -f "$0"))
(
    cd $BASEDIR
    sed -i "s/version = .*/version = \"${VERSION}\"/g" build.gradle.kts
    sed -i "s/spec.version\(\s*\)=.*/spec.version\1= \'${VERSION}\'/g" PLMLibTorchWrapper.podspec
    sed -i "s/:tag => .*/:tag => \'v${VERSION}\' }/g" PLMLibTorchWrapper.podspec
    sed -i "s/spec.version\(\s*\)=.*/spec.version\1= \'${VERSION}\'/g" ios/LibTorchWrapper/PLMLibTorchWrapper.podspec
)
