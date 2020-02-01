# k8s-java-keda-rabbitmq

## Install RabbitMQ via Helm 3

```shell
$ helm install rabbitmq --set rabbitmq.username=user,rabbitmq.password=PASSWORD,volumePermissions.enabled=true stable/rabbitmq

NAME: rabbitmq
LAST DEPLOYED: Sat Feb  1 16:52:16 2020
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
** Please be patient while the chart is being deployed **

Credentials:

    Username      : user
    echo "Password      : $(kubectl get secret --namespace default rabbitmq -o jsonpath="{.data.rabbitmq-password}" | base64 --decode)"
    echo "ErLang Cookie : $(kubectl get secret --namespace default rabbitmq -o jsonpath="{.data.rabbitmq-erlang-cookie}" | base64 --decode)"

RabbitMQ can be accessed within the cluster on port  at rabbitmq.default.svc.cluster.local

To access for outside the cluster, perform the following steps:

To Access the RabbitMQ AMQP port:

    kubectl port-forward --namespace default svc/rabbitmq 5672:5672
    echo "URL : amqp://127.0.0.1:5672/"

To Access the RabbitMQ Management interface:

    kubectl port-forward --namespace default svc/rabbitmq 15672:15672
    echo "URL : http://127.0.0.1:15672/"

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