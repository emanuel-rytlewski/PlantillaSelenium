#!/usr/bin/env groovy
import java.math.RoundingMode

def getRepoURL() {
  sh "git config --get remote.origin.url > .git/remote-url"
  return readFile(".git/remote-url").trim()
}

def getCommitSha() {
  sh "git rev-parse HEAD > .git/current-commit"
  return readFile(".git/current-commit").trim()
}

void waitForSeleniumSessionsToEnd(String host, String port) {
    timeout(time: 5, unit: 'MINUTES') {
        echo "Waiting for selenium sessions to end at http://${host}:${port}"
        while (isSeleniumSessionsActive(host, port)) {
            sleep(time: 2, unit: 'SECONDS')
        }
        echo "No more selenium sessions active at http://${host}:${port}"
    }
}

boolean isSeleniumSessionsActive(String host, String port) {
    sh(returnStatus: true,
            script: "(curl -sSL http://${host}:${port}/grid/api/sessions || true) | grep sessions") == 0
}

def updateGithubCommitStatus(build) {
  repoUrl = getRepoURL()
  commitSha = getCommitSha()

  step([
    $class: 'GitHubCommitStatusSetter',
    reposSource: [$class: "ManuallyEnteredRepositorySource", url: repoUrl],
    commitShaSource: [$class: "ManuallyEnteredShaSource", sha: commitSha],
    errorHandlers: [[$class: 'ShallowAnyErrorHandler']],
    statusResultSource: [
      $class: 'ConditionalStatusResultSource',
      results: [
        [$class: 'BetterThanOrEqualBuildResult', result: 'SUCCESS', state: 'SUCCESS', message: build.description],
        [$class: 'BetterThanOrEqualBuildResult', result: 'FAILURE', state: 'FAILURE', message: build.description],
        [$class: 'AnyBuildResult', state: 'FAILURE', message: 'Loophole']
      ]
    ]
  ])
}

def buildCause = currentBuild.getBuildCauses()[0]
def buildPrincipal = [type:"Unknown", name:""]
def summary
def failCount = 0
def totalCount = 0
def passRate = 0

if (buildCause._class ==~ /.+BranchEventCause/) {
  buildPrincipal = [type:"Github",name:buildCause.shortDescription]
}
else if (buildCause._class ==~ /.+TimerTriggerCause/) {
  buildPrincipal = [type:"Timer", name:"Timer event"]
}
else if (buildCause._class ==~ /.+UserIdCause/) {
  buildPrincipal = [type:"User", name:buildCause.userId]
}
if (buildCause._class ==~ /.+UpstreamCause/) {
    def buildUpstreamCause = currentBuild.getRawBuild().getCause(hudson.model.Cause.UpstreamCause)

    try {
        buildPrincipal = [type:"Upstream", name:buildUpstreamCause.getUpstreamCauses()[0].userId]
    } catch(Exception e) {
        echo 'The upstream build does not have UserId. It was triggered by the Timer.'
        buildPrincipal = [type:"Upstream", name:"Timer event"]
    }
}

def COLOR_MAP = ['SUCCESS': 'good', 'FAILURE': 'danger', 'UNSTABLE': 'warning', 'ABORTED': 'warning']


def action = null
int statusCode = 0

def kubernetesPod = """
---
apiVersion: v1
kind: Pod
metadata:
  name: zalenium-grid
  labels:
    role: grid
    app: zalenium-${UUID.randomUUID().toString().substring(0, 5)}
    chart: zalenium-3.141.59
    release: my-zalenium
    heritage: Helm
    app.kubernetes.io/name: zalenium
    helm.sh/chart: zalenium-3.141.59
    app.kubernetes.io/instance: my-zalenium
    app.kubernetes.io/managed-by: Helm
spec:
  securityContext: {}
  containers:
    -
      image: groovy:3.0.6-jdk11
      imagePullPolicy: IfNotPresent
      name: groovy
      resources:
        requests:
          cpu: 1.5
          memory: 2.5Gi
        limits:
          cpu: 4
          memory: 5Gi
      tty: true
    -
      name: zalenium
      image: dosel/zalenium:3.141.59z
      imagePullPolicy: IfNotPresent
      securityContext: {}
      ports:
        - containerPort: 4444
          protocol: TCP
      livenessProbe:
        httpGet:
          path: /grid/console
          port: 4444
        initialDelaySeconds: 90
        periodSeconds: 5
        timeoutSeconds: 1
      readinessProbe:
        httpGet:
          path: /grid/console
          port: 4444
        timeoutSeconds: 1
      env:
        - name: ZALENIUM_KUBERNETES_CPU_REQUEST
          value: "1000m"
        - name: ZALENIUM_KUBERNETES_CPU_LIMIT
          value: "3000m"
        - name: ZALENIUM_KUBERNETES_MEMORY_REQUEST
          value: "2.5Gi"
        - name: ZALENIUM_KUBERNETES_MEMORY_LIMIT
          value: "5Gi"
        - name: DESIRED_CONTAINERS
          value: "30"
        - name: MAX_DOCKER_SELENIUM_CONTAINERS
          value: "50"
        - name: SELENIUM_IMAGE_NAME
          value: "elgalu/selenium:latest"
        - name: VIDEO_RECORDING_ENABLED
          value: "true"
        - name: SCREEN_WIDTH
          value: "1920"
        - name: SCREEN_HEIGHT
          value: "1080"
        - name: NEW_SESSION_WAIT_TIMEOUT
          value: "240000"
        - name: DEBUG_ENABLED
          value: "false"
        - name: TZ
          value: "UTC"
        - name: KEEP_ONLY_FAILED_TESTS
          value: "false"
      args:
        - start
      resources:
        requests:
          cpu: 1
          memory: 600Mi
        limits:
          cpu: 3
          memory: 1024Mi
      volumeMounts:
        - name: my-zalenium-videos
          mountPath: /home/seluser/videos
        - name: my-zalenium-data
          mountPath: /tmp/mounted
  terminationGracePeriodSeconds: 1
  serviceAccount: "jenkins"
  volumes:
    - name: my-zalenium-videos
      emptyDir: {}
    - name: my-zalenium-data
      emptyDir: {}
    - name: jenkins-docker-cfg
      projected:
        sources:
        - secret:
            name: general-dockerconfig
            items:
              - key: .dockerconfigjson
                path: config.json
"""

