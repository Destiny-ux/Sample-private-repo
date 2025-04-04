pipeline {
    agent any
    tools {
        maven 'maven-3.99'  // Must match Jenkins global tool name
        jdk 'Java-21'       // Must match Jenkins global tool name
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', 
                url: 'https://github.com/Destiny-ux/Sample-private-repo.git'
                // Removed invalid credentialsId since it wasn't properly configured
            }
        }
        
        stage('Build & Test') {
            steps {
                // Force JaCoCo execution and report generation
                bat 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent verify'
            }
            post {
                always {
                    junit '**/target/surefire-reports/**/*.xml'  // Archive JUnit results
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
                                reportName: 'index.html',
                                keepAll: true
                            ]
                        ])
                    } else {
                        echo "Warning: JaCoCo HTML report not found!"
                    }
                }
            }
        }
    }
    
    post {
        always {
            bat '''
                echo "Checking for JaCoCo report..."
                dir /s "target\\site\\jacoco" || echo "No JaCoCo report generated."
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
