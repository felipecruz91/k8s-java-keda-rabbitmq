docker run --rm --name rabbitmq -p 5672:5672 -p 15672:15672 --network=host rabbitmq:management

docker build -t webapp .

docker run --network=host webapp 