def jobName = ''
def startedByMessage = ''
def buildNumberMessage = ''

pipeline {
  options {
    ansiColor('xterm')
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '50', artifactNumToKeepStr: '50'))
    timeout(time: 30, unit: 'MINUTES')
  }

  agent {
    kubernetes {
      label "selenium-grid-${UUID.randomUUID().toString().substring(0, 5)}"
      yamlMergeStrategy merge()
      yaml kubernetesPod
    }
  }

  parameters {
    string(name: 'browserName', defaultValue: params.browserName ?:'chrome', description: 'Browser name: chrome or firefox')
    string(name: 'threads', defaultValue: params.threads ?:'30', description: 'Number of threads to execute the parallel tests.')
    string(name: 'environment', defaultValue: params.environment ?:'prod', description: 'The environment to run the tests')
    string(name: 'testGroups', defaultValue: params.testGroups ?:'full-regression', description: 'The test groups to execute. Default value: full-regression')
    string(name: 'excludeGroups', defaultValue: params.excludeGroups ?:'onlyPreprod', description: 'The test groups to exclude. Default value: onlyPreprod')
    string(name: 'slackChannels', defaultValue: params.slackChannels ?:'#qa', description: 'Slack channels to send build notifications. Multiple channels may be provided as a comma, semicolon, or space delimited string. Example: #qa #release')
    string(name: 'extraFailureSlackChannels', defaultValue: params.extraFailureSlackChannels ?:'', description: 'Extra Slack channels where to send build notifications in case of failure only. Multiple channels may be provided as a comma, semicolon, or space delimited string. Example: #qa #release')
    string(name: 'additionalGradleArguments', defaultValue: params.additionalGradleArguments ?:'',
            description: 'Additional command line arguments, for example to launch a single test: --tests ClassName.testMethod')
    booleanParam(name: 'staticAnalysis', defaultValue: params.staticAnalysis ?:false, description: 'Enable the static analysis performed with checkstyle and Sonarqube')
    booleanParam(name: 'forceTestExecution', defaultValue: params.forceTestExecution ?:false, description: 'Force the tests to be executed when the branch is not master')
  }

  stages {
    stage('Start Gradle Daemon') {
      steps {
        script {
          jobName = env.JOB_NAME
          startedByMessage = "\nStarted by: ${buildPrincipal.type} - ${buildPrincipal.name}"
          buildNumberMessage = "\nBuild number: ${env.BUILD_NUMBER}"

          intialMessage = "Starting job *" + jobName + "*" +
                    buildNumberMessage +
                    startedByMessage +
                    "\n(<${env.BUILD_URL}|Link to the build>)"

          slackSend(
                  channel: params.slackChannels,
                  color: '#4682B4',
                  message: intialMessage
          )
        }
        container('groovy') {
          sh './gradlew --daemon' // start gradle daemon
        }
      }
    }
    stage('Sonarqube') {
      when {
        anyOf {
          expression { params.staticAnalysis == true }
          changeRequest target: 'master'
        }
      }
      steps {
        container('groovy') {
          sh './gradlew compileJava'
          script {
            sonarqubeRunner { generateBranchPRConfig = true }
          }
        }
      }
    }
    stage('Tests') {
      when{
        anyOf {
          expression { params.forceTestExecution == true }
        }
      }
      steps {
        catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
          container('groovy') {
            script {
              statusCode = sh(script:"./gradlew test -Denvironment=${params.environment} -Dthreads=${params.threads} " +
                      "-Dbrowser=${params.browserName} -DgridURL=http://127.0.0.1:4444/wd/hub " +
                      "-Dgroups=${params.testGroups} -DexcludeGroups=${params.excludeGroups} " +
                      "${params.additionalGradleArguments} -Duser.timezone=Europe/Paris", returnStatus:true)
            }
          }
        }
      }
      post {
        always {
          container('groovy') {
            // copy extent report to ${workspace}/ExtentReport folder and give all permissions
            sh "cp -r ./build/extent/HtmlReport ${workspace}/ExtentReport && chmod -R 777 ${workspace}/ExtentReport"

            // copy junit reports to ${workspace}/junitreports folder and give all permissions
            sh "cp -r ./build/reports/tests/test/junitreports ${workspace}/junitreports && chmod -R 777 ${workspace}/junitreports"
          }
          container('zalenium') {
            // Wait for Selenium sessions to end (i.e. videos to be processed and copied)
            waitForSeleniumSessionsToEnd('127.0.0.1', "4444")
            // copy videos to ${workspace}/ExtentReport folder
            sh "cp /home/seluser/videos/*.mp4 ${workspace}/ExtentReport"
          }
          publishHTML target: [
                  allowMissing: true,
                  alwaysLinkToLastBuild: true,
                  keepAll: true,
                  reportDir: 'ExtentReport',
                  reportFiles: 'ExtentHtml.html',
                  reportName: 'Test Report'
          ]

          archiveArtifacts artifacts: 'build/logs/**/*.log', fingerprint: true, onlyIfSuccessful: false
          junit 'build/reports/tests/test/junitreports/**/*.xml'
          archiveArtifacts artifacts: 'build/reports/tests/test/junitreports/**/*.xml', fingerprint: true, onlyIfSuccessful: false
        }
      }
    }
  }
  post {
    always {
      script {
        if (statusCode!=0) {
          currentBuild.result = 'FAILURE'
        }
      }
      updateGithubCommitStatus(currentBuild)
      script {
        if (currentBuild.currentResult == 'SUCCESS' || currentBuild.currentResult == 'FAILURE') {
          if (params.forceTestExecution == true) {
            summary = junit(testResults: 'build/reports/tests/test/junitreports/**/*.xml')
            failCount = summary.failCount
            totalCount = summary.totalCount - summary.skipCount
            passRate = ((summary.passCount / totalCount) * 100).setScale(2, RoundingMode.FLOOR)

            //Writing the variables value failCount, totalCount and passRate in file summaryFile.txt
            writeFile(file: 'summaryFile.txt', text: totalCount + " " + failCount + " " + passRate)

            archiveArtifacts artifacts: 'summaryFile.txt', fingerprint: true, onlyIfSuccessful: false
          }
        }
      }
      updateGithubCommitStatus(currentBuild)
      script {
        // send a message if aborted (the message after the pipeline is not executed)
        if (currentBuild.currentResult == 'ABORTED') {
          action = currentBuild.rawBuild.getAction(jenkins.metrics.impl.TimeInQueueAction.class)

          abortedMessage = "The job *" + jobName + "* was aborted" +
                    buildNumberMessage +
                    startedByMessage +
                    "\n(<${env.BUILD_URL}|Link to aborted build>)"
          print "A slack message was ignored"
          slackSend(
                  channel: params.slackChannels,
                  color: COLOR_MAP[currentBuild.currentResult],
                  message: abortedMessage
          )
        }
      }
    }
  }
}

