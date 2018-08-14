import groovy.json.JsonSlurper
pipeline {
	environment {
		IMAGE = readMavenPom().getArtifactId()
    	VERSION = readMavenPom().getVersion()
    	folderpath = '/home/sergio/Downloads/teste'
    	str = '{"id":"12345678","name":"Sharon","email":"sharonexample.com"}'
		//slurper = new JsonSlurper().parseText(str)
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
					if(CHOICE_ENV == "TEST"){
						echo 'I only execute test'    
				    }else {
						echo 'I am not running test'    
				    }				    
				}
		    
            }
        }
        stage('Example2'){
                input {
                	message "Should we continue?"
                	ok "Yes, we should."
                	submitter "alice,bob"
                	parameters {
                    string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')
                	}
            	}
        		steps {
            		echo "Hello, ${PERSON}, nice to meet you."
        		}
                
        }
		stage("Build") {
			steps {
				script{
					if (fileExists(folderpath)) {
						sh 'rm -rf ${folderpath}'
						echo 'Yes'
					} else {
					    echo 'No'
					}
				}
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
				echo "Subindo aplicação"
				sh 'java -jar ${folderpath}/target/${IMAGE}-${VERSION}.jar &'
  			}
  		}
	}
	
	post { 
        always { 
            echo 'I will always say Hello again!'
        }
    }
}
