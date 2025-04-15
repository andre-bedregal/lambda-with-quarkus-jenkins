pipeline {
    environment {
        JAVA_TOOL_OPTIONS = "-Duser.home=/var/maven"
    }
    agent {
        docker {
            image 'andre-image:latest'
            args '-v /opt/maven:/var/maven/.m2:z -e MAVEN_CONFIG=/var/maven/.m2'
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