pipeline {
	agent any
	stages {
		stage('Example') {
            steps {
                echo "Running ${env.BUILD_ID} on ${env.JENKINS_URL}"
            }
        }
		stage("Build") {
			steps {
    			sh 'make'
    			archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
			}
  		}
  		stage("Test") {
  		    steps {
				sh 'make check || true'
				junit '**/target/*.xml'  		        
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
