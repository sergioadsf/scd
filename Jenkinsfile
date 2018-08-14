node {
	
    	//Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
	def IMAGE = readMavenPom().getArtifactId()
	def VERSION = readMavenPom().getVersion()
	def folderpath = '/home/sergio/Downloads/teste'
  	
   	
	//agent any
	
		stage('Example') {			
                echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
                echo "DEPLOY_ENV = ${DEPLOY_ENV}"
                echo "VALID_ENV = ${VALID_ENV}"
                echo "CHOICE_ENV = ${CHOICE_ENV}"
                
                echo "IMAGE ${IMAGE}"
                echo "VERSION ${VERSION}"
                echo "BUILD_ID ${env.BUILD_ID}"
                echo "JAVA_HOME ${env.JAVA_HOME}/bin:${env.PATH}"
				echo "JAVA_HOME ${env.MAVEN_HOME}"
		    if(${CHOICE_ENV} == "Test"){
			echo 'I only execute test'    
		    }else {
			echo 'I am not running test'    
		    }
        }
		stage("Build") {
    				sh 'git clone https://github.com/sergioadsf/scd.git ${folderpath}'
  		}
  		stage("Test") {
				sh 'mvn clean install -f ${folderpath}'
  		}
  		stage("Deploy") {
				echo "Current - ${currentBuild.result}"
  		}
}
