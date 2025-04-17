node {
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
        stage("Build"){
            image = docker.build(dockerImageTag)
        }

        stage("Push"){
            dockerWithRegistry('', 'docker-hub-credentials') {   
                image.push()
            }
        }

        stage('Deploy') {
            script {
                sh "kubectl set image deployment/${appName}-deploy ${appName}=${dockerImageTag} --record"
                sh "kubectl rollout status deployment/${appName}-deploy"
            }
        }
    } catch (Throwable e) {
        throw e
    } finally {
        cleanWs()
    }
}