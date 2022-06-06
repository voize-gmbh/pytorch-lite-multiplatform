Pod::Spec.new do |spec|
    spec.name                     = 'LibTorchWrapper'
    spec.version                  = '0.1.0'
    spec.homepage                 = 'https://github.com/voize-gmbh/pytorch-lite-multiplatform'
    spec.source                   = { :git => 'https://github.com/voize-gmbh/pytorch-lite-multiplatform.git' }
    spec.authors                  = { 'Erik Ziegler' => 'erik@voize.de' }
    spec.license                  = { :type => 'MIT' }
    spec.summary                  = 'Objective-C wrapper for LibTorch-Lite, to be used with pytorch-lite-multiplatform'

    spec.ios.deployment_target = '13.5'

    spec.source_files = "ios/LibTorchWrapper/LibTorchWrapper/**/*.{h,m,mm}"

    spec.dependency 'LibTorch-Lite', '1.11.0'

    spec.xcconfig = {
        'HEADER_SEARCH_PATHS' => '"${PODS_ROOT}/LibTorch-Lite/install/include/"',
        'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64'
    }

end
