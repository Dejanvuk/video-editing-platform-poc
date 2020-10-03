
curl -X POST 'http://localhost:8080/auth/sign-up' -H "Content-Type: application/json; charset=UTF-8" -d '{
"name" : "dejan",
"username": "dejanvuk",
"email": "none@email.com",
"password": "password321"
}'

curl -X POST 'http://localhost:8080/auth/signin' -H "Content-Type: application/json; charset=UTF-8" -d '{
"usernameOrEmail": "dejanvuk",
"password": "password321"
}'

curl -X GET -k http://localhost:8080/user/me -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbGJhdHJvemE5NUB0dXRhbm90YS5kZSIsImlhdCI6MTYwMTI5MjU1NSwiZXhwIjoxNjAxMzU2NTU1LCJhdWQiOiJ1c2VycyIsImlzcyI6InVzZXItbWljcm9zZXJ2aWNlIn0.m9ZKBttBPK1qYkd4afI4y4uzdOGzMGT7Onvlzv2BNLm0Oco5zBpSp47X0ZCa466KFkUn5mhMAXNG84HfDVEdrg"

http://localhost:8080/oauth2/authorization/facebook
http://localhost:8080/oauth2/authorization/facebook?redirect_uri=http://localhost:3000

