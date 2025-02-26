# Vezbe Backend

Primer projekta rađen na vežbama iz Softverskog inženjerstva.

## Pokretanje

```shell
docker compose --profile user-service up --build
```

## Korisne komande

```shell
# Logovanje
curl -X POST -H "Content-Type: application/json" -d '{"username":"admin","password":"admin"}' http://localhost:8080/auth/login
TOKEN=$(curl --silent -X POST -H "Content-Type: application/json" -d '{"username":"admin","password":"admin"}' http://localhost:8080/auth/login | jq -r .jwt)

# Listanje svih korisnika
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api

# Dohvatanje korisnika
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/username/admin

# Kreiranje korisnika
curl -X POST -d '{"username":"student","password":"student","isAdmin":false,"imePrezime":"Student"}' -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" http://localhost:8080/api

# Izmena korisnika
curl -X POST -d '{"username":"student","password":"student123","isAdmin":true,"imePrezime":"Student Studentic"}' -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" http://localhost:8080/api
```
