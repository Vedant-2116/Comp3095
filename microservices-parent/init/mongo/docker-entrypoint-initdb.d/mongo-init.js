// log the start if the script execution
print('Start');

// switch to the 'product-service' database (or create it if it doesn't exist)
db = db.getSiblingDB('product-service');

db.creatUser(
    {
    user : "rootadmin",
    pwd : "password",
    roles : [{role : "readWrite", db : "product-service"}],
    }
);

// create a new collection 'user' within the 'product-service' database
db.creatCollection('user');

print("End");