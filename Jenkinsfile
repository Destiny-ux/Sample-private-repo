pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                bat 'mvn clean package -U'  // -U forces update of dependencies
            }
        }
        stage('Test') {
            steps {
                bat 'mvn test -U'  // -U forces update of dependencies
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
        stage('SonarQube Analysis') {
            steps {
                // Your SonarQube steps here
            }
        }
    }
    
    post {
        always {
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
