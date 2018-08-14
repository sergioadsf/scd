import groovy.json.JsonSlurperClassic


	def sendGETRequest(curl) {
	  	sh "${curl} -o output.json"
	
	  	def workspace = pwd()		
	  	def json = readFile("output.json")
		def data = new JsonSlurperClassic().parseText(json)
		echo "Versão do planejamento ${data.version.name}"

  		return data;
	}

pipeline {
	
	environment {
	redmine_url = "http://demo.redmine.org/versions/16534.json"		
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
		  sendGETRequest("curl -X GET \"http://demo.redmine.org/versions/16534.json\"");
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
  			input {
				  message 'Teste? '
				  ok 'Permitir'
				  submitter 'sergioadsf'
				  submitterParameter 'VALID_CHECK'
				  parameters {
				    booleanParam defaultValue: true, description: 'Fazer deploy em Teste?', name: 'validar'
				  }
				}
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
