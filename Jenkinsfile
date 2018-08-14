import groovy.json.JsonSlurper
pipeline {
	environment {
    	//Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
		IMAGE = readMavenPom().getArtifactId()
    	VERSION = readMavenPom().getVersion()
    	folderpath = '/home/sergio/Downloads/teste'
    	str = '{"id":"12345678","name":"Sergio","email":"sergio@example.com"}'
		slurper = new JsonSlurper().parseText(str)
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
				script {
					if(slurper.id == "12345678"){
			echo 'I only execute test'    
		    }else {
			echo 'I am not running test'    
		    }				    
				}

		    
            }
        }
		stage("Build") {
			steps {
    				sh 'git clone https://github.com/sergioadsf/scd.git ${folderpath}'
			}
  		}
  		stage("Test") {
  		    steps {
				sh 'mvn clean install -f ${folderpath}'
  		    }
  		}
  		stage("Deploy") {
  			steps {
				echo "Current - ${currentBuild.result}"
  			}
  		}
	}
}
