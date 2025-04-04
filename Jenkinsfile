pipeline {
    agent any
    tools {
        maven 'maven-3.99'  // matching name with Jenkins global tools
        jdk 'Java-21'       // matching name with Jenkins global tools
    }
    
    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/Destiny-ux/Sample-private-repo.git'
            }
        }
        
        stage('Build') {
            steps {
                // Force clean repository and update dependencies
                bat 'mvn clean package' 
            }
        }
        
        stage('Test') {
            steps {
                bat 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/**/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec'
                       // classPattern: '**/target/classes',
                        //sourcePattern: '**/src/main/java'
                    )
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                // matching Jenkins config SonarQube server name
                withSonarQubeEnv('sonarqube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }
        
        stage('Report') {
            steps {
                archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
                publishHTML([
                    target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: false,
                        includes: '**/*.png',
                        keepAll: true,
                        reportDir: 'C:/Users/User/MyMavenApp/target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ]
                ])
            }
        }
    }
    
    post {
    always {
        bat '''
            icacls "C:\\ProgramData\\Jenkins\\.jenkins\\jobs\\MyMavenApp\\builds\\${BUILD_NUMBER}\\htmlreports" /grant "Everyone:(OI)(CI)F" /T
        '''
    }
}
        success {
            echo 'Pipeline succeeded!'
        }
        
    }
}
