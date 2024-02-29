# this podspec is used for development
# the podspec for publishing can be found in ../LibTorchWrapper.podspec

Pod::Spec.new do |spec|
    spec.name                     = 'PLMLibTorchWrapper'
    spec.version                  = '0.6.0'
    spec.homepage                 = 'https://github.com/voize-gmbh/pytorch-lite-multiplatform'
    spec.source                   = { :http=> '' }
    spec.authors                  = { 'Erik Ziegler' => 'erik@voize.de' }
    spec.license                  = { :type => 'Apache License, Version 2.0' }
    spec.summary                  = 'Objective-C wrapper for LibTorch-Lite, to be used with pytorch-lite-multiplatform'

    spec.ios.deployment_target = '13.5'

    spec.source_files = "LibTorchWrapper/**/*.{h,m,mm}"

    spec.dependency 'LibTorch-Lite', '2.1.0'
    spec.frameworks = 'Accelerate'

    spec.xcconfig = {
        'HEADER_SEARCH_PATHS' => '"${PODS_ROOT}/LibTorch-Lite/install/include/"',
        'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64',
    }

end
