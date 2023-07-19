Pod::Spec.new do |spec|
    spec.name                     = 'PLMLibTorchWrapper'
    spec.version                  = '0.5.1'
    spec.homepage                 = 'https://github.com/voize-gmbh/pytorch-lite-multiplatform'
    spec.source                   = { :git => 'https://github.com/voize-gmbh/pytorch-lite-multiplatform.git', :tag => 'v0.5.1' }
    spec.authors                  = { 'Erik Ziegler' => 'erik@voize.de' }
    spec.license                  = { :type => 'Apache License, Version 2.0' }
    spec.summary                  = 'Objective-C wrapper for LibTorch-Lite, to be used with pytorch-lite-multiplatform'

    spec.ios.deployment_target = '13.5'

    spec.source_files = "ios/LibTorchWrapper/LibTorchWrapper/**/*.{h,m,mm}"

    spec.dependency 'LibTorch-Lite', '1.13.0.1'
    spec.frameworks = 'Accelerate'

    spec.xcconfig = {
        'HEADER_SEARCH_PATHS' => '"${PODS_ROOT}/LibTorch-Lite/install/include/"',
        'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64'
    }

end
