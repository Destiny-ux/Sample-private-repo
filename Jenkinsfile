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
                        bat 'mvn test jacoco:report'
                        
                        // Archive test results
                        junit '**/target/surefire-reports/**/*.xml'
                        
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
                    // Always archive test results, even if tests fail
                    archiveArtifacts artifacts: '**/target/surefire-reports/**/*.*, **/target/site/jacoco/**/*.*', allowEmptyArchive: true
                }
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }
        
        stage('Report') {
            steps {
                script {
                    // Archive the built artifact
                    archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
                    
                    // Publish HTML report only if it exists
                    if (fileExists('target/site/jacoco/index.html')) {
                        publishHTML target: [
                            allowMissing: false,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: 'target/site/jacoco',
                            reportFiles: 'index.html',
                            reportName: 'JaCoCo Code Coverage',
                            reportTitles: ''
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
        unstable {
            echo 'Pipeline completed with unstable status (test failures)'
            // Optional: Add unstable notifications
        }
    }
}
