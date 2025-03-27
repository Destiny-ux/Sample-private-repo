pipeline {
    agent any
    
    // Triggers should be at the pipeline level, not inside a stage
    triggers {
        pollSCM('H 10 * * *')  // Poll SCM every day at 10 AM
        // githubPush()  // Note: githubPush() is not a valid trigger (use 'githubPush' instead if using GitHub plugin)
    }

    tools {
        maven 'maven-3.99' 
        jdk 'Java-21'      
    }

    stages {
        stage('Build') {
            steps {
                echo 'Building the application...'
                bat 'mvn clean package' 
            }
        }
        stage('Test') {
            steps {
                echo 'Running tests...'
                bat 'mvn test' 
            }
        }
        stage('SonarQube Analysis') {
            steps {
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
        stage('Deploy') {
            steps {
                echo 'Deploying the application...'
                // Add deployment steps here
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
