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
                url: 'https://github.com/Destiny-ux/Sample-private-repo.git',
               
            }
        }
        
        stage('Build & Test') {
            steps {
                // Force JaCoCo execution and report generation
                bat 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent verify'
            }
            post {
                always {
                    junit '**/target/surefire-reports/**/*.xml'  // Archive JUnit results (if any)
                    
                    // Process JaCoCo coverage (even if tests fail)
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
                    // Explicitly point to JaCoCo XML report
                    bat 'mvn sonar:sonar -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml'
                }
            }
        }
        
        stage('Report') {
            steps {
                // Archive the WAR file (optional)
                archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
                
                // Publish JaCoCo HTML report (only if it exists)
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
                        echo "Warning: JaCoCo HTML report not found!"
                    }
                }
            }
        }
    }
    
    post {
        always {
            // Debug: Verify if the JaCoCo report exists
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
