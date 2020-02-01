# k8s-java-keda-rabbitmq

## Install RabbitMQ via Helm 3

```shell
$ helm install rabbitmq --set rabbitmq.username=user,rabbitmq.password=PASSWORD,volumePermissions.enabled=true stable/rabbitmq
```

Wait for RabbitMQ to deploy
⚠️ Be sure to wait until the deployment has completed before continuing. ⚠️

```shell
$ kubectl get pods

NAME         READY   STATUS    RESTARTS   AGE
rabbitmq-0   1/1     Running   0          3m3s
```

### Deploying a RabbitMQ consumer

#### Deploy a consumer
```cli
kubectl apply -f deploy/deploy-consumer.yaml
```

#### Validate the consumer has deployed
```cli
kubectl get deploy
```

### Publishing messages to the queue

#### Deploy the publisher job

The following job will publish messages indefinitely to the "spring-boot" queue the deployment is listening to. As the queue builds up, KEDA will help the horizontal pod autoscaler add more and more pods until the queue is drained after about 2 minutes and up to 30 concurrent pods.

```cli
kubectl apply -f deploy/deploy-publisher-job.yaml
```

#### Validate the deployment scales
```cli
kubectl get deploy -w
```

## Cleanup resources

```cli
kubectl delete job publisher
kubectl delete ScaledObject rabbitmq-consumer
kubectl delete deploy rabbitmq-consumer
helm delete rabbitmq
```