pipeline {
    agent any
    tools {
        maven 'maven-3.99'
        jdk 'Java-21'
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', 
                url: 'https://github.com/Destiny-ux/Sample-private-repo.git'
            }
        }
        
        stage('Build & Test') {
            steps {
                // 1. Force JaCoCo execution
                bat 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package'
                
                // 2. Explicitly generate JaCoCo reports
                bat 'mvn org.jacoco:jacoco-maven-plugin:report'
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
                withSonarQubeEnv('sonarqube') {
                    bat 'mvn sonar:sonar -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml'
                }
            }
        }
        
        stage('Report') {
            steps {
                archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
                script {
                    if (fileExists('target/site/jacoco/index.html')) {
                        publishHTML([
                            target: [
                                reportDir: 'target/site/jacoco',
                                reportFiles: 'index.html',
                                reportName: 'JaCoCo Coverage Report',
                                keepAll: true
                            ]
                        ])
                    } else {
                        echo "Warning: No tests found - JaCoCo report not generated."
                    }
                }
            }
        }
    }
    
    post {
        always {
            bat '''
                echo "Build completed with status: ${currentBuild.result}"
                dir /s "target\\site\\jacoco" 2>nul || echo "No JaCoCo report available."
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
