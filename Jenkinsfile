pipeline {
    agent any

    tools {
        maven 'Maven-21' // Ensure Maven is installed and configured in Jenkins
        jdk 'Java-21'       // Ensure JDK is installed and configured in Jenkins
    }

    stages {
        stage('Build') {
            steps {
                echo 'Building the application...'
                sh 'mvn clean package' // Run Maven build
            }
        }
        stage('Test') {
            steps {
                echo 'Running tests...'
                sh 'mvn test' // Run Maven tests
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying the application...'
                // Add deployment steps here (e.g., copying files to a server)
            }
        }
       
}
     post {
    success {
        echo 'Pipeline succeeded!'
    }
    failure {
        echo 'Pipeline failed!'
    }
    }
}
