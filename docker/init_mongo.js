db.createUser(
    {
        user: "admin",
        pwd: "admin",
        roles:[
            {
                role: "readWrite",
                db:   "Tinyx"
            }
        ]
    }
);
db.createCollection("Media");
db.createCollection("Users");
db.createCollection("Posts");
