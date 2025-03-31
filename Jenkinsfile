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
                url: 'https://github.com/Destiny-ux/Sample-private-repo.git',
                credentialsId: 'your-credentials-id' // Add if private repo needs auth
            }
        }

        stage('Verify Config') {
            steps {
                script {
                    echo "Verifying Maven and JaCoCo configuration..."
                    sh '''
                        mvn help:effective-pom | grep jacoco -A 20
                        echo "Java version:"
                        java -version
                    '''
                }
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean verify -DskipTests=false' // Explicitly enable tests
            }
            post {
                always {
                    junit allowEmptyResults: true, 
                          testResults: '**/target/surefire-reports/*.xml',
                          skipPublishingChecks: true
                    
                    jacoco execPattern: '**/target/jacoco.exec',
                          classPattern: '**/target/classes',
                          sourcePattern: '**/src/main/java',
                          exclusionPattern: '**/test/*'
                    
                    script {
                        echo "Searching for JaCoCo reports..."
                        def reportPaths = [
                            'target/site/jacoco',
                            'target/jacoco-report',
                            'target/site/jacoco-aggregate'
                        ]
                        
                        def foundReports = false
                        reportPaths.each { path ->
                            if (fileExists(path)) {
                                echo "Found JaCoCo report at: ${path}"
                                foundReports = true
                                // Archive the report for debugging
                                archiveArtifacts artifacts: "${path}/**/*", allowEmptyArchive: true
                            }
                        }
                        
                        if (!foundReports) {
                            echo "WARNING: No JaCoCo HTML reports found in standard locations"
                            sh 'find target -type d -name "*jacoco*" || echo "No jacoco directories found"'
                        }
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh 'mvn sonar:sonar -Dsonar.jacoco.reportPaths=target/jacoco.exec'
                }
            }
        }

        stage('Report') {
            steps {
                script {
                    // Archive WAR file if exists
                    def warFiles = findFiles(glob: '**/target/*.war')
                    if (!warFiles.isEmpty()) {
                        archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
                    } else {
                        echo "No WAR files found to archive"
                    }
                    
                    // Try multiple possible report locations
                    def htmlDirs = [
                        'target/site/jacoco',
                        'target/jacoco-report',
                        'target/site/jacoco-aggregate',
                        'target/jacoco'
                    ]
                    
                    def published = false
                    htmlDirs.each { dir ->
                        if (fileExists("${dir}/index.html")) {
                            echo "Publishing HTML report from: ${dir}"
                            publishHTML target: [
                                allowMissing: false,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: dir,
                                reportFiles: 'index.html',
                                reportName: "JaCoCo Report (${dir})"
                            ]
                            published = true
                        }
                    }
                    
                    if (!published) {
                        echo "WARNING: Failed to publish JaCoCo HTML report - no valid index.html found"
                        archiveArtifacts artifacts: '**/target/jacoco.exec', allowEmptyArchive: true
                    }
                }
            }
        }
    }

    triggers {
        pollSCM('H 10 * * *')
        githubPush()
    }

    post {
        always {
            script {
                echo "Pipeline completed - sending notifications"
                // Clean up workspace to save disk space
                cleanWs()
            }
        }
        success {
            slackSend color: 'good', 
                     message: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
        }
        failure {
            slackSend color: 'danger',
                     message: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'\n" +
                             "See ${env.BUILD_URL}"
        }
        unstable {
            slackSend color: 'warning',
                     message: "UNSTABLE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
        }
    }
}
