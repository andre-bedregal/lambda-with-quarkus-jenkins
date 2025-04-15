pipeline {
    agent {
        docker {
            image 'andre-image:latest'
        }
    }
    stages {
        stage('Build JVM') {
            steps {
                sh 'mvn install -DskipTests'
            }
        }
        stage('Build Native') {
            steps {
                sh 'mvn install -Dnative -DskipTests'
            }
        }
        stage('SAM build') {
            steps {
                sh 'sam build'
            }
        }
    }
}