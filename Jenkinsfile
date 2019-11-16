def Application_deployed_url=""
pipeline {
    agent { label 'JenkinsBuildServer' }
     environment {
       JAVA_HOME="${tool 'JDK-1.8'}"
       MAVEN_HOME="${tool 'MavenHome'}"
       build_artifact_version=""
         }
    
    stages{
        
      stage('ServiceNow-ChangeTicket Created'){
       steps{ 
           script{
          SUBJECT= "Hellowrold Job with Build No: "+BUILD_NUMBER+" Initiated"
          build_description=" Hi Team , \n \n" + SUBJECT + " \n Please  find the BUILD URL: "+ BUILD_URL + "\n JOb Name: " + JOB_NAME  +"\n \n Thanks, \n Wipro DevOps Team"
          println build_description
          def request= ChangeRequest assignedTo:'Ansible Integrations',category:'Other',ci:'AS400',impact:'3 - Low',fullDescription: build_description,Description: build_description,priority:'4 - Low',requestedBy:'Ansible Integrations',risk:'Moderate',shortDescription:SUBJECT,state:'New',type:'Standard'
         //createChangeRequest changeRequest: request
           }
        }
     }
     /*
       stage('JIRA'){
            steps{ 
                script{
                    def testIssue = [fields: [ project: [key: 'HOTELAPP'],
                               summary: "The $JOB_NAME with Build Number $BUILD_DISPLAY_NAME Trigger " ,
                               description: "Hi Team \n\n The $JOB_NAME with Build Number $BUILD_DISPLAY_NAME Triggered \n The job $BUILD_DISPLAY_NAME URL \n $BUILD_URL \n\n Thanks,\n DevOps Team" ,
                               issuetype: [name: 'Task']]]
                    def response = jiraNewIssue issue: testIssue,  site: 'DevLiteJIRA'
                    println response.successful.toString()
                    println response.data.toString()
                }
            } 
        } */
        
     stage('Github-Code Checkout'){
        steps{
            git branch: '$GitBranch', credentialsId: 'Basha-GIT', url: '$Gitcodeurl'
        }
    }
    stage('Maven- Code Build'){
     steps{
        sh '''
	    PATH=${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}
        java -version
		mvn -version
        mvn clean package --batch-mode
		mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.version -l version.log  -Dhttps.protocols=TLSv1.2 --batch-mode
		'''
		sh  'grep -v "\\["  version.log > ansible_build.properties'
		sh 'cat ansible_build.properties'
     }
    }
    stage('Sonarqube- Code Quality Analysis'){
      steps{
        sh '''
        JAVA_HOME=${JAVA8}
        PATH=${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}
        java -version
		mvn -version
        mvn sonar:sonar --batch-mode -Dsonar.host.url=http://3.209.53.193:9000  -Dhttps.protocols=TLSv1.2 
        '''
         }
    } 
     stage('Jfrog- Upload Application Binaries '){
      steps{
        sh '''
         PATH=${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}
        mvn deploy -Dartifact_url=3.209.53.193 --batch-mode  -Dhttps.protocols=TLSv1.2
        '''
         }
   }
  
   
    stage("Fortify- Code Security Scan "){
       steps{
                sh '''
                cd $WORKSPACE
                PATH=${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}:/opt/Fortify/Fortify_SCA_and_Apps_18.20/bin
                mvn com.fortify.sca.plugins.maven:sca-maven-plugin:18.20:clean -Dhttps.protocols=TLSv1.2 --batch-mode
                /opt/Fortify/Fortify_SCA_and_Apps_18.20/bin/sourceanalyzer -b TheBookie mvn package -Dhttps.protocols=TLSv1.2 --batch-mode
                /opt/Fortify/Fortify_SCA_and_Apps_18.20/bin/sourceanalyzer -b TheBookie  -scan -f HelloWorld.fpr
                /opt/Fortify/Fortify_SCA_and_Apps_18.20/bin/BIRTReportGenerator  -template  "DISA STIG" -source HelloWorld.fpr -output HelloWorld_Fortify_Report.pdf -format PDF -showSuppressed --Version "DISA STIG 3.9" -UseFortifyPriorityOrder
                '''
           }
    } 

    
   stage('Ansible- App Binary Deploy'){
         environment{
           build_artifact_version=readFile('ansible_build.properties').trim()
       }
       steps{
       script{
            ansible_output=ansibleTower credential: '', extraVars:'''
            artifact_version: ${build_artifact_version}  
            instance_name: ${instance_name} 
            dev_instance_count: ${DevInstances}
            test_instance_count: ${TestInstances}
            prod_instance_count: ${ProdInstances}''' ,async: false, importTowerLogs: true, importWorkflowChildLogs: true, inventory: '', jobTags: '', jobTemplate: 'VMCloudbeesDeploy_HelloWorld', jobType: 'run', limit: '', removeColor: true, skipJobTags: '', templateType: 'workflow', throwExceptionWhenFail: true, towerServer: 'AnsibleTower', verbose: true
            println ansible_output.Application_END_URL
            Application_deployed_url=ansible_output.Application_END_URL
       } 
       }
      }  
  }


post {
        always {
            script {
                if (currentBuild.currentResult == 'FAILURE') { // Other values: SUCCESS, UNSTABLE
                    
                    script{
                            SUBJECT= 'The Hellworld App - Build Status: $BUILD_DISPLAY_NAME has Failed'
             build_description=" Hi Team , \n " + SUBJECT + " \n Please  find the BUILD URL: "+ BUILD_URL + "\n JOb Name: " + JOB_NAME  +"\n Thanks, \n Wipro DevOps Team"
             println build_description
            // def request = ChangeRequest assignedTo:'Ansible Integrations',category:'Other',ci:'AS400',impact:'3 - Low',fullDescription: build_description,priority:'4 - Low',Description: build_description, requestedBy:'Ansible Integrations',risk:'Moderate',shortDescription:SUBJECT,state:'New',type:'Standard'
             //createChangeRequest changeRequest: request
                       }
                    // Send an email only if the build status has changed from green/unstable to red
                    emailext subject: 'The HelloWorld App - Build Status: $BUILD_DISPLAY_NAME has Failed' ,
                      body: '''
                        Hi All,
   
    The Current Build $BUILD_DISPLAY_NAME is Failed.
    Please Find  the attached Build Logs: $BUILD_URL

        Please find the input parameter values:
        --------------
	instance_name	= $instance_name 
	Gitcodeurl 	= $Gitcodeurl
	GitBranch	= $GitBranch
	Language	= $Language  
	Languageversion	= $Languageversion	
	ServerType	= $ServerType	
	FieldType	= $FieldType	
	DevInstances	= $DevInstances	
	DTshirtsize	= $DTshirtsize	
	TestInstances	= $TestInstances
	TTshirtsize	= $TTshirtsize	
	ProdInstances	= $ProdInstances	
	MAILIDs		= $MAILIDs	
  Thanks,
  Devops Team ''' ,
                      // body: 'Hi All, \n The Current Build $BUILD_DISPLAY_NAME is Failed, Please Find  the attached Build Logs: $BUILD_URL \n \n Thanks, \n Devops Team ',
                        replyTo: 'no-reply@wipro-poc.com',
                        from:'no-reply@wipro-poc.com',
                      // to: 'shaik.basha35@wipro.com', //,geetha.16@wipro.com,syed.ahemed@wipro.com',
                        to: "$MAILIDs",
                        attachLog:'true',
                        attachmentsPattern:'*.pdf'
                } 
                else if (currentBuild.currentResult == 'SUCCESS') { // Other values: SUCCESS, UNSTABLE
                    // Send an email only if the build  http://18.139.219.62:8080/helloworld status has changed from green/unstable to red
                    emailext subject: 'The Helloworld App - Build Status: $BUILD_DISPLAY_NAME has Succeeded',
                       body: """
                         Hi All,
   
    The Current Build $BUILD_DISPLAY_NAME is Successful.
   
    Please find the Application URL :  $Application_deployed_url
    Please Find  the attached Build Logs: $BUILD_URL

        Please find the input parameter values:
        --------------
	instance_name	= $instance_name 
	Gitcodeurl 	= $Gitcodeurl
	GitBranch	= $GitBranch
	Language	= $Language  
	Languageversion	= $Languageversion	
	ServerType	= $ServerType	
	FieldType	= $FieldType	
	DevInstances	= $DevInstances	
	DTshirtsize	= $DTshirtsize	
	TestInstances	= $TestInstances
	TTshirtsize	= $TTshirtsize	
	ProdInstances	= $ProdInstances	
	MAILIDs		= $MAILIDs	
  Thanks,
  Devops Team """,
                       // body: "Hi All, \n       The Current Build $BUILD_DISPLAY_NAME is Successful. \n       Please Access your application at $Application_deployed_url \n \nPlease Find  the attached Build Logs: $BUILD_URL  \n \n Thanks, \n Devops Team",
                        replyTo: 'no-reply@wipro-poc.com',
                        from:'no-reply@wipro-poc.com',
                      //  to: 'shaik.basha35@wipro.com' ,//,geetha.16@wipro.com,syed.ahemed@wipro.com',
                       to: "$MAILIDs",
                        attachLog:'true',
                       attachmentsPattern:'*.pdf'
                }
                else if (currentBuild.currentResult == 'ABORTED') { 
                    
                        script{
                            JOb_name=""+JOB_NAME+" "+BUILD_NUMBER 
                           // def request = ChangeRequest shortDescription:"$JOb_name",type:'Standard',category:'Other',impact:'3 - Low',risk:'Moderate',priority:'4 - Low',ci:'AS400'
                         //   createChangeRequest changeRequest: request
                       }
                    // Send an email only if the build status has changed from green/unstable to red
                    emailext subject:'The hellworld App - Build Status: $BUILD_DISPLAY_NAME has Aborted',
                      body: '''
                       Hi All,
   
    The Current Build $BUILD_DISPLAY_NAME is Aborted.
    Please Find  the attached Build Logs: $BUILD_URL

        Please find the input parameter values
	instance_name	= $instance_name 
	Gitcodeurl 	= $Gitcodeurl
	Language	= $Language  
	Languageversion	= $Languageversion	
	ServerType	= $ServerType	
	FieldType	= $FieldType	
	DevInstances	= $DevInstances	
	DTshirtsize	= $DTshirtsize	
	TestInstances	= $TestInstances
	TTshirtsize	= $TTshirtsize	
	ProdInstances	= $ProdInstances	
	MAILIDs		= $MAILIDs	
  Thanks,
  Devops Team

''', 
                      
                      //  body: 'Hi All, \n The Current Build $BUILD_DISPLAY_NAME is ABORTED, Please Find  the attached Build Logs: $BUILD_URL \n \n Thanks, \n Devops Team ',
                        replyTo: 'no-reply@wipro-poc.com',
                        from:'no-reply@wipro-poc.com',
                        //to: 'shaik.basha35@wipro.com' ,//,geetha.16@wipro.com,syed.ahemed@wipro.com',
                         to: "$MAILIDs",
                        attachLog:'true',
                        attachmentsPattern:'*.pdf'
                }
            }
        }
    } 
 
}

