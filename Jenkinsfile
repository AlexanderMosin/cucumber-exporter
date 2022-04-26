pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '100', artifactNumToKeepStr: '10'))
    }

    stages {
        stage('Build') {
            steps {
                sh '''
                    chmod +x ./gradlew
                    ./gradlew assemble
                '''
            }
        }

        stage('Sonar') {
          steps {
            sh """
              ./gradlew sonarqube -DBRANCH=${GIT_BRANCH} -x test
            """
          }
        }
    }
}