def postAction = currentBuild.rawBuild.getAction(jenkins.metrics.impl.TimeInQueueAction.class)
def finalMessage
def executionTimeMessage = "\nThis build spent *${postAction.getQueuingTimeString()}* waiting and *${postAction.getExecutingTimeString()}* running"
def numberOfTestsMessage = "\nThere were ${failCount} failed test from a total of ${totalCount}. The pass rate was ${passRate}%"
def testReportMessage = "\n(<${env.BUILD_URL}/Test_20Report/|Link to the test report>)"

if(currentBuild.result == 'FAILURE') { // Failed
  finalMessage = "The job *" + jobName + "* has failed"
} else { // Successful
  finalMessage = "The job *" + jobName + "* was executed successfully"
}

finalMessage = finalMessage +
                buildNumberMessage +
                startedByMessage +
                executionTimeMessage +
                numberOfTestsMessage +
                testReportMessage

slackSend (
  channel: params.slackChannels,
  color: COLOR_MAP[currentBuild.currentResult],
  message: finalMessage
)

if(currentBuild.result == 'FAILURE' && params.extraFailureSlackChannels != '') { // Only in case of failure and if param is defined
  slackSend (
    channel: params.extraFailureSlackChannels,
    color: COLOR_MAP[currentBuild.currentResult],
    message: finalMessage
  )
}