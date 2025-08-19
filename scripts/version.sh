#!/bin/bash
set -e

VERSION=${1}
GIT_TAG=v${VERSION}
BASEDIR=$(dirname $(readlink -f "$0"))
(
    cd "$BASEDIR/.."
    sed "s/version=.*/version=${VERSION}/g" gradle.properties > gradle.properties.tmp
    mv gradle.properties.tmp gradle.properties

    sed "s/spec.version\([[:space:]]*\)=.*/spec.version\1= '${VERSION}'/g" PLMLibTorchWrapper.podspec > PLMLibTorchWrapper.podspec.tmp
    sed "s/:tag => .*/:tag => 'v${VERSION}' }/g" PLMLibTorchWrapper.podspec.tmp > PLMLibTorchWrapper.podspec.tmp2
    mv PLMLibTorchWrapper.podspec.tmp2 PLMLibTorchWrapper.podspec
    rm PLMLibTorchWrapper.podspec.tmp

    sed "s/spec.version\([[:space:]]*\)=.*/spec.version\1= '${VERSION}'/g" ios/LibTorchWrapper/PLMLibTorchWrapper.podspec > ios/LibTorchWrapper/PLMLibTorchWrapper.podspec.tmp
    mv ios/LibTorchWrapper/PLMLibTorchWrapper.podspec.tmp ios/LibTorchWrapper/PLMLibTorchWrapper.podspec
    git commit -m "version ${VERSION}" gradle.properties PLMLibTorchWrapper.podspec ios/LibTorchWrapper/PLMLibTorchWrapper.podspec
    git tag -a $GIT_TAG -m "version ${VERSION}"
)
