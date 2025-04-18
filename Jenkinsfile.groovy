node('docker') {
    stage("Checkout") {
        checkout scm
        script {
            env.GIT_COMMIT_HASH = sh(script: 'git rev-parse --short=5 HEAD', returnStdout: true).trim()
        }
    }

    properties([disableConcurrentBuilds()])

    def appName = 'python-app-test'
    def dockerRepository = 'medviedievm/python-app-test'

    def image
    def imageTag = "${env.GIT_COMMIT_HASH}-${env.BUILD_NUMBER}"
    def dockerImageTag = "${dockerRepository}:${imageTag}"

    try {
        stage("Build") {
            image = docker.build(dockerImageTag)
        }

        stage("Push") {
            docker.withRegistry('', 'docker-hub-credentials') {   
                image.push()
            }
        }

        stage('Deploy') {
            deployToKubernetes(appName, dockerImageTag)
        }

    } catch (Throwable e) {
        throw e
    } finally {
        cleanWs()
    }
}

def deployToKubernetes(appName, dockerImageTag) {
    node('master') {
        sh "kubectl set image deployment/${appName}-deploy ${appName}=${dockerImageTag} --record -n default"
        sh "kubectl rollout status deployment/${appName}-deploy -n default"
    }
}