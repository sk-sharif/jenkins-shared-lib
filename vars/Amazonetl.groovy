def call(body)
{
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()
pipeline {
  agent any
 environment{
    registryCredential = 'Docker_cred'
    
//     gpg_secret = credentials("gpg-secret")
//     gpg_trust = credentials("gpg-trust")
//     gpg_passphrase = credentials("gpg-password")
 }
  stages {
 stage('Building a image for amazon-associate-etl ') {
      when {
        changeset "${pipelineParams.name}**"
      }
      steps {
        script {
          docker.withRegistry('', registryCredential) {
              sh """

cd ${pipelineParams.name}
               ls
               pwd
               make build-image """

          }
        }

      }
      
    }
    stage('Test a image for  amazon-associate-etl ') {
      when {
        changeset "${pipelineParams.name}**"
      }
      steps {
        script {
          
            sh """
               cd ${pipelineParams.name}
               make test-image """

          
        }

      }
      
    }
    stage('Push a image amazon-associate-etl ') {
      when {
        changeset "${pipelineParams.name}**"
      }
      steps {
        script {
          docker.withRegistry('', registryCredential) {
            sh """
               cd ${pipelineParams.name}
               make push-image """

          }
        }

      }
      
    }
    stage('Pre-deploy image for amazon-associate-etl ') {
      when {
        changeset "${pipelineParams.name}**"
      }
      steps {
        script {
          
            sh """
               cd ${pipelineParams.name}
               make pre-deploy-image """

        }
        }

      }
      
    
    stage('deploy image for amazon-associate-etl ') {
      when {
        changeset "${pipelineParams.name}**"
      }
      steps {
        script {
          docker.withRegistry('', registryCredential) {
            sh """
               cd ${pipelineParams.name}
               make deploy-dockerimage """

          }
        }

      }
      
    }
    stage('Post-deploy image for amazon-associate-etl ') {
      when {
        branch 'test'
        changeset "${pipelineParams.name}**"
      }
      steps {
        script {
            sh """
               cd ${pipelineParams.name}
               make post-deploy-image """

          
        }

      }
      
    }

}
}
}
