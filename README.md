# Lambda with Quarkus

## Setup local environment

### Start localstack
```sh
LOCALSTACK_LAMBDA_DOCKER_FLAGS="-e DYNAMODB_ENDPOINT=http://localhost.localstack.cloud:4566" DEBUG=1 localstack start
```

### Create DynamoDB table
```sh
TABLE_NAME=ServerlessTableLocalStack
aws dynamodb create-table \
    --endpoint-url http://localhost:4566 \
    --table-name $TABLE_NAME \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
        AttributeName=itemType,AttributeType=S \
        AttributeName=createdAt,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH AttributeName=itemType,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=1,WriteCapacityUnits=1 \
    --table-class STANDARD \
    --global-secondary-indexes \
        'IndexName=type_index,KeySchema=[{AttributeName=itemType,KeyType=HASH},{AttributeName=createdAt,KeyType=RANGE}],Projection={ProjectionType=ALL},ProvisionedThroughput={ReadCapacityUnits=1,WriteCapacityUnits=1}'
```

## Quarkus DEV mode
```sh
quarkus build --no-tests
export DYNAMODB_ENDPOINT=http://localhost:4566 TABLE_NAME=ServerlessTableLocalStack && quarkus dev
```

## Test with Quarkus DEV
```sh
API_GW_URL=http://localhost:8080/students
curl -XPOST "$API_GW_URL" --data '{"fullName":"Little John", "age":18}' --header 'Content-Type: application/json'
curl -XGET "$API_GW_URL" | jq '.'
```

# Localstack

### Create native image in a different output directory
```xml
<build>
    <directory>target-native</directory>
</build>
```

### Add @RegisterForReflection to Student and Students

## Build
```sh
quarkus build --no-tests
quarkus build --native --no-tests -Dquarkus.native.container-build=true
```

### Create the templates

### SAM build & deploy
```sh
sam build
samlocal deploy
```

## Test

### JVM
```sh
API_GW_URL=$(
    aws cloudformation describe-stacks \
    --endpoint-url http://localhost:4566 \
    --stack-name sam-app \
    | jq -r '.Stacks[0].Outputs[] | select(.OutputKey == "LocalStackLambdaWithQuarkusJvmApi").OutputValue')
curl -XPOST "$API_GW_URL" --data '{"fullName":"Little John - JVM", "age":18}' --header 'Content-Type: application/json'

curl -XGET "$API_GW_URL" | jq '.'
```

### Native
```sh
API_GW_URL=$(
    aws cloudformation describe-stacks \
    --endpoint-url http://localhost:4566 \
    --stack-name sam-app \
    | jq -r '.Stacks[0].Outputs[] | select(.OutputKey == "LocalStackLambdaWithQuarkusNativeApi").OutputValue')
curl -XPOST "$API_GW_URL" --data '{"fullName":"Little John - Native", "age":18}' --header 'Content-Type: application/json'

curl -XGET "$API_GW_URL" | jq '.'
```

# AWS

## Build

```sh
quarkus build --no-tests
quarkus build --native --no-tests -Dquarkus.native.container-build=true

sam build
```

## Deploy
```sh
sam deploy
```

## Test

### JVM
```sh
API_GW_URL=$(
    aws cloudformation describe-stacks \
    --stack-name sam-app \
    | jq -r '.Stacks[0].Outputs[] | select(.OutputKey == "LambdaWithQuarkusJvmApi").OutputValue')

echo $API_GW_URL

curl -XPOST "$API_GW_URL" --data '{"fullName":"Little John - JVM", "age":18}' --header 'Content-Type: application/json' | jq '.'

curl -XGET "$API_GW_URL" | jq '.'
```

### Native
```sh
API_GW_URL=$(
    aws cloudformation describe-stacks \
    --stack-name sam-app \
    | jq -r '.Stacks[0].Outputs[] | select(.OutputKey == "LambdaWithQuarkusNativeApi").OutputValue')

echo $API_GW_URL

curl -XPOST "$API_GW_URL" --data '{"fullName":"Little John - Native", "age":18}' --header 'Content-Type: application/json' | jq '.'

curl -XGET "$API_GW_URL" | jq '.'
```



## Reset functions
### JVM
```sh
NEW_ENVVARS=$(aws lambda get-function-configuration --function-name lambda-with-quarkus-jvm --query "Environment.Variables | merge(@, \`{\"LAST_RUN\":\"$(date)\"}\`)")
aws lambda update-function-configuration \
    --region us-east-1 \
    --function-name lambda-with-quarkus-jvm \
    --environment "{ \"Variables\": $NEW_ENVVARS }" > /dev/null
```

### Native

```sh
NEW_ENVVARS=$(aws lambda get-function-configuration --function-name lambda-with-quarkus-jvm --query "Environment.Variables | merge(@, \`{\"LAST_RUN\":\"$(date)\"}\`)")
aws lambda update-function-configuration \
    --region us-east-1 \
    --function-name lambda-with-quarkus-native \
    --environment "{ \"Variables\": $NEW_ENVVARS }" > /dev/null
```

## Run the benchmark

### JVM
```sh
API_GW_URL=$(
    aws cloudformation describe-stacks \
    --stack-name sam-app \
    | jq -r '.Stacks[0].Outputs[] | select(.OutputKey == "LambdaWithQuarkusJvmApi").OutputValue')
echo $API_GW_URL
```

### Native
```sh
API_GW_URL=$(
    aws cloudformation describe-stacks \
    --stack-name sam-app \
    | jq -r '.Stacks[0].Outputs[] | select(.OutputKey == "LambdaWithQuarkusNativeApi").OutputValue')
echo $API_GW_URL
```

### Artillery
```sh
docker run --rm -it -v ${PWD}/artillery:/scripts --network host \
  artilleryio/artillery:latest \
  run -t "$API_GW_URL" /scripts/test_script.yaml
```


### Log insights
```sql
 filter @type = "REPORT"
    | parse @log /\d+:\/aws\/lambda\/(?<function>.*)/
    | stats
    count(*) as invocations,
    pct(@duration+coalesce(@initDuration,0), 0) as p0,
    pct(@duration+coalesce(@initDuration,0), 25) as p25,
    pct(@duration+coalesce(@initDuration,0), 50) as p50,
    pct(@duration+coalesce(@initDuration,0), 75) as p75,
    pct(@duration+coalesce(@initDuration,0), 90) as p90,
    pct(@duration+coalesce(@initDuration,0), 95) as p95,
    pct(@duration+coalesce(@initDuration,0), 99) as p99,
    pct(@duration+coalesce(@initDuration,0), 100) as p100
    group by function, ispresent(@initDuration) as coldstart
    | sort by coldstart, function
```