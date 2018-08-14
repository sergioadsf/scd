import groovy.json.JsonSlurper
node {
	
    	//Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
	def IMAGE = readMavenPom().getArtifactId()
	def VERSION = readMavenPom().getVersion()
	def folderpath = '/home/sergio/Downloads/teste'
	//def choice_env = ${CHOICE_ENV}
	def str = '{"id":"12345678","name":"Sharon","email":"sharon\u0040example.com"}'
	def slurper = new JsonSlurper().parseText(str)
   	
	//agent any
	stages{
	    

		stage('Example') {			
                echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
                echo "DEPLOY_ENV = ${DEPLOY_ENV}"
                echo "VALID_ENV = ${VALID_ENV}"
                echo "CHOICE_ENV = ${CHOICE_ENV}"
                
                echo "IMAGE ${IMAGE}"
                echo "VERSION ${VERSION}"
			echo "BUILD_ID ${env.BUILD_ID} ${slurper.id}"
                echo "JAVA_HOME ${env.JAVA_HOME}/bin:${env.PATH}"
				echo "JAVA_HOME ${env.MAVEN_HOME}"
		    if(CHOICE_ENV == "12345678"){
			echo 'I only execute test'    
		    }else {
			echo 'I am not running test'    
		    }
        }
		stage("Build") {
			echo slurper.name
    				//sh 'git clone https://github.com/sergioadsf/scd.git ${folderpath}'
  		}
  		stage("Test") {
				//sh 'mvn clean install -f ${folderpath}'
  		}
  		stage("Deploy") {
				//echo "Current - ${currentBuild.result}"
  		}
  		}
}
