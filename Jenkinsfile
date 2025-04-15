pipeline {
    agent {
        docker {
            image 'andre-image:latest'
        }
    }
    stages {
        stage('Build JVM') {
            steps {
                sh './mvnw install -DskipTests'
            }
        }
        stage('Build Native') {
            steps {
                sh './mvnw install -Dnative -DskipTests'
            }
        }
        stage('SAM build') {
            steps {
                sh 'sam build'
            }
        }
    }
}