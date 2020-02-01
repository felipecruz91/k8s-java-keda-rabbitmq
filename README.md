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