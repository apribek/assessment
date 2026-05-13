# Create payment
RESPONSE=$(curl -s -X POST http://localhost:8080/payments \
     -H "Content-Type: application/json" \
     -d '{
       "amount": 100.0,
       "currency": "USD",
       "debtorAccount": "ACC1",
       "creditorAccount": "ACC2"
     }')
PAYMENT_ID=$(echo $RESPONSE | jq -r '.id')
echo "Created Payment ID: $PAYMENT_ID"

curl -X GET http://localhost:8080/payments/$PAYMENT_ID
echo
curl -X PUT http://localhost:8080/payments/$PAYMENT_ID \
     -H "Content-Type: application/json" \
     -d "{
       \"id\": \"$PAYMENT_ID\",
       \"amount\": 200.0,
       \"currency\": \"USD\",
       \"debtorAccount\": \"ACC1\",
       \"creditorAccount\": \"ACC2\"
       \"status\": \"CREATED\"
     }"
echo
curl -X GET http://localhost:8080/payments/$PAYMENT_ID
echo
curl -X PUT http://localhost:8080/payments/$PAYMENT_ID \
     -H "Content-Type: application/json" \
     -d "{
       \"id\": \"$PAYMENT_ID\",
       \"amount\": 300.0,
       \"currency\": \"USD\",
       \"debtorAccount\": \"ACC1\",
       \"creditorAccount\": \"ACC2\",
       \"status\": \"COMPLETED\"
     }"
echo
curl -X GET http://localhost:8080/payments/$PAYMENT_ID

