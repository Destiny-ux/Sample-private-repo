pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }
        
        stage('Test') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/test/**'
                    )
                }
            }
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
    
    post {
        always {
            archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
            publishHTML(
                target: [
                    allowMissing: true,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Report'
                ]
            )
        }
    }
}
triggers {
        pollSCM('H 10 * * *')  // time is adjustable
        githubPush()
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
