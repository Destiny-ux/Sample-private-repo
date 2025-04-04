pipeline {
    agent any
    tools {
        maven 'maven-3.99'  // Ensure this matches your Jenkins global tool name
        jdk 'Java-21'       // Ensure this matches your Jenkins global tool name
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', 
                url: 'https://github.com/Destiny-ux/Sample-private-repo.git',
                credentialsId: 'your-github-credentials-id'  // Add if your repo is private
            }
        }
        
        stage('Build & Test') {
            steps {
                // Runs unit tests + generates JaCoCo reports
                bat 'mvn clean verify'  
            }
            post {
                always {
                    // Archive JUnit test results
                    junit '**/target/surefire-reports/**/*.xml'
                    
                    // Process JaCoCo coverage
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
                withSonarQubeEnv('sonarqube') {
                    bat 'mvn sonar:sonar -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml'
                }
            }
        }
        
        stage('Report') {
            steps {
                // Archive the WAR file
                archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
                
                // Publish JaCoCo HTML report (using RELATIVE path)
                publishHTML([
                    target: [
                        allowMissing: false,
                        reportDir: 'target/site/jacoco',  // Relative to workspace
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report',
                        keepAll: true
                    ]
                ])
            }
        }
    }
    
    post {
        always {
            // Debug: List files to verify the report exists
            bat 'dir /s target\\site\\jacoco || echo "No JaCoCo report generated"'
            
            // Fix permissions (optional, only if Jenkins still has issues)
            bat '''
                icacls "C:\\ProgramData\\Jenkins\\jobs\\MyMavenApp\\builds\\%BUILD_NUMBER%\\htmlreports" /grant "Everyone:(OI)(CI)F" /T
            '''
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed. Check logs for details.'
        }
    }
}
