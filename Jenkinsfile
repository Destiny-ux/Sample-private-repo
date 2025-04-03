pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                // Force clean repository and update dependencies
                bat 'mvn clean package -U -Dmaven.repo.local=$WORKSPACE/.repository'
            }
        }
        
        stage('Test') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/**/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java'
                    )
                }
            }
        }
        
        stage('Archive') {
            steps {
                archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
                publishHTML([
                    target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: false,
                        includes: '**/*.png',
                        keepAll: true,
                        reportDir: 'C:/Users/User/MyMavenApp/target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'HTML Report'
                    ]
                ])
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
