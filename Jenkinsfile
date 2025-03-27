pipeline {
    agent any
    

    tools {
        maven 'maven-3.99' // Ensure Maven is installed and configured in Jenkins
        jdk 'Java-21'       // Ensure JDK is installed and configured in Jenkins
    }

    stages {
        stage('Build') {
            steps {
                echo 'Building the application...'
                bat 'mvn clean package' // Run Maven build
            }
        }
        stage('Test') {
            steps {
                echo 'Running tests...'
                bat 'mvn test' // Run Maven tests
            }
        }
         stage('SonarQube Analysis') {
            steps {
                // matching Jenkins config SonarQube server name
                withSonarQubeEnv('sonarqube-local') {
                    sh 'mvn sonar:sonar'
                }
            }
        }
         stage('Report') {
            steps {
                archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
                publishHTML target: [
                    allowMissing: false,
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Report'
                ]
            }
        }
        triggers {
        pollSCM('H 10 * * *')  // time is adjustable
        githubPush()
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
