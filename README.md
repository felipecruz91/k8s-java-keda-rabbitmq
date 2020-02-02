# RabbitMQ consumer and sender

A simple docker container that will receive messages from a RabbitMQ queue and scale via KEDA. The receiver will receive a single message at a time (per instance), and sleep for 10 ms to simulate performing work. When adding a massive amount of queue messages, KEDA will drive the container to scale out according to the event source (RabbitMQ).

## Pre-requisites

* Kubernetes cluster
* [KEDA installed](https://github.com/kedacore/keda#setup) on the cluster

## Setup

This setup will go through creating a RabbitMQ queue on the cluster and deploying this consumer with the `ScaledObject` to scale via KEDA.  If you already have RabbitMQ you can use your existing queues.

First you should clone the project:

```cli
git clone https://github.com/felipecruz91/k8s-java-keda-rabbitmq
cd k8s-java-keda-rabbitmq
```

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

You should see `rabbitmq-consumer` deployment with 0 pods as there currently aren't any queue messages.  It is scale to zero.

```
NAME                DESIRED   CURRENT   UP-TO-DATE   AVAILABLE   AGE
rabbitmq-consumer   0         0         0            0           3s
```

[This consumer](https://github.com/felipecruz91/k8s-java-keda-rabbitmq/blob/master/queue-consumer/src/main/java/com/example/messagingrabbitmq/Receiver.java) is set to consume one message per instance, sleep for 10 ms, and then acknowledge completion of the message.  This is used to simulate work.  The [`ScaledObject` included in the above deployment](deploy/deploy-consumer.yaml) is set to scale to a minimum of 0 replicas on no events, and up to a maximum of 15 replicas on heavy events (optimizing for a queue length of 5 message per replica).  After 30 seconds of no events the replicas will be scaled down (cooldown period).  These settings can be changed on the `ScaledObject` as needed.

### Publishing messages to the queue

#### Deploy the publisher job

The following job will publish 100K messages to the "spring-boot" queue the deployment is listening to. As the queue builds up, KEDA will help the horizontal pod autoscaler add more and more pods until the queue is drained after about 2 minutes and up to 15 concurrent pods.

```cli
kubectl apply -f deploy/deploy-publisher-job.yaml
```

#### Validate the deployment scales
```cli
kubectl get deploy -w
```

You can watch the pods spin up and start to process queue messages.  As the message length continues to increase, more pods will be pro-actively added.  

You can see the number of messages vs the target per pod as well:
```cli
kubectl get hpa
```

After the queue is empty and the specified cooldown period (a property of the `ScaledObject`, default of 300 seconds) the last replica will scale back down to zero.

## Cleanup resources

```cli
kubectl delete job rabbitmq-publisher
kubectl delete ScaledObject rabbitmq-consumer
kubectl delete deploy rabbitmq-consumer
helm delete rabbitmq
```