pipeline {
    agent any
    options {
        timestamps()
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    triggers {
        pollSCM('H/2 * * * *')
    }
    environment {
        DEPLOY_ROOT = '/opt/postmate'
        APP_PORT = '10001'
        PUBLIC_URL = 'https://postmate.sunilab.work'
    }
    stages {
        stage('Test and build') {
            steps {
                sh 'bash ./gradlew clean test bootJar --no-daemon --console=plain'
            }
            post {
                always {
                    junit testResults: 'build/test-results/test/TEST-*.xml', allowEmptyResults: true
                }
            }
        }
        stage('Deploy and verify') {
            steps {
                sh 'bash scripts/deploy.sh build/libs/PostMate-0.0.1-SNAPSHOT.jar'
            }
        }
    }
    post {
        success {
            archiveArtifacts artifacts: 'build/libs/PostMate-0.0.1-SNAPSHOT.jar', fingerprint: true
        }
        failure {
            echo 'Build or deployment failed. Deployment script restores the previous release if a switch failed.'
        }
    }
}
