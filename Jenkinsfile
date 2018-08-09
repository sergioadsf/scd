pipeline {
	environment {
    	//Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
		IMAGE = readMavenPom().getArtifactId()
    	VERSION = readMavenPom().getVersion()
  	}
   	
	agent any
	
	stages {
		stage('Example') {			
            steps {
                echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
                echo "DEPLOY_ENV = ${DEPLOY_ENV}"
                echo "VALID_ENV = ${VALID_ENV}"
                echo "CHOICE_ENV = ${CHOICE_ENV}"
                
                echo "IMAGE ${IMAGE}"
                echo "VERSION ${VERSION}"
                echo "BUILD_ID ${env.BUILD_ID}"
                echo "JAVA_HOME ${env.JAVA_HOME}/bin:${env.PATH}"
				echo "JAVA_HOME ${env.MAVEN_HOME}"
            }
        }
		stage("Build") {
			steps {
    			git clone 'https://github.com/sergioadsf/scd.git /home/sergio/Downloads/teste'
			}
  		}
  		stage("Test") {
  		    steps {
				sh 'make check || true'
  		    }
  		}
  		stage("Deploy") {
  			steps {
				echo "Current - ${currentBuild.result}"
  			}
  		}
	}
}
