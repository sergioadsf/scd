pipeline {
	agent any

	stages {
		stage('Example') {
            steps {
                echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
                echo "DEPLOY_ENV = ${DEPLOY_ENV}"
                echo "VALID_ENV = ${VALID_ENV}"
                echo "CHOICE_ENV = ${CHOICE_ENV}"
                pom = readMavenPom file: 'pom.xml'
                echo "Pom ${pom.version}"
            }
        }
		stage("Build") {
			steps {
    			sh 'make'
			}
  		}
  		stage("Test") {
  		    steps {
				sh 'make check || true'
  		    }
  		}
  		stage("Deploy") {
  			steps {
				when {
	              expression {
	                currentBuild.result == null || currentBuild.result == 'SUCCESS' 
	              }
	            }
            	steps {
                	sh 'make publish'
            	}		    
  			}
  		}
	}
}
