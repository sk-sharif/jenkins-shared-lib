def call(body)
{
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()
pipeline {
  agent any
 environment{
    registryCredential = 'docker_id1'
    registry = 'sarosejoshi/apiweb'
    gpg_secret = credentials("gpg-secret")
    gpg_trust = credentials("gpg-trust")
    gpg_passphrase = credentials("gpg-password")
 }
  stages {
    
    stage('checkout git') {
                steps {
                    git branch: pipelineParams.branch, credentialsId: 'f73bdad1-1506-4ee0-82f1-eab94378d8a5', url: pipelineParams.scmUrl
                }
            }
   
    
 stage('Building a image for amazon-associate-etl ') {
      when {
        changeset "amazon-associate-etl/docker-images/amazon-associate-service/**"
      }
      steps {
        script {
          docker.withRegistry('', registryCredential) {
            sh '''
               cd amazon-associate-etl/docker-images/amazon-associate-service/
               make build-image '''

          }
        }

      }
      
    }
    stage('Test a image for  amazon-associate-etl ') {
      when {
        changeset "amazon-associate-etl/docker-images/amazon-associate-service/**"
      }
      steps {
        script {
          
            sh '''
               cd amazon-associate-etl/docker-images/amazon-associate-service/
               make test-image '''

          
        }

      }
      
    }
    stage('Push a image amazon-associate-etl ') {
      when {
        changeset "amazon-associate-etl/docker-images/amazon-associate-service/**"
      }
      steps {
        script {
          docker.withRegistry('', registryCredential) {
            sh '''
               cd amazon-associate-etl/docker-images/amazon-associate-service/
               make push-image '''

          }
        }

      }
      
    }
    stage('Pre-deploy image for amazon-associate-etl ') {
      when {
        changeset "amazon-associate-etl/docker-images/amazon-associate-service/**"
      }
      steps {
        script {
          
            sh '''
               cd amazon-associate-etl/docker-images/amazon-associate-service/
               make pre-deploy-image '''

        }
        }

      }
      
    
    stage('deploy image for amazon-associate-etl ') {
      when {
        changeset "amazon-associate-etl/docker-images/amazon-associate-service/**"
      }
      steps {
        script {
          docker.withRegistry('', registryCredential) {
            sh '''
               cd amazon-associate-etl/docker-images/amazon-associate-service/
               make deploy-dockerimage '''

          }
        }

      }
      
    }
    stage('Post-deploy image for amazon-associate-etl ') {
      when {
        branch 'test'
        changeset "amazon-associate-etl/docker-images/amazon-associate-service/**"
      }
      steps {
        script {
        
            sh '''
               cd amazon-associate-etl/docker-images/amazon-associate-service/
               make post-deploy-image '''

          
        }

      }
      
    }
 stage('Release Tag') {
      when {
 	
 	branch 'master'
      }
      steps {
        script {
          sh '''
               cd amazon-associate-etl/docker-images/amazon-associate-service/
               make push-image '''
        }
      }
    }
    
  }
}
}