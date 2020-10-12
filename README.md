# awspring.io Infrastructure

Infrastructure needed to run [awspring.io](https://awspring.io) website and other domains built with AWS CDK.

It is a [Maven](https://maven.apache.org/) based project, so you can open this project with any Maven compatible Java IDE to build and run tests.

## Useful commands

 * `make build`      runs formatter and builds the project
 * `make deploy`     deploys the CloudFormation stack

CDK commands you may also need:

 * `cdk ls`          list all stacks in the app
 * `cdk synth`       emits the synthesized CloudFormation template
 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk docs`        open CDK documentation
