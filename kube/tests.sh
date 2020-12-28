kubectl run aks-mongo-test --rm --tty -i --restart='Never' --image=mongo:4.0.21 --command -- mongo admin --host mongo-user --authenticationDatabase admin -u mongodb-user -p mongodb-password

use users-db

db.roles.insertOne({ name: "ROLE_ADMIN"})
db.roles.insertOne({ name: "ROLE_USER"})
