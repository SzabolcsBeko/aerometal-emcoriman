pipeline {
    agent any

    stages {

        stage('Environment Check') {
            steps {
                bat '''
                    echo ===== CURRENT DIRECTORY =====
                    cd

                    echo ===== DIRECTORY CONTENT =====
                    dir

                    echo ===== JAVA =====
                    java -version

                    echo ===== MAVEN =====
                    mvn -version

                    echo ===== GIT =====
                    git --version
                '''
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Check Project') {
            steps {
                dir('backend') {
                    bat '''
                        echo ===== PROJECT DIRECTORY =====
                        cd
                        dir
                        echo ===== POM.XML =====
                        if exist pom.xml (
                            echo pom.xml FOUND
                        ) else (
                            echo ERROR: pom.xml NOT FOUND
                            exit /b 1
                        )
                    '''
                }
            }
        }

        stage('Maven Build') {
            steps {
                dir('backend') {
                    bat 'mvn clean package'
                }
            }
        }
    }

    post {
        success {
            echo '===== BUILD SUCCESSFUL ====='
            archiveArtifacts artifacts: 'backend/target/*.jar',
                             fingerprint: true
        }

        failure {
            echo '===== BUILD FAILED ====='
        }
    }
}