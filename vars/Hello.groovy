def call(test)
{
    def pipelineParams= [:]
    test.resolveStrategy = Closure.DELEGATE_FIRST
    test.delegate = pipelineParams
    test()
pipeline {
  agent any
 environment{
    registryCredential = 'Docker_cred'
    
    gpg_secret = credentials("gpg-secret")
    gpg_trust = credentials("gpg-trust")
    gpg_passphrase = credentials("gpg-password")
 }
  stages {
    
 stage('Build adsbrain-feed-etl') {
            when {
                changeset "adsbrain-feed-etl/**" 
                
                
            }
            steps {
                echo 'changed in Build adsbrain-feed-etl '
              script {
                 docker.withRegistry( '', registryCredential ) {

                    def dockerfile = 'Dockerfile'
                      def customImage = docker.build("${registry}/adsbrain-feed:${BUILD_NUMBER}", "-f ./adsbrain-feed-etl/docker-images/adsbrain-feed/${dockerfile} ./adsbrain-feed-etl/docker-images/adsbrain-feed/")
                            customImage.push()
                     
                    }
                }
                 
            }
 }

}
}
}
