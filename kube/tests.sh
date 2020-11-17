kubectl run aks-mongo-test --rm --tty -i --restart='Never' --image=mongo:4.0.21 --command -- mongo admin --host <Mongo-service-cluster-ip> --authenticationDatabase admin -u mongodb-user -p mongodb-password

