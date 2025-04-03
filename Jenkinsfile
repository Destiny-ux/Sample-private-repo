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
                bat 'mvn clean package -U'
            }
        }
        
        stage('Test') {
            steps {
                bat 'mvn test -U'
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
                publishHTML(
                    target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Report'
                    ]
                )
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
    }
}
