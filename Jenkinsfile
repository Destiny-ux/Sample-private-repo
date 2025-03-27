pipeline {
    agent any
    
    triggers {
        pollSCM('H 10 * * *')  // Poll SCM every day at 10 AM
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
                script {
                    try {
                        // Run tests with JaCoCo agent
                        bat '''
            mvn clean org.jacoco:jacoco-maven-plugin:0.8.8:prepare-agent test
            mvn org.jacoco:jacoco-maven-plugin:0.8.8:report
        '''
        junit 'target/surefire-reports/*.xml'
                        
                        // Check if coverage reports exist
                        def coverageExists = fileExists 'target/site/jacoco/index.html'
                        echo "Coverage reports exist: ${coverageExists}"
                    } catch(e) {
                        echo "Test execution failed: ${e}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    jacoco execPattern: '**/target/jacoco.exec'
                }
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                     sh '''
            mvn sonar:sonar \
                -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml \
                -Dsonar.coverage.jacoco.executionData=target/jacoco.exec
            '''
                }
            }
        }
        
        stage('Report') {
            steps {
                script {
                    // Archive the built artifact
                    archiveArtifacts artifacts: 'target/*.war, target/site/jacoco/**/*', allowEmptyArchive: true
                    
                    // Publish HTML report only if it exists
                    if (fileExists('target/site/jacoco/index.html')) {
                        publishHTML target: [
                            allowMissing: false,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'target/site/jacoco',
                            reportFiles: 'index.html',
                            reportName: 'JaCoCo Report'
                            
                        ]
                    } else {
                        echo 'No JaCoCo coverage report found. Skipping HTML publishing.'
                    }
                }
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
        always {
            // Clean up workspace after build
            cleanWs()
        }
        success {
            echo 'Pipeline succeeded!'
            // Optional: Add success notifications
        }
        failure {
            echo 'Pipeline failed!'
            // Optional: Add failure notifications
        }
        
    }
}
