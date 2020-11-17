
curl -X POST 'http://localhost:8080/api/v1/auth/sign-up' -H "Content-Type: application/json; charset=UTF-8" -d '{
"name" : "dejan",
"username": "dejanvuk",
"email": "none@email.com",
"password": "somepassword"
}'

curl -X POST 'http://localhost:8080/api/v1/auth/signin' -H "Content-Type: application/json; charset=UTF-8" -d '{
"usernameOrEmail": "dejanvuk",
"password": "somepassword"
}'

curl -X GET -k http://localhost:8080/api/v1/users/me -H "Content-Type: application/json" -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbGJhdHJvemE5NUB0dXRhbm90YS5kZSIsImlhdCI6MTYwMTI5MjU1NSwiZXhwIjoxNjAxMzU2NTU1LCJhdWQiOiJ1c2VycyIsImlzcyI6InVzZXItbWljcm9zZXJ2aWNlIn0.m9ZKBttBPK1qYkd4afI4y4uzdOGzMGT7Onvlzv2BNLm0Oco5zBpSp47X0ZCa466KFkUn5mhMAXNG84HfDVEdrg"

http://localhost:8080/api/v1/oauth2/authorization/facebook
http://localhost:8080/api/v1/oauth2/authorization/facebook?redirect_uri=http://localhost:3